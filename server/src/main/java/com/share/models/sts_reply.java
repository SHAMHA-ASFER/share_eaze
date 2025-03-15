package com.share.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.share.Database;

public class sts_reply {

    private String insert_query = "insert into sts_reply(uid,status,reply,date_time) values (?,?,?,?);";
    private String update_status_query = "update sts_reply set status =? where rid =?";
    private String get_reply_query = "select * from sts_reply where uid = ? and status = 0";
    private String check_msg_query = "select exists (select * from sts_reply where uid = ?);";
    private String get_rid_query = "select rid from sts_reply where uid = ?;";
    private String delete_rid_query = "delete from sts_reply where rid = ?;";

    public boolean delete(int rid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(delete_rid_query);
            preparedStatement.setInt(1, rid);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();    
        }
        return false;
    }

    public int getRid(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_rid_query);
            preparedStatement.setInt(1,uid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int rid = result.getInt(1);
                return rid;
            }
        } catch (SQLException e) {
            e.printStackTrace();    
        }
        return -1;
    }

    public boolean checkForReplies(int uid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(check_msg_query);
            preparedStatement.setInt(1,uid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                boolean bool = result.getBoolean(1);
                return bool;
            }
        } catch (SQLException e) {
            e.printStackTrace();    
        }
        return false;
    }

    public boolean insert(int uid, int status , String reply){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1,uid);
            preparedStatement.setInt(2, status);
            preparedStatement.setString(3, reply);
            preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    public void update_status(int status , int rid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_status_query);
            preparedStatement.setInt(1, status);
            preparedStatement.setInt(2, rid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String[][] getReplies(int uid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_reply_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount];
                row[0] = Integer.toString(result.getInt(1));
                row[1] = Integer.toString(result.getInt(2));
                row[2] = Integer.toString(result.getInt(3));
                row[3] = result.getString(4);
                row[4] = Database.STS_ITEM.getItemName(Integer.parseInt(row[3].split("@")[1].split("\\$")[1]));
                rows.add(row);
            }
            String[][] data = rows.toArray(new String[0][]);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }
    
}
