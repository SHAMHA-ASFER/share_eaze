package com.share.models;

import java.util.List;
import java.util.ArrayList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.share.Database;

public class sts_card {

    private String insert_query = "insert into sts_card (number,uid,year,month,cvc,holder) values(?,?,?,?,?,?);";
    private String retrieve_query = "select * from sts_card where uid =?;";
    private String delete_card_query = "delete from sts_card where uid = ? and number = ?;";

    public boolean insert (long number , int uid , int year , int month , int cvc, String holder){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setLong(1, number);
            preparedStatement.setInt(2,uid);
            preparedStatement.setInt(3,year);
            preparedStatement.setInt(4, month);
            preparedStatement.setInt(5, cvc);
            preparedStatement.setString(6, holder);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;    
           }
         catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String[][] getCardsInfo(int uid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(retrieve_query);
            preparedStatement.setInt(1, uid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount];
                row[0] = Long.toString(result.getLong(1));
                row[1] = Integer.toString(result.getInt(2));
                row[2] = Integer.toString(result.getInt(3));
                row[3] = Integer.toString(result.getInt(4));
                row[4] = Integer.toString(result.getInt(5));
                row[5] = result.getString(6);
                rows.add(row);
            }
            String[][] data = rows.toArray(new String[0][]);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }

     public void deleteCard(int uid, long num){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(delete_card_query);
            preparedStatement.setInt(1, uid);
            preparedStatement.setLong(2, num);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
        }

     }
}


