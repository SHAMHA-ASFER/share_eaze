package com.share.models;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.share.Database;

public class sts_share {
    private String insert_query = "insert into sts_share(bid, iid, loid, vid, target, phase, start_d, end_d, payment, number, complete, finished) values (?,?,?,?,?,?,?,?,?,?,?,?);";
    private String get_share_borrow_query = "select * from sts_share where bid = ? and not finished = 1;";
    private String get_share_lender_query = "select * from sts_share s inner join sts_item i on s.iid = i.iid where i.lid = ? and not finished = 1;";
    private String update_share_query = "update sts_share set start_d = ?, end_d = ?, payment = ?, number = ?, complete = ? where bid = ? and iid = ?;";
    public static String update_phase_query = "update sts_share set target = ?, phase = ?, vid = ? where bid = ? and iid = ? and not target = 'l' and not phase = 'g' and vid = ?;";
    public static String update_phase_arduino_query = "update sts_share set target = ?, phase = ?, vid = ? where vid = ?;";
    public String update_vid_query = "update sts_share set vid = ? where bid = ? and iid = ?;";
    private String update_dates = "update sts_share set start_d = ?, end_d = ? where bid = ?";
    private String get_bid_query = "select bid from sts_share where vid = ?";
    private String get_iid_query = "select iid from sts_share where vid = ?";
    private String get_loid_query = "select loid from sts_share where vid = ?";
    private String set_finished = "update sts_share set finished = 1 where vid = ?";
    
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public void setFinished(int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(set_finished);
            preparedStatement.setInt(1, vid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBid(int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_bid_query);
            preparedStatement.setInt(1, vid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int bid  = result.getInt(1);
                preparedStatement.close();
                return bid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getLoid(int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_loid_query);
            preparedStatement.setInt(1, vid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int loid  = result.getInt(1);
                preparedStatement.close();
                return loid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getIid(int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_iid_query);
            preparedStatement.setInt(1, vid);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                int iid  = result.getInt(1);
                preparedStatement.close();
                return iid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateDates(Date start, Date end, int bid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_dates);
            preparedStatement.setDate(1, start);
            preparedStatement.setDate(2, end);
            preparedStatement.setInt(3, bid);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public void update(Date start, Date end, int payment, long number, int complete, int bid, int iid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_share_query);
            preparedStatement.setDate(1, start);
            preparedStatement.setDate(2, end);
            preparedStatement.setInt(3, payment);
            preparedStatement.setLong(4, number);
            preparedStatement.setInt(5, complete);
            preparedStatement.setInt(6, bid);
            preparedStatement.setInt(7, iid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePhase(String target, String phase, int nvid, int bid, int iid, int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_phase_query);
            preparedStatement.setString(1, target);
            preparedStatement.setString(2, phase);
            preparedStatement.setInt(3, nvid);
            preparedStatement.setInt(4, bid);
            preparedStatement.setInt(5, iid);
            preparedStatement.setInt(6, vid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePhase(String target, String phase, int nvid, int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(update_phase_arduino_query);
            preparedStatement.setString(1, target);
            preparedStatement.setString(2, phase);
            preparedStatement.setInt(3, nvid);
            preparedStatement.setInt(4, vid);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean insert(int bid, int iid, int loid, int vid) {
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(insert_query);
            preparedStatement.setInt(1, bid);
            preparedStatement.setInt(2, iid);
            preparedStatement.setInt(3, loid);
            preparedStatement.setInt(4, vid);
            preparedStatement.setString(5, "");
            preparedStatement.setString(6, "");
            preparedStatement.setDate(7, Date.valueOf(simpleDateFormat.format(new java.util.Date())));
            preparedStatement.setDate(8, Date.valueOf(simpleDateFormat.format(new java.util.Date())));
            preparedStatement.setInt(9, 0);
            preparedStatement.setInt(10, 0);
            preparedStatement.setInt(11, 0);
            preparedStatement.setInt(12, 0);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String[][] getBorrowerShares(int bid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_share_borrow_query);
            preparedStatement.setInt(1, bid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount+3];
                row[0] = Integer.toString(result.getInt(1));
                row[1] = Integer.toString(result.getInt(2));
                row[2] = Integer.toString(result.getInt(3));
                row[3] = Integer.toString(result.getInt(4));
                row[4] = result.getString(5);
                row[5] = result.getString(6);
                row[6] = result.getDate(7).toString();
                row[7] = result.getDate(8).toString();
                row[8] = Integer.toString(result.getInt(9));
                row[9] = Long.toString(result.getLong(10));
                row[10] = Integer.toString(result.getInt(11));
                row[11] = Integer.toString(result.getInt(12));
                row[12] = Database.STS_ITEM.getItemName(Integer.parseInt(row[1]));
                row[13] = Database.STS_ITEM.getRate(Integer.parseInt(row[1]));
                row[14] = Database.STS_USER.getUsername(Database.STS_LEND.getUid(Database.STS_ITEM.getLid(Integer.parseInt(row[1]))));
                rows.add(row);
            }
            String[][] data = rows.toArray(new String[0][]);
            return data;
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return null;
    }

    public String[][] getLenderShares(int lid){
        try {
            PreparedStatement preparedStatement = Database.conn.prepareStatement(get_share_lender_query);
            preparedStatement.setInt(1, lid);
            ResultSet result = preparedStatement.executeQuery();
            int columnCount = result.getMetaData().getColumnCount();
            List<String[]> rows = new ArrayList<>();
            while (result.next()) {
                String[] row = new String[columnCount+3];
                row[0] = Integer.toString(result.getInt(1));
                row[1] = Integer.toString(result.getInt(2));
                row[2] = Integer.toString(result.getInt(3));
                row[3] = Integer.toString(result.getInt(4));
                row[4] = result.getString(5);
                row[5] = result.getString(6);
                row[6] = result.getDate(7).toString();
                row[7] = result.getDate(8).toString();
                row[8] = Integer.toString(result.getInt(9));
                row[9] = Integer.toString(result.getInt(10));
                row[10] = Integer.toString(result.getInt(11));
                row[11] = Integer.toString(result.getInt(12));
                row[12] = Database.STS_ITEM.getItemName(Integer.parseInt(row[1]));
                row[13] = Database.STS_ITEM.getRate(Integer.parseInt(row[1]));
                row[14] = Database.STS_USER.getUsername(Database.STS_LEND.getUid(Database.STS_ITEM.getLid(Integer.parseInt(row[1]))));
                rows.add(row);
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
