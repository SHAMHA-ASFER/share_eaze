package com.share;

import com.share.models.sts_borrow;
import com.share.models.sts_card;
import com.share.models.sts_item;
import com.share.models.sts_lend;
import com.share.models.sts_locker;
import com.share.models.sts_personal;
import com.share.models.sts_reply;
import com.share.models.sts_share;
import com.share.models.sts_user;
import com.share.models.sts_verify;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private static String url = "jdbc:postgresql://localhost:5432/share";
    private static String username = "postgres";
    private static String password = "123";
    private static HikariDataSource dataSource;
    public static Connection conn;

    private static String checkquery = "select exists (select 1 from pg_tables where schemaname = 'public' and tablename = ?)";
    
    private static String create_sts_user = "create table sts_user(uid serial,roll varchar (50),username varchar(50) unique,password varchar (30),email varchar (100) unique,is_verified integer ,is_superuser integer,date_joined date,constraint pk_user primary key(uid));";
    private static String create_sts_reply = "create table sts_reply(rid serial ,uid integer, status integer,reply varchar(255),date_time timestamp,constraint pk_rep primary key (rid, uid), constraint fk_reply foreign key(uid) references sts_user (uid));";
    private static String create_sts_verify = "create table sts_verify(vid serial unique,uid integer,v_code integer unique,constraint pk_veri primary key (vid),constraint fk_veri foreign key (uid) references sts_user (uid));";
    private static String create_sts_personal = "create table sts_personal(nic varchar(15) unique,uid integer ,first_name varchar(30),last_name varchar(30),address varchar (100),phone_no varchar(16),constraint fk_personal foreign key(uid) references sts_user (uid),constraint pk_personal primary key (nic,uid));";
    private static String create_sts_card = "create table sts_card(number bigint unique,uid integer,year integer,month integer,cvc integer, holder varchar(30), constraint pk_card primary key (number,uid),constraint fk_card foreign key (uid)references sts_user(uid));";
    private static String create_sts_lend = "create table sts_lend(lid serial unique,uid integer,constraint pk_lend primary key (lid,uid),constraint fk_lend foreign key (uid) references sts_user(uid));";
    private static String create_sts_item = "create table sts_item(iid serial unique,lid integer,category varchar(30),description varchar(255),name varchar(30),price integer,height float,length float,width float,constraint fk_item foreign key (lid) references sts_lend (lid),constraint pk_item primary key (iid,lid,name));";
    private static String create_sts_borrow = "create table sts_borrow(bid serial unique,uid integer,constraint pk_borrow primary key (uid,bid),constraint fk_borrow foreign key (uid) references sts_user (uid));";
    private static String create_sts_locker = "create table sts_locker(loid serial unique ,name varchar(30),username varchar(30),password varchar(30), availability integer,height integer ,length integer,width integer,constraint pk_lock primary key(loid));";
    private static String create_sts_share = "create table sts_share(bid integer,iid integer,loid integer,vid integer, target char (1), phase char(1), start_d date, end_d date, payment integer, number bigint, complete integer, finished integer, constraint pk_share primary key(bid,iid,loid,vid),constraint fk_share_user foreign key (bid)references sts_borrow(bid),constraint fk_share_item foreign key (iid)references sts_item(iid),constraint fk_share_lock foreign key (loid)references sts_locker(loid))";

    public static sts_user STS_USER = new sts_user();
    public static sts_reply STS_REPLY = new sts_reply();
    public static sts_verify STS_VERIFY = new sts_verify();
    public static sts_personal STS_PERSONAL = new sts_personal();
    public static sts_lend STS_LEND = new sts_lend();
    public static sts_borrow STS_BORROW = new sts_borrow();
    public static sts_item STS_ITEM = new sts_item();
    public static sts_locker STS_LOCKER = new sts_locker();
    public static sts_share STS_SHARE = new sts_share();
    public static sts_card STS_CARD = new sts_card();

    public static void setupDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url); // JDBC database connection url
        config.setUsername(username);   // psql username
        config.setPassword(password);   // psql password
        config.setMaximumPoolSize(60);  // maximum connections from JDBC database
        config.setMinimumIdle(10);  // minimum connections allowed from JDBC database
        config.setIdleTimeout(300000); // 5 minutes 
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(30000); // 30 seconds
        config.setLeakDetectionThreshold(2000);
        dataSource = new HikariDataSource(config);
    }

    public static void shutdownDataSource(Connection con){
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public static void initialize(){
        setupDataSource();
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!isExist("sts_user"))
            createTable(create_sts_user);

        if(!isExist("sts_reply"))
            createTable(create_sts_reply);

        if(!isExist("sts_verify"))
            createTable(create_sts_verify);

        if(!isExist("sts_personal"))
            createTable(create_sts_personal);

        if(!isExist("sts_card"))
            createTable(create_sts_card);

        if(!isExist("sts_lend"))
            createTable(create_sts_lend);

        if(!isExist("sts_item"))
            createTable(create_sts_item);

        if(!isExist("sts_borrow"))
            createTable(create_sts_borrow);

        if(!isExist("sts_locker"))
            createTable(create_sts_locker);

        if(!isExist("sts_share"))
            createTable(create_sts_share);    
    }

    public static boolean isExist(String tName){
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(checkquery);
            preparedStatement.setString(1, tName);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return result.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void createTable(String sql){
        try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
