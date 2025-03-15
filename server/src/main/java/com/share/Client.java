package com.share;

import java.net.Socket;
import java.util.LinkedList;

public class Client {
    public Socket socket;
    public Platform platform;
    public String username;
    public String email;
    public int uid;
    public int vid;
    public int bid = 0;
    public int loid;
    public LinkedList<Integer> iids = new LinkedList<>();
    public int lid;
}
