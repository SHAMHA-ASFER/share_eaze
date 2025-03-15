package com.share.models;

import java.util.List;
import java.util.ArrayList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.share.Category;
import com.share.Database;

public class sts_item {
    private String insert_query = "insert into sts_item (lid, category, description, name, price, height, length, width) values (?,?,?,?,?,?,?,?);";
    private String get_items_info_query = "select * from sts_item where not lid = ?;";
    private String get_user_items_info_query = "select * from sts_item where lid = ?;";
    private String delete_item_query = "delete from sts_item where iid = ?;";
    private String get_record_count_query = "select count(*) from sts_item;";
    private String get_lid_query = "select lid from sts_item where iid = ?;";
    private String get_item_name_query = "select name from sts_item where iid = ?;";
    private String get_item_rate_query = "select price from sts_item where iid = ?;";
    private String get_iid_query = "select iid from sts_item where lid = ? and name = ?;";
    private String get_dimension_query = "select width, height, length from sts_item where iid = ?;";

    public int[] getDimension(int iid) {
        int[] array = new int[3];
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_dimension_query);
            preparedStatement.setInt(1, iid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                array[0] = result.getInt(1);
                array[1] = result.getInt(2);
                array[2] = result.getInt(3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return array;
    }

    public String getRate(int iid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_item_rate_query);
            preparedStatement.setInt(1, iid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                String name = Integer.toString(result.getInt(1));
                return name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }

    public String getItemName(int iid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_item_name_query);
            preparedStatement.setInt(1, iid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                String name = result.getString(1);
                return name;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }

    public int getRecordsCount() {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_record_count_query);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int count = result.getInt(1);
                return count;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean deleteItem(int iid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(delete_item_query);
            preparedStatement.setInt(1, iid);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean insert(int lid, Category cat, String description, String name, int price, float height, float length, float width) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1, lid);
            preparedStatement.setString(2, cat.getCategory());
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, name);
            preparedStatement.setFloat(5, price);
            preparedStatement.setFloat(6, height);
            preparedStatement.setFloat(7, length);
            preparedStatement.setFloat(8, width);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public String[][] getItemsInfo(int lid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_items_info_query);
            preparedStatement.setInt(1, lid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = result.getString(i);
                }
                rows.add(row);
            }
            String[][] data = rows.toArray(new String[0][]);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[][] getUserItemsInfo(int lid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_user_items_info_query);
            preparedStatement.setInt(1, lid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = result.getString(i);
                }
                rows.add(row);
            }
            String[][] data = rows.toArray(new String[0][]);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getLid(int iid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_lid_query);
            preparedStatement.setInt(1, iid);
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

    public int getIid(int uid, String name) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_iid_query);
            preparedStatement.setInt(1, uid);
            preparedStatement.setString(2, name);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int iid = result.getInt(1);
                return iid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}