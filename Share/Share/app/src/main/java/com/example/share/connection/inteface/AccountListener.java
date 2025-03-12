package com.example.share.connection.inteface;

public abstract class AccountListener {
    public void AuthenticationSuccess(String email, String uid, String bid) {
    }

    public void AuthenticationFailed() {
    }
}
