package com.example.share.constants;

import com.example.share.connection.Borrow;
import com.example.share.connection.Box;
import com.example.share.connection.Card;
import com.example.share.connection.ItemAccepted;
import com.example.share.connection.ItemDeclined;
import com.example.share.connection.ItemRequest;
import com.example.share.connection.Lend;
import com.example.share.connection.OtherItem;
import com.example.share.connection.UserItem;

import java.util.ArrayList;
public class StaticData {
    public static String username;
    public static String email;
    public static int uid;
    public static int bid;
    public static ArrayList<ItemRequest> itemRequests = new ArrayList<>();
    public static ArrayList<ItemDeclined> itemDeclines = new ArrayList<>();
    public static ArrayList<ItemAccepted> itemsAccepted = new ArrayList<>();
    public static ArrayList<Borrow> borrows = new ArrayList<>();
    public static ArrayList<Lend> lends = new ArrayList<>();
    public static ArrayList<OtherItem> otherItems = new ArrayList<>();
    public static ArrayList<UserItem> userItems = new ArrayList<>();
    public static ArrayList<Box> boxes = new ArrayList<>();
    public static ArrayList<Card> cards = new ArrayList<>();
}
