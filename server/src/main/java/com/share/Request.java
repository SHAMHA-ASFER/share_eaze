package com.share;

public enum Request {
    LOGIN_USER("login_user"),
    CREATE_USER("create_user"),

    RETRIEVE_ALL_DATA("retrieve_all_data"),
    USER_ITEM_ASKED_FOR_BORROW("user_item_asked_for_borrow"),
    DECLINE_ITEM_REQUEST("decline_item_request"),
    ACCEPT_ITEM_REQUEST("accept_item_request"),
    ADD_USER_ITEM("add_user_item"),
    DELETE_USER_ITEM("delete_user_item"),
    REQUEST_ITEM("request_item"),
    UPDATE_BORROW_DATES("update_borrow_dates"),
    DELETE_CARD("delete_card"),
    MAKE_PAYMENT("make_payment"),
    OK_DECLINE("ok_decline"),
    SEND_VERIFICATION("send_verification"),

    ARDUINO_IDENTIFICATION("arduino_identification"),

    LP_VERIFICATION("lp_verification"),
    LG_VERIFICATION("lg_verification"),
    BP_VERIFICATION("bp_verification"),
    BG_VERIFICATION("bg_verification"),

    UKNW_REQUEST("uknw_request");

    private final String request;
    private Request(String req) {
        this.request = req;
    }

    public String getRequest() {
        return request;
    }

    public static Request fromString(String str) {
        for (Request req: Request.values()) {
            if (req.request.equalsIgnoreCase(str)) {
                return req;
            }
        }
        return Request.UKNW_REQUEST;
    }
}
