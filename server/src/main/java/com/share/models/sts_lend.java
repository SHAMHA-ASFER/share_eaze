package com.share.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.share.Database;

public class sts_lend {
    private String insert_query = "insert into sts_lend(uid) values(?);";    
    private String get_lid_query = "select lid from sts_lend where uid = ?;";
    private String get_uid_query = "select uid from sts_lend where lid = ?;";

    public void insert(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1, uid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLid(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_lid_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int lid = result.getInt(1);
                return lid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getUid(int lid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_uid_query);
            preparedStatement.setInt(1, lid);
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
