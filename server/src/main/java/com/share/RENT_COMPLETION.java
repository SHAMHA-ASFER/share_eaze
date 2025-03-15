package com.share;

public class RENT_COMPLETION {
    public int bid;
    public int loid;
    public int iid;
    public int vid;
    public int lid;
    public boolean isBorrowerCompleted = false;
    public boolean isLenderCompleted = false;

    public RENT_COMPLETION(int bid, int iid, int loid, int vid, int lid) {
        this.bid = bid;
        this.loid = loid;
        this.iid = iid;
        this.vid = vid;
        this.lid = lid;
    }
    
}
