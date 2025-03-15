package com.share;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
/*
 * Runnable interface allows programe to context switch
 */
public class ShareEazeServer implements Runnable{
    /*
     * Socket Api (ServerSocket & Socket)
     */
    private ServerSocket shareEazeServer;   // socket api
    private boolean isServerRunning = false;
    private int port = 55555;
    private ArrayList<Client> users = new ArrayList<>();
    private ArrayList<BORROWER_ACK> acks = new ArrayList<>();
    private ArrayList<RENT_COMPLETION> comps = new ArrayList<>();

    public ShareEazeServer() {
        /*
         * @param port denotes specific service with the ip address
         * when we call ServerSocket it will launch server at localhost:port
         * if server lunched set isServerRunning state to true for indication 
         * server lunched state.
         */
        try {
            shareEazeServer = new ServerSocket(port);
            isServerRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        /*
         * Stop server running
         */
        try {
            shareEazeServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Overriding run method from Runnable interface &
     * implementing an infinite loop for accept clients
     * through hotspot (network)
     */
    @Override
    public void run() {
        try {
            while (isServerRunning) {
                Socket socket = shareEazeServer.accept();
                Client client = new Client();
                client.socket = socket;
                users.add(client);
                /*
                 * Parse client info into client handlers
                 */
                HandleClient handleClient = new HandleClient(client, users, isServerRunning, acks, comps);
                Thread thread = new Thread(handleClient);
                thread.start();
                handleClient.setThread(thread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
