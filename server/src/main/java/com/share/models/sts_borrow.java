package com.share.models;

import java.sql.*;

import com.share.Database;

public class sts_borrow {
    private String insert_query = "insert into sts_borrow (uid) values (?);";
    private String get_bid_query = "select bid from sts_borrow where uid = ?;";
    private String get_uid_query = "select uid from sts_borrow where bid = ?;";

    public void insert(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1, uid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBid(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_bid_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int bid = result.getInt(1);
                return bid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getUid(int bid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_uid_query);
            preparedStatement.setInt(1, bid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int uid = result.getInt(1);
                return uid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
