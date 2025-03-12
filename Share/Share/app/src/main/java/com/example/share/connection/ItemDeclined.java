package com.example.share.connection;

public class ItemDeclined {
    private int rid;
    private int uid;
    private int status;
    private String replyMsg;
    private String replyUsr;
    private String replyItm;
    private String itemName;

    public ItemDeclined(int rid, int uid, int status, String reply, String itemName) {
        this.rid = rid;
        this.uid = uid;
        this.status = status;
        this.itemName = itemName;
        this.replyMsg = reply.split("@")[0];
        this.replyUsr = reply.split("@")[1].split("\\$")[0];
        this.replyItm = reply.split("@")[1].split("\\$")[1];
    }

    public int getRid() {
        return rid;
    }

    public int getUid() {
        return uid;
    }

    public int getStatus() {
        return status;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public String getReplyUsr() {
        return replyUsr;
    }

    public String getReplyItm() {
        return replyItm;
    }

    public String getItemName() {
        return itemName;
    }
}
