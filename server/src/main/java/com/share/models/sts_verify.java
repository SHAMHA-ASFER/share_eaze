package com.share.models;

import com.share.Database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class sts_verify {

    private String insert_query = "insert into sts_verify(uid , v_code) values (?,?);";
    private String delete_query = "delete from sts_verify where vid = ? ;";
    private String get_vid_query = "select vid from sts_verify where uid=?;";
    private String get_vidvv_query = "select vid from sts_verify where v_code=?;";
    private String get_vcode_query = "select v_code from sts_verify where vid = ?;";


    public boolean insert(int uid , int v_code){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1, uid);
            preparedStatement.setInt(2, v_code);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public void delete (int vid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(delete_query);
            preparedStatement.setInt(1, vid);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        } catch (SQLException e) {

        }
    }
  
    public int getVid (int uid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_vid_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getVidVV(int vcode){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_vidvv_query);
            preparedStatement.setInt(1, vcode);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getVcode(int vid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_vcode_query);
            preparedStatement.setInt(1, vid);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
