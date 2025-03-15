package com.share.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.share.Database;

public class sts_user {
    private String insert_query = "insert into sts_user(roll,username,password,email,is_verified,is_superuser,date_joined) values(?,?,?,?,?,?,?);";
    private String auth_query = "select exists (select 1 from sts_user where username = ? and password = ?);";
    private String update_password_query = "update sts_user set password = ? where uid = ?;";
    private String update_verified_query = "update sts_user set is_verified = ? where email =?;";
    private String get_uid_query = "select uid from sts_user where username =?;";
    private String get_username_query = "select username from sts_user where uid =?;";
    private String get_email_query = "select email from sts_user where username =?;";
    private String get_uid_via_email_query = "select uid from sts_user where email =?;";
    private String verify_email_query = "select exists (select 1 from sts_user where email = ?);";

    public boolean verifyEmail(String email) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(verify_email_query);
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                return result.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insert(String roll, String username,String password,String email,int is_verified,int is_superuser,Date date_joined){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setString(1,roll);
            preparedStatement.setString(2,username);
            preparedStatement.setString(3,password);
            preparedStatement.setString(4,email);
            preparedStatement.setInt(5,is_verified);
            preparedStatement.setInt(6,is_superuser);
            preparedStatement.setDate(7,date_joined);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean auth(String username , String password){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(auth_query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()){
                return result.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update_password(int uid, String password){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_password_query);
            preparedStatement.setString(1, password);
            preparedStatement.setInt(2, uid);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update_verify (int is_verified , String email){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_verified_query);
            preparedStatement.setInt(1, is_verified);
            preparedStatement.setString(2, email);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getUid(String username){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_uid_query);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return -1;
    }

    public String getUsername(int uid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_username_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();   
        }
        return null;
    }

    public int getUidViaEmail(String email){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_uid_via_email_query);
            preparedStatement.setString(1, email);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return -1;
    }

    public String getEmail(String username){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_email_query);
            preparedStatement.setString(1, username);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                return result.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }
}
