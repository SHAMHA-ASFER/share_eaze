package com.example.share.connection;
/*
 * Program Starter
 * Author : Divija
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.example.share.account.signin.Login;
import com.example.share.connection.inteface.AccountListener;
import com.example.share.connection.inteface.AllDataListener;
import com.example.share.connection.inteface.BorrowListener;
import com.example.share.connection.inteface.CardListener;
import com.example.share.connection.inteface.ItemListener;
import com.example.share.connection.inteface.VerificationListener;
import com.example.share.constants.Platform;
import com.example.share.constants.Reply;
import com.example.share.constants.Request;
import com.example.share.constants.StaticData;
import com.example.share.error.Server404;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
 * ServerAPI is a Singleton class; it can't be duplicated
 * It should return its common instance for anywhere using
 * static method called getInstance()
 */
public class ServerAPI extends Thread {
    private static ServerAPI serverAPI = new ServerAPI();
    private static String host = "192.168.137.1";
    private static int port = 55555;
    private static int timeout = 1000;
    private static Activity currentActivity;
    private static Socket socket;
    public static BufferedReader bufferedReader;
    public static PrintWriter printWriter;
    public static boolean requestForInterrupt = false;
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

    private static String replyText = "";
    private static Reply reply = null;

    /* disable instance creation */
    private ServerAPI() {
    }

    /* get ServerAPI class instance */
    public static ServerAPI getInstance() {
        return serverAPI;
    }

