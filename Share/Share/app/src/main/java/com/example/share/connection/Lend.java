package com.example.share.connection;

import java.util.Date;

public class Lend {
    private int     bid;
    private int     iid;
    private int     lock_id;
    private int     vid;
    private char    target;
    private char    phase;
    private Date    start_d;
    private Date    end_d;
    private float   payment;
    private int     number;
    private int     complete;
    private int     finished;
    private String  itemName;
    private String  itemRate;
    private String  itemOwner;

    public Lend(int bid, int iid, int lock_id, int vid, char target, char phase, Date start_d, Date end_d, float payment, int number, int complete, int finished, String itemName, String itemRate, String itemOwner) {
        this.bid = bid;
        this.iid = iid;
        this.lock_id = lock_id;
        this.vid = vid;
        this.target = target;
        this.phase = phase;
        this.start_d = start_d;
        this.end_d = end_d;
        this.payment = payment;
        this.number = number;
        this.complete = complete;
        this.finished = finished;
        this.itemName = itemName;
        this.itemRate = itemRate;
        this.itemOwner = itemOwner;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public void setTarget(char target) {
        this.target = target;
    }

    public void setPhase(char phase) {
        this.phase = phase;
    }

    public void setStart_d(Date start_d) {
        this.start_d = start_d;
    }

    public void setEnd_d(Date end_d) {
        this.end_d = end_d;
    }

    public void setPayment(float payment) {
        this.payment = payment;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public int getBid() {
        return bid;
    }

    public int getIid() {
        return iid;
    }

    public int getLock_id() {
        return lock_id;
    }

    public int getVid() {
        return vid;
    }

    public char getTarget() {
        return target;
    }

    public char getPhase() {
        return phase;
    }

    public Date getStart_d() {
        return start_d;
    }

    public Date getEnd_d() {
        return end_d;
    }

    public float getPayment() {
        return payment;
    }

    public int getNumber() {
        return number;
    }

    public int getComplete() {
        return complete;
    }

    public int getFinished() {
        return finished;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemRate() {
        return itemRate;
    }

    public String getItemOwner() {
        return itemOwner;
    }
}
