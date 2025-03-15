package com.share.models;

import java.util.ArrayList;
import java.util.List;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.share.Client;
import com.share.Database;

public class sts_locker {
    private String insert = "";
    private String auth_query = "select exists (select * from sts_locker where username = ? and password = ?);";
    private String get_loid_query = "select loid from sts_locker where username = ? and password = ?;";
    private String get_name_query = "select name from sts_locker where loid = ?;";
    private String get_all_lockers_query = "select * from sts_locker;";
    private String select_locker_query = "select loid from sts_locker where width >= ? and height >= ? and length >= ?;";
    private String lock_query = "update sts_locker set availability = '0' where loid = ?";
    private String unlock_query = "update sts_locker set availability = '1' where loid = ?";

    private static ArrayList<Client> users;

    public void lockLocker(int loid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(lock_query);
            preparedStatement.setInt(1, loid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unlockLocker(int loid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(unlock_query);
            preparedStatement.setInt(1, loid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int selectLocker(int iid) {
        int[] dimesion = Database.STS_ITEM.getDimension(iid);
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(select_locker_query);
            preparedStatement.setInt(1, dimesion[0]);
            preparedStatement.setInt(2, dimesion[1]);
            preparedStatement.setInt(3, dimesion[2]);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setUsers(ArrayList<Client> users) {
        sts_locker.users = users;
    }

    public boolean auth(String username, String password) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(auth_query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                boolean exists = result.getBoolean(1);
                return exists;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getLoid(String username, String password) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_loid_query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int loid = result.getInt(1);
                return loid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getName(int loid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_name_query);
            preparedStatement.setInt(1, loid);
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

    public String[][] getBoxes(){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_all_lockers_query);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount];
                row[0] = Integer.toString(result.getInt(1));
                row[1] = result.getString(2);
                row[2] = Integer.toString(result.getInt(5));
                row[3] = Integer.toString(result.getInt(6));
                row[4] = Integer.toString(result.getInt(7));
                row[5] = Integer.toString(result.getInt(8));
                boolean isFound = false;
                for (int i = 0; i < users.size(); i++) {
                    if (users.get(i) != null) {
                        if (users.get(i).loid == result.getInt(1)) {
                            isFound = true;
                            break;
                        }
                    }
                }
                if (isFound) {
                    row[6] = "Online";
                } else {
                    row[6] = "Offline";
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

    public int selectBox(int width, int height, int lenght) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(select_locker_query);
            preparedStatement.setInt(1, width);
            preparedStatement.setInt(2, height);
            preparedStatement.setInt(3, lenght);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int loid = result.getInt(1);
                preparedStatement.close();
                return loid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
