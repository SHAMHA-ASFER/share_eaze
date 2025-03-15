package com.share;

import java.io.*;
import java.sql.Date;
import java.util.*;
/*
 * Runnable interface allows programe to context switch
 * All clients information include messaging logic implemented here!
 */
public class HandleClient implements Runnable{
    /*
     * Client's informations references
     */
    private Client client;
    private ArrayList<Client> users;
    private boolean isServerRunning;
    /*
     * BufferedReader & PrintWriter both class playing key roll on
     * sending recieving data from esp32warroom32 & mobile application
     * in a same time.
     */
    private BufferedReader bufferedReader;  // Input from client    (mobile & arduino)
    private PrintWriter printWriter;        // Output for a client  (mobile & arduino)
    private Thread thread;
    private String[] args;

    // private boolean isCodeGenerated = false;

    /*
     * Decoding string for handle messages from clients
     * tok_del -> identifies request type
     * arg_del -> identifies arguments from request
     * con_del -> identifies concatenated records (token + arguments)
     * sep_del -> identifies with name for concatenated records
     */
    private static String tok_del = "<!!]";
    private static String tit_del = "<<<]";
    private static String arg_del = "<;;]";
    private static String con_del = "<::]";
    private static String sep_del = "<,,]";
    /*
     * Assign references from Server
     */
    public HandleClient(Client cli, ArrayList<Client> usrs, boolean isRunning, ArrayList<BORROWER_ACK> acks, ArrayList<RENT_COMPLETION> rents) {
        this.client = cli;
        this.users = usrs;
        this.isServerRunning = isRunning;
    }
    /*
     * References Client Handler's thread (context switch)
     */
    public void setThread(Thread t) {
        this.thread = t;
    }
    /*
     * Extract exact request from client request
     */
    public String getRequest(String req) {
        if (req != null) {
            return req.split(tok_del)[0];
        }
        return null;
    }
    /*
     * Extract exact arguments from client request
     */
    public String[] getArguments(String req) {
        if (req != null) {
            String[] r = req.split(tok_del);
            if (r.length > 1) {
                String[] a = r[1].split(arg_del);
                if (a.length > 0) {
                    return a;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public void run() {
        try {
            /*
             * Initialize BufferedReader & PrintWriter using client socket 
             */
            bufferedReader = new BufferedReader(new InputStreamReader(client.socket.getInputStream()));
            printWriter = new PrintWriter(client.socket.getOutputStream(), true);
            /*
             * Set platform info using bufferedReader
             * platform referes which module connect through interenet
             * ex: mobile, arduino, unknown
             */
            client.platform = Platform.fromString(bufferedReader.readLine());
            // System.out.println(client.platform);
            Database.STS_LOCKER.setUsers(users);
            while (isServerRunning) {
                /*
                 * Infinitly read request from clients 
                 */
                String request = bufferedReader.readLine();
                System.out.println(request);
                args = getArguments(request);
                request = getRequest(request);
                /*
                 * REJECT UNKNOWN CONNECTIONS
                 */
                if (client.platform == Platform.UNKNOWN) { 
                    break; 
                } else 
                /*
                 * MOBILE APPLICATION FUNCTIONALITY
                 */
                if (client.platform == Platform.MOBILE) {
                    // CREATE NEW USER ACCOUNT
                    if (request.equals(Request.CREATE_USER.getRequest())) {

                    } else 
                    // LOGIN USER ACCOUNT
                    if (request.equals(Request.LOGIN_USER.getRequest())) {
                        /*
                         * Using STS_USER table authenticate users
                         */
                        if (Database.STS_USER.auth(args[0], args[1])) {
                            /*
                             * Update necessary information about client
                             */
                            client.username = args[0];
                            client.uid = Database.STS_USER.getUid(client.username);
                            client.bid = Database.STS_BORROW.getBid(client.uid);
                            client.lid = Database.STS_LEND.getLid(client.uid);
                            client.email = Database.STS_USER.getEmail(client.username);
                            /*
                             * Send reply to client success or failure
                             */
                            printWriter.println(Reply.AUTHENTICATION_SUCCESS.getReply() + tok_del + client.email + arg_del + client.uid + arg_del + client.bid);
                        } else {
                            printWriter.println(Reply.AUTHENTICATION_FAILED.getReply());
                        }
                    } else 
                    // RETRIEVE ALL DATA 
                    if (request.equals(Request.RETRIEVE_ALL_DATA.getRequest())) {
                        /*
                         * Collect all information from share database replies, borrows, lends, boxes, items, etc.
                         */
                        String[][] sts_replies = Database.STS_REPLY.getReplies(client.uid);
                        String[][] sts_borrows = Database.STS_SHARE.getBorrowerShares(client.bid);
                        String[][] sts_lends = Database.STS_SHARE.getLenderShares(client.lid);
                        String[][] sts_boxes = Database.STS_LOCKER.getBoxes();
                        String[][] sts_user_items = Database.STS_ITEM.getUserItemsInfo(client.lid); 
                        String[][] sts_other_items = Database.STS_ITEM.getItemsInfo(client.lid);
                        String[][] sts_cards = Database.STS_CARD.getCardsInfo(client.uid);
                        /*
                         * Combine all collected data togather generate single lined text.
                         */
                        String data = generate_data(
                            sts_replies, sts_borrows, sts_lends, sts_boxes, sts_user_items, sts_other_items, sts_cards
                        );
                        /*
                         * Send generated data to clients
                         */
                        printWriter.println(Reply.RETRIVE_ALL_DATA_SUCCESS.getReply() + tok_del + data);
                    } else 
                    // ADD NEW ITEM FOR EACH USER
                    if (request.equals(Request.ADD_USER_ITEM.getRequest())) {
                        /*
                         * Add new user item using client information driven buy client request
                         * @param lid = lender id
                         * @param item category
                         * @param description
                         * @param name
                         * @param price
                         * @param height
                         * @param width
                         * @param length
                         */
                        if (Database.STS_ITEM.insert(
                                client.lid, 
                                Category.fromString(args[1]), 
                                args[3], 
                                args[0], 
                                Integer.parseInt(args[2]), 
                                Float.parseFloat(args[4]), 
                                Float.parseFloat(args[5]), 
                                Float.parseFloat(args[6])
                            )
                        ) {
                            /*
                             * Reply Added or Exists
                             */
                            printWriter.println(Reply.ITEM_ADDED.getReply());
                        } else {
                            printWriter.println(Reply.ITEM_EXISTS.getReply());
                        }
                    } else 
                    // DELETE ITEM FOR EACH USER
                    if (request.equals(Request.DELETE_USER_ITEM.getRequest())) {
                        /*
                         * Delete item using item id
                         */
                        if (Database.STS_ITEM.deleteItem(Integer.parseInt(args[0]))) {
                            printWriter.println(Reply.ITEM_DELETED.getReply());
                        }
                    } else 
                    // REQUEST OTHER USER'S ITEM
                    if (request.equals(Request.REQUEST_ITEM.getRequest())) {
                        /*
                         * Requesting other items shown on Borrow Fragment(Mobile App)
                         * using item id find out item's owner and using client information
                         * create reply record and save into database at STS_REPLY
                         */
                        int owner_uid = Database.STS_ITEM.getLid(Integer.parseInt(args[0]));
                        String reply =  Request.USER_ITEM_ASKED_FOR_BORROW.getRequest() + "@" + client.username + "$" + args[0];
                        if (Database.STS_REPLY.insert(owner_uid, 0, reply)) {
                            printWriter.println(Reply.ITEM_REQUESTED.getReply());
                        }
                    } else 
                    // DELETE SPECIFIC USER'S CARD RECORD
                    // if (request.equals(Request.DELETE_CARD.getRequest())) {
                    //     Database.STS_CARD.deleteCard(client.uid, Long.parseLong(args[0]));
                    //     printWriter.println(Reply.CARD_DELETED.getReply());
                    // } else 
                    // ACCEPT ITEM REQUEST FOR SPECIFIC BORROWER
                    if (request.equals(Request.ACCEPT_ITEM_REQUEST.getRequest())) {
                        /*
                         * Accept borrower request poped from Lender's interface
                         * Inside STS_SHARE put borrower id, item id, reply id, 
                         * items dimensions, specific lockers for items using it's dimensions,
                         * verification id generated by OTP class; all of the information 
                         * going to add into that table.
                         */
                        int bid = Database.STS_BORROW.getBid(Database.STS_USER.getUid(args[0]));
                        int iid = Database.STS_ITEM.getIid(client.uid, args[1]);
                        int rid = Integer.parseInt(args[2]);
                        int[] dimension = Database.STS_ITEM.getDimension(iid);
                        int loid = Database.STS_LOCKER.selectBox(dimension[0], dimension[1], dimension[2]);
                        Database.STS_VERIFY.insert(client.uid, OtpGenerator.generateCode());
                        int vid = Database.STS_VERIFY.getVid(client.uid);
                        if (Database.STS_SHARE.insert(bid, iid, loid, vid)) {
                            /*
                             * Update Lender item putting phase & delete reply after accept borrow request
                             */
                            Database.STS_SHARE.updatePhase("l", "p", vid, bid, iid, vid);
                            Database.STS_REPLY.delete(rid);
                        }
                        Thread.sleep(1000);

                        /*
                         * Create new reply for client (borrower) to inform about 
                         * rent acceptance.
                         */
                        int owner_uid = Database.STS_USER.getUid(args[0]);
                        String reply =  Reply.REQUEST_ACCEPTED.getReply() + "@" + client.username + "$" + iid;
                        Database.STS_REPLY.delete(Integer.parseInt(args[2]));
                        if (Database.STS_REPLY.insert(owner_uid, 0, reply)) {
                            printWriter.println(Reply.REQUEST_ACCEPTED.getReply());
                        }
                    } else 
                    // DECLINE ITEM REQUEST FOR SPECIFIC BORROWER
                    if (request.equals(Request.DECLINE_ITEM_REQUEST.getRequest())) {
                        /*
                         * Delete reply from STS_REPLY depending on borrow request from specific client
                         * and after that inform that client about borrow request was declined using
                         * newly generated reply.
                         */
                        int owner_uid = Database.STS_USER.getUid(args[0]);
                        String reply =  Reply.REQUEST_DECLINED.getReply() + "@" + client.username + "$" + Database.STS_ITEM.getIid(client.uid, args[1]);
                        Database.STS_REPLY.delete(Integer.parseInt(args[2]));
                        if (Database.STS_REPLY.insert(owner_uid, 0, reply)) {
                            printWriter.println(Reply.REQUEST_DECLINED.getReply());
                        }
                    } else
                    // REMOVE DECLINE INFO FROM REPLY
                    if (request.equals(Request.OK_DECLINE.getRequest())) {
                        /*
                         * Remove delcine reply from STS_REPLY
                         */
                        int rid = Integer.parseInt(args[0]);
                        Database.STS_REPLY.delete(rid);
                        printWriter.println(Reply.DECLINE.getReply());
                    } else 
                    // MAKE PAYMENT FOR SPECIFIC BORROW
                    if (request.equals(Request.MAKE_PAYMENT.getRequest())) {
                        // Database.STS_CARD.insert(
                        //     Long.parseLong(args[4]), client.uid, Integer.parseInt(args[5]), 
                        //     Integer.parseInt(args[6]), Integer.parseInt(args[7]), args[3]);
                        /*
                         * update STS_SHARE table where a spcific user make payment on
                         * specific rent order and reply as 'Paid' to client.
                         */
                        Database.STS_SHARE.update(Date.valueOf(args[1]), Date.valueOf(args[2]), Integer.parseInt(args[8]), 
                            Long.parseLong(args[4]), 1, Integer.parseInt(args[0]), Integer.parseInt(args[9]));
                        printWriter.println(Reply.PAID.getReply());
                    } else 
                    // SEND CODE FOR PARTICULAR USER (LP/LG/BP/BG)
                    if (request.equals(Request.SEND_VERIFICATION.getRequest())) {
                        /*
                         * The first stage of updating client phase & target, generate verification code
                         * for lender putting item into lockers.
                         */
                        String stage = args[4];
                        int bid = Integer.parseInt(args[1]);
                        int iid = Integer.parseInt(args[2]);
                        int vid = Integer.parseInt(args[3]);
                        int lid = Database.STS_ITEM.getLid(iid);
                        int uid = -1;
                        char del = '\0';
                        /*
                         * Using special charactors into verification code to identify specific phase
                         */
                        if (stage.equals("lp")) {
                            uid = Database.STS_LEND.getUid(lid);
                            del = 'A';
                        } else if (stage.equals("lg")) {
                            uid = Database.STS_LEND.getUid(lid);
                            del = 'D';
                        } else if (stage.equals("bg")) {
                            uid = Database.STS_BORROW.getUid(bid);
                            del = 'B';
                        }  else if (stage.equals("bp")) {
                            uid = Database.STS_BORROW.getUid(bid);
                            del = 'C';
                        } 
                        /*
                         * Generte code with spcial charactors
                         */
                        String code =  String.valueOf(uid) + del + Database.STS_VERIFY.getVcode(vid);
                        System.out.println(code);
                        printWriter.println(Reply.VERIFICATION_SENT.getReply());
                    } 
                    


                /*
                 *  AURDUINO FUNCTIONALITY
                 */
                } else if (client.platform == Platform.ARDUINO) {
                    // VERIFY EACH ESP32-WARROOM-32D INERFACE
                    if (request.equals(Request.ARDUINO_IDENTIFICATION.getRequest())) {
                        if (Database.STS_LOCKER.auth(args[0], args[1])) {
                            client.loid = Database.STS_LOCKER.getLoid(args[0], args[1]);
                            users.add(client);
                            printWriter.println(String.valueOf(client.loid));
                        } else {
                            printWriter.println(Reply.AUTHENTICATION_FAILED.getReply());
                        }
                    } else
                    // VERIFY CODE ENTERED BY USER
                    if (request.equals(Request.LP_VERIFICATION.getRequest())) {
                        int uid = Integer.parseInt(args[0].split("A")[0]);
                        int code = Integer.parseInt(args[0].split("A")[1].split("@")[0]);
                        int vid_vc = Database.STS_VERIFY.getVidVV(code);
                        int vid_vu = Database.STS_VERIFY.getVid(uid);
                        int loid = Integer.parseInt(args[0].split("A")[1].split("@")[1]);
                        int loid_vv = Database.STS_SHARE.getLoid(vid_vu);
                        System.out.println(vid_vc + " " + vid_vu + " " + loid + " " + loid_vv);
                        if (vid_vc == vid_vu && loid == loid_vv) {
                            Database.STS_VERIFY.delete(vid_vu);
                            int bid = Database.STS_SHARE.getBid(vid_vu);
                            int id = Database.STS_BORROW.getUid(bid);
                            Database.STS_VERIFY.insert(id, OtpGenerator.generateCode());
                            int nvid = Database.STS_VERIFY.getVid(id);
                            Database.STS_SHARE.updatePhase("b", "g", nvid, vid_vc);
                            printWriter.println(Reply.LP_SUCCESS.getReply());
                            System.out.println(Reply.LP_SUCCESS.getReply());
                        } else {
                            printWriter.println(Reply.LP_FAILED.getReply());
                            System.out.println(Reply.LP_FAILED.getReply());
                        }
                    } else
                    // VERIFY CODE ENTERED BY USER
                    if (request.equals(Request.LG_VERIFICATION.getRequest())) {
                        int uid = Integer.parseInt(args[0].split("D")[0]);
                        int code = Integer.parseInt(args[0].split("D")[1].split("@")[0]);
                        int vid_vc = Database.STS_VERIFY.getVidVV(code);
                        int vid_vu = Database.STS_VERIFY.getVid(uid);
                        int loid = Integer.parseInt(args[0].split("D")[1].split("@")[1]);
                        int loid_vv = Database.STS_SHARE.getLoid(vid_vu);
                        if (vid_vc == vid_vu && loid == loid_vv) {
                            Database.STS_VERIFY.delete(vid_vu);
                            Database.STS_SHARE.setFinished(vid_vu);
                            printWriter.println(Reply.LG_SUCCESS.getReply());
                        } else {
                            printWriter.println(Reply.LG_FAILED.getReply());
                        }
                    } else
                    // VERIFY CODE ENTERED BY USER
                    if (request.equals(Request.BP_VERIFICATION.getRequest())) {
                        int uid = Integer.parseInt(args[0].split("C")[0]);
                        int code = Integer.parseInt(args[0].split("C")[1].split("@")[0]);
                        int vid_vc = Database.STS_VERIFY.getVidVV(code);
                        int vid_vu = Database.STS_VERIFY.getVid(uid);
                        int loid = Integer.parseInt(args[0].split("C")[1].split("@")[1]);
                        int loid_vv = Database.STS_SHARE.getLoid(vid_vu);
                        if (vid_vc == vid_vu && loid == loid_vv) {
                            Database.STS_VERIFY.delete(vid_vu);
                            int iid = Database.STS_SHARE.getIid(vid_vu);
                            int lid = Database.STS_ITEM.getLid(iid);
                            int id = Database.STS_LEND.getUid(lid);
                            Database.STS_VERIFY.insert(id, OtpGenerator.generateCode());
                            int nvid = Database.STS_VERIFY.getVid(id);
                            Database.STS_SHARE.updatePhase("l", "g", nvid, vid_vc);
                            printWriter.println(Reply.BP_SUCCESS.getReply());
                        } else {
                            printWriter.println(Reply.BP_FAILED.getReply());
                        }
                    } else
                    // VERIFY CODE ENTERED BY USER
                    if (request.equals(Request.BG_VERIFICATION.getRequest())) {
                        int uid = Integer.parseInt(args[0].split("B")[0]);
                        int code = Integer.parseInt(args[0].split("B")[1].split("@")[0]);
                        int vid_vc = Database.STS_VERIFY.getVidVV(code);
                        int vid_vu = Database.STS_VERIFY.getVid(uid);
                        int loid = Integer.parseInt(args[0].split("B")[1].split("@")[1]);
                        int loid_vv = Database.STS_SHARE.getLoid(vid_vu);
                        if (vid_vc == vid_vu && loid == loid_vv) {
                            System.out.println("s1");
                            Database.STS_VERIFY.delete(vid_vu);
                            Database.STS_VERIFY.insert(uid, OtpGenerator.generateCode());
                            int nvid = Database.STS_VERIFY.getVid(uid);
                            Database.STS_SHARE.updatePhase("b", "p", nvid, vid_vc);
                            printWriter.println(Reply.BG_SUCCESS.getReply());
                        } else {
                            System.out.println("s2");
                            printWriter.println(Reply.BG_FAILED.getReply());
                        }
                    }
                        
                }
                Thread.sleep(500);
            }
            bufferedReader.close();
            printWriter.close();
        } catch (IOException e) {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).socket == client.socket) {
                    users.remove(users.get(i));
                } 
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e) {
            
        }
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String generate_data(
        String[][] replies, 
        String[][] borrows, 
        String[][] lends, 
        String[][] boxes,
        String[][] user_items,
        String[][] other_items,
        String[][] cards
    ) {
        String data = "";
        data = combine(data, "replies",replies);
        data += con_del;
        data = combine(data, "borrows", borrows);
        data += con_del;
        data = combine(data, "lends", lends);
        data += con_del;
        data = combine(data, "boxes", boxes);
        data += con_del;
        data = combine(data, "user_items", user_items);
        data += con_del;
        data = combine(data, "other_items", other_items);
        data += con_del;
        data = combine(data, "cards", cards);
        return data;
    }

    public String combine(String data, String title, String[][] arr) {
        data += (title + tit_del);
        for (String[] row: arr) {
            for (int i = 0; i < row.length; i++) {
                data += row[i];
                if (i != row.length - 1) 
                    data += arg_del;
            }
            data += sep_del;
        }
        return data;
    }
}
