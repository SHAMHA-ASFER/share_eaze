package com.share.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.share.Database;

public class sts_personal {
    private String insert_query = "insert into sts_personal (nic,uid,first_name,last_name,address,phone_no)values(?,?,?,?,?,?);";
    private String retrieve_query = "select * from sts_personal where uid=?;";


    public boolean insert(String nic,int uid,String first_name,String last_name,String address, String phone_no){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setString(1,nic);
            preparedStatement.setInt(2,uid);
            preparedStatement.setString(3,first_name);
            preparedStatement.setString(4,last_name);
            preparedStatement.setString(5,address);
            preparedStatement.setString(6, phone_no);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public String[] retrieve(int uid){
        String[] personal = new String[5];
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(retrieve_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            if(result.next()){
                personal[0] = result.getString(1);
                personal[1] = result.getString(3);
                personal[2] = result.getString(4);
                personal[3] = result.getString(5);
                personal[4] = result.getString(6);
            }
        } catch (SQLException e) {
            return null;
        }
        return personal;
    }
}
