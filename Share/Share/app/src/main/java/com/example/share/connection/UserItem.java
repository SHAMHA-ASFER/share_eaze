package com.example.share.connection;

public class UserItem {
    private int     iid;
    private int     lid;
    private String  cate;
    private String  desc;
    private String  name;
    private float   price;
    private float     height;
    private float     length;
    private float     width;

    public UserItem(int iid, int lid, String cate, String desc, String name, float price, float height, float length, float width) {
        this.iid = iid;
        this.lid = lid;
        this.cate = cate;
        this.desc = desc;
        this.name = name;
        this.price = price;
        this.height = height;
        this.length = length;
        this.width = width;
    }

    public int getIid() {
        return iid;
    }

    public int getLid() {
        return lid;
    }

    public String getCate() {
        return cate;
    }

    public String getDesc() {
        return desc;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public float getHeight() {
        return height;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }
}
