package com.example.share.connection;

public class Card {
    private long number;
    private int uid;
    private int year;
    private int month;
    private int cvc;
    private String holder;

    public Card(long number, int uid, int year, int month, int cvc, String holder) {
        this.number = number;
        this.uid = uid;
        this.year = year;
        this.month = month;
        this.cvc = cvc;
        this.holder = holder;
    }

    public long getNumber() {
        return number;
    }

    public int getUid() {
        return uid;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getCvc() {
        return cvc;
    }

    public String getHolder() {
        return holder;
    }
}
