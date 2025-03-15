package com.share;
/*
 *  ShareEaze Server Starter Class
 */
public class Main {
    public static void main(String[] args) {
        Database.initialize(); 
        ShareEazeServer server = new ShareEazeServer(); // Launch server
        Thread thread = new Thread(server);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}