    /* update current screen on mobile application */
    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }
    /* attempt to connect server with IP address and port number
     * using android thread by Handler and postDelayed method for
     * some delay when starting window
     *  */
    public static void getConnected() {
        new Handler().postDelayed(()-> {
            currentActivity.runOnUiThread(() -> {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    socket = new Socket();
                    try {
                        socket.connect(new InetSocketAddress(host, port), timeout);
                        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        printWriter = new PrintWriter(socket.getOutputStream(), true);
                        printWriter.println(Platform.MOBILE.getPlatform());
                        currentActivity.startActivity(
                                new Intent(currentActivity.getApplicationContext(),
                                Login.class));
                        currentActivity.finish();
                    } catch (IOException ex) {
                        currentActivity.startActivity(
                                new Intent(currentActivity.getApplicationContext(),
                                Server404.class));
                        currentActivity.finish();
                    }
                });
                executor.shutdown();
            });
        } ,timeout);
    }
    /*
     * Extract reply from server response
     * */
    public static Reply getReply(String reply) {
        return Reply.fromString(reply.split(tok_del)[0]);
    }
    /*
     * Login users with username & passwords driven from their views
     * via requesting server
     */
    public static void LoginUser(AccountListener listener, String username, String password) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                Thread.sleep(500);
                printWriter.println(Request.LOGIN_USER.getRequest() + tok_del + username + arg_del + password);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.AUTHENTICATION_SUCCESS) {
                    listener.AuthenticationSuccess(
                            replyText.split(tok_del)[1].split(arg_del)[0],
                            replyText.split(tok_del)[1].split(arg_del)[1],
                            replyText.split(tok_del)[1].split(arg_del)[2]
                    );
                    Log.d("OUT", replyText);
                } else if (reply == Reply.AUTHENTICATION_FAILED) {
                    listener.AuthenticationFailed();
                }
                requestForInterrupt = false;
            } catch (IOException | InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /*
     * Override run method due to Thread abstract class (must be implemented)
     * Retrieve all data from server using specific user id
     */
    public void run() {
        while (true) {
            try {
                if (!requestForInterrupt) {
                    updateAllData(new AllDataListener() {
                        @Override
                        public void isAllDataReceived(String data) {
                            decodeAllData(data);
                        }
                    });
                }
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    /*
     * update all client data from server in a time periot
     */
    public void updateAllData(AllDataListener listener) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                printWriter.println(Request.RETRIEVE_ALL_DATA.getRequest());
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.RETRIEVE_ALL_DATA_SUCCESS) {
                    listener.isAllDataReceived(replyText.split(tok_del)[1]);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* add item using driven data from views inside Item Fragment class */
    public static void addItem(ItemListener listener, String name, String cate, String price, String desc, String height, String length, String width) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(
                        Request.ADD_USER_ITEM.getRequest() + tok_del +
                        name + arg_del + cate + arg_del + price + arg_del + desc + arg_del + height + arg_del + length +arg_del + width);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.ITEM_ADDED) {
                    listener.onItemAdded();
                } else if (reply == Reply.ITEM_EXISTS) {
                    listener.onItemAlreadyExists();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* using item id delete record request for server */
    public static void deleteItem(ItemListener listener, String iid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.DELETE_USER_ITEM.getRequest() + tok_del + iid);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.ITEM_DELETED) {
                    listener.onItemDeleted();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* request particular item by client to server */
    public static void requestItem(ItemListener listener, String iid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.REQUEST_ITEM.getRequest() + tok_del + iid);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.ITEM_REQUESTED) {
                    listener.onItemRequested();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* Make payment using depit or cridit card info and rent duration */
    public static void makePayment(BorrowListener listener, String bid, String sdate, String edate, String owner, String number, String expire, String cvc, String amount, String iid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(
                        Request.MAKE_PAYMENT.getRequest() + tok_del + bid + arg_del + sdate + arg_del + edate +
                                arg_del + owner + arg_del + number + arg_del + expire.split("/")[0] + arg_del + expire.split("/")[1] +
                                arg_del + cvc + arg_del + amount + arg_del + iid
                );
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.PAID) {
                    listener.onPaid();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }

//    public static void removeCard(CardListener listener, String number) {
//        ExecutorService service = Executors.newSingleThreadExecutor();
//        service.execute(() -> {
//            try {
//                requestForInterrupt = true;
//                printWriter.println(Request.DELETE_CARD.getRequest() + tok_del + number);
//                replyText = bufferedReader.readLine();
//                reply = getReply(replyText);
//                if (reply == Reply.CARD_DELETED) {
//                    listener.isCardRemoved();
//                }
//                requestForInterrupt = false;
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        });
//        service.shutdown();
//    }
    /* accept a particular borrower request */
    public static void acceptRequest(ItemListener listener, String name, String item, String rid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.ACCEPT_ITEM_REQUEST.getRequest() + tok_del + name + arg_del + item + arg_del + rid);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.REQUEST_ACCEPTED) {
                    listener.onItemRequestAccepted();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* decline particular borrow request */
    public static void declineRequest(ItemListener listener , String borrower, String item, String rid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.DECLINE_ITEM_REQUEST.getRequest() + tok_del + borrower + arg_del + item + arg_del + rid);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.REQUEST_DECLINED) {
                    listener.onItemRequestDeclined();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* delete decline info from STS_REPLY */
    public static void okDecline(ItemListener listener, String rid) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.OK_DECLINE.getRequest() + tok_del + rid);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.DECLINE) {
                    listener.onDeclineOk();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* resent verification codes */
    public static void sendVerificationCode(VerificationListener listener, String loid, String bid, String iid, String vid, String stage) {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(() -> {
            try {
                requestForInterrupt = true;
                printWriter.println(Request.SEND_VERIFICATION.getRequest() + tok_del + loid + arg_del + bid + arg_del + iid + arg_del + vid + arg_del + stage);
                replyText = bufferedReader.readLine();
                reply = getReply(replyText);
                if (reply == Reply.VERIFICATION_SENT) {
                    listener.onCodeSend();
                }
                requestForInterrupt = false;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        service.shutdown();
    }
    /* divide into peaces and place them categorized */
    public static void decodeAllData(String data) {
        for (String content: data.split(con_del)) {
            String[] cont = content.split(tit_del);
            Log.d("TITLE", cont[0]);
            switch (cont[0]) {
                case "replies":
                    if (cont.length > 1)
                        updateReplies(cont[1]);
                    break;
                case "borrows":
                    if (cont.length > 1) {
                        updateBorrows(cont[1]);
                    }
                    break;
                case "lends":
                    if (cont.length > 1)
                        updateLends(cont[1]);
                    break;
                case "boxes":
                    if (cont.length > 1)
                        updateBoxes(cont[1]);
                    break;
                case "user_items":
                    if (cont.length > 1)
                        updateUserItems(cont[1]);
                    break;
                case "other_items":
                    if (cont.length > 1)
                        updateOtherItems(cont[1]);
                    break;
                case "cards":
                    if (cont.length > 1) {
                        updateCards(cont[1]);
                    }
            }
        }
    }
    /* update replies into StaticData */
    public static void  updateReplies(String data) {
        for (String row: data.split(sep_del)) {
            Log.d("OUTPUT", row);
            String[] args  = row.split(arg_del);
            if (args[3].split("@")[0].equals("user_item_asked_for_borrow")) {
                ItemRequest itemRequest = new ItemRequest(
                        Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), args[3], args[4]);
                boolean found = false;
                for (int i = 0; i < StaticData.itemRequests.size(); i++) {
                    if (StaticData.itemRequests.get(i).getRid() == itemRequest.getRid() &&
                        StaticData.itemRequests.get(i).getUid() == itemRequest.getUid()) {
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.itemRequests.add(itemRequest);
                }
            } else if (args[3].split("@")[0].equals("request_declined")) {
                ItemDeclined itemDecline = new ItemDeclined(
                        Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), args[3], args[4]);
                boolean found = false;
                for (int i = 0; i < StaticData.itemDeclines.size(); i++) {
                    if (StaticData.itemDeclines.get(i).getRid() == itemDecline.getRid() &&
                            StaticData.itemDeclines.get(i).getUid() == itemDecline.getUid()) {
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.itemDeclines.add(itemDecline);
                }
            } else if (args[3].split("@")[0].equals("request_accepted")) {
                ItemAccepted itemAccepted = new ItemAccepted(
                        Integer.parseInt(args[0]), Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]), args[3], args[4]);
                boolean found = false;
                for (int i = 0; i < StaticData.itemsAccepted.size(); i++) {
                    if (StaticData.itemsAccepted.get(i).getRid() == itemAccepted.getRid() &&
                            StaticData.itemsAccepted.get(i).getUid() == itemAccepted.getUid()) {
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.itemsAccepted.add(itemAccepted);
                }
            }
        }
    }

    /* update borrows into StaticData */
    public static void updateBorrows(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            Borrow borrow = new Borrow(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4].toCharArray()[0], args[5].toCharArray()[0],
                    Date.valueOf(args[6]), Date.valueOf(args[7]), Float.parseFloat(args[8]), Long.parseLong(args[9]),
                    Integer.parseInt(args[10]), Integer.parseInt(args[11]), args[12], args[13], args[14]);

            if (StaticData.borrows.isEmpty()) {
                StaticData.borrows.add(borrow);
            } else {
                boolean found = false;
                for (int i = 0; i < StaticData.borrows.size(); i++) {
                    if (borrow.getBid() == StaticData.borrows.get(i).getBid() &&
                            borrow.getIid() == StaticData.borrows.get(i).getIid()) {
                        StaticData.borrows.get(i).setStart_d(borrow.getStart_d());
                        StaticData.borrows.get(i).setEnd_d(borrow.getEnd_d());
                        StaticData.borrows.get(i).setComplete(borrow.getComplete());
                        StaticData.borrows.get(i).setNumber(borrow.getNumber());
                        StaticData.borrows.get(i).setTarget(borrow.getTarget());
                        StaticData.borrows.get(i).setPhase(borrow.getPhase());
                        StaticData.borrows.get(i).setPayment(borrow.getPayment());
                        StaticData.borrows.get(i).setVid(borrow.getVid());
                        StaticData.borrows.get(i).setFinished(borrow.getFinished());
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.borrows.add(borrow);
                }
            }
        }
    }

    /* update Lends into StaticData */
    public static void updateLends(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            Lend lend = new Lend(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), args[4].toCharArray()[0], args[5].toCharArray()[0],
                    Date.valueOf(args[6]), Date.valueOf(args[7]), Float.parseFloat(args[8]), Integer.parseInt(args[9]),
                    Integer.parseInt(args[10]), Integer.parseInt(args[11]), args[12], args[13], args[14]);

            if (StaticData.lends.isEmpty()) {
                StaticData.lends.add(lend);
            } else {
                boolean found = false;
                for (int i = 0; i < StaticData.lends.size(); i++) {
                    if (lend.getBid() == StaticData.lends.get(i).getBid() &&
                            lend.getIid() == StaticData.lends.get(i).getIid()) {
                        StaticData.lends.get(i).setStart_d(lend.getStart_d());
                        StaticData.lends.get(i).setEnd_d(lend.getEnd_d());
                        StaticData.lends.get(i).setComplete(lend.getComplete());
                        StaticData.lends.get(i).setNumber(lend.getNumber());
                        StaticData.lends.get(i).setTarget(lend.getTarget());
                        StaticData.lends.get(i).setPhase(lend.getPhase());
                        StaticData.lends.get(i).setPayment(lend.getPayment());
                        StaticData.lends.get(i).setVid(lend.getVid());
                        StaticData.lends.get(i).setFinished(lend.getFinished());
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.lends.add(lend);
                }
            }
        }
    }

    /* update boxes into StaticData */
    public static void updateBoxes(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            Box box = new Box(
                    Integer.parseInt(args[0]), args[1], Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]), Integer.parseInt(args[4]),Integer.parseInt(args[5]), args[6]);

            if (StaticData.boxes.isEmpty()) {
                StaticData.boxes.add(box);
            } else {
                boolean found = false;
                for (int i = 0; i < StaticData.boxes.size(); i++) {
                    if (box.getLock_id() == StaticData.boxes.get(i).getLock_id()) {
                        StaticData.boxes.get(i).setAvailability(box.getAvailability());
                        StaticData.boxes.get(i).setStatus(box.getStatus());
                        Log.d("STATUS", box.getStatus());
                        found = true;
                    }
                }
                if (!found) {
                    StaticData.boxes.add(box);
                }
            }
        }
    }

    /* update User Items into StaticData */
    public static void updateUserItems(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            Log.d("CHECK", args[8]);
            UserItem userItem = new UserItem(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3], args[4],
                    Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]),
                    Float.parseFloat(args[8]));

            for (int i = 0; i < StaticData.userItems.size(); i++) {
                if (userItem.getIid() == StaticData.userItems.get(i).getIid()) {
                    StaticData.userItems.remove(StaticData.userItems.get(i));
                }
            }

            StaticData.userItems.add(userItem);
        }
    }

    /* update Other Items into StaticData */
    public static void updateOtherItems(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            OtherItem otherItem = new OtherItem(
                    Integer.parseInt(args[0]), Integer.parseInt(args[1]), args[2], args[3], args[4],
                    Float.parseFloat(args[5]), Float.parseFloat(args[6]), Float.parseFloat(args[7]),
                    Float.parseFloat(args[8]));

            for (int i = 0; i < StaticData.otherItems.size(); i++) {
                if (otherItem.getIid() == StaticData.otherItems.get(i).getIid()) {
                    StaticData.otherItems.remove(StaticData.otherItems.get(i));
                }
            }

            StaticData.otherItems.add(otherItem);
        }
    }

    /* update cards into StaticData */
    public static void updateCards(String data) {
        for (String row: data.split(sep_del)) {
            String[] args = row.split(arg_del);
            Card card = new Card(
                    Long.parseLong(args[0]),
                    Integer.parseInt(args[1]),
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Integer.parseInt(args[4]),
                    args[5]
            );

            for (int i = 0; i < StaticData.cards.size(); i++) {
                if (StaticData.cards.get(i).getNumber() == card.getNumber()) {
                    StaticData.cards.remove(StaticData.cards.get(i));
                }
            }

            StaticData.cards.add(card);
        }
    }
}
