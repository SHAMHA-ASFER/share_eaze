package com.example.share.constants;

public enum Reply {
    AUTHENTICATION_SUCCESS("authentication_success"),
    AUTHENTICATION_FAILED("authentication_failed"),

    RETRIEVE_ALL_DATA_SUCCESS("retrieve_all_data_success"),
    LENDER_DECLINED_BORROW_REQUEST("lender_declined_borrow_request"),
    REQUEST_ACCEPTED("request_accepted"),
    REQUEST_DECLINED("request_declined"),
    DECLINE("decline"),
    ITEM_ADDED("item_added"),
    ITEM_EXISTS("item_exists"),
    ITEM_DELETED("item_deleted"),
    ITEM_REQUESTED("item_requested"),
    BORROW_DATES_UPDATED("borrow_dates_updated"),
    CARD_DELETED("card_deleted"),
    PAID("paid"),
    VERIFICATION_SENT("verification_sent"),

    UNKW_REPLY("unknown_reply");

    private final String reply;
    private Reply(String rep) {
        this.reply = rep;
    }

    public String getReply() {
        return reply;
    }

    public static Reply fromString(String str) {
        for (Reply rep: Reply.values()) {
            if (rep.reply.equalsIgnoreCase(str)) {
                return rep;
            }
        }
        return Reply.UNKW_REPLY;
    }
}
