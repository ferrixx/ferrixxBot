package de.ferrixx.bot.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.util.Date;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Mutes {

    public static void addMute(String discordID, String reason, Timestamp unmute, Timestamp created) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("INSERT INTO mutes(discordID, reason, unmute, created) VALUES (? ,? ,? ,? );");
            ps.setString(1, discordID);
            ps.setString(2, reason);
            ps.setTimestamp(3, unmute);
            ps.setTimestamp(4, created);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Timestamp getMuteDuration(String discordID) {

        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("SELECT unmute FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if(!rs.getString("unmute").isEmpty() || rs.getString("unmute") != null || rs != null) {
                Timestamp muteduration = Timestamp.valueOf(rs.getString("unmute"));
                return muteduration;
            } else {
                return null;
            }
        }catch (SQLException e) {
            return null;
        }
    }

    public static Timestamp getCreatedTime(String discordID) {

        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("SELECT created FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            Timestamp created = Timestamp.valueOf(rs.getString("created"));
            if(created == null) {
                return null;
            }
            return created;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkifMuted(String discordID) {
        Date currentdate = new Date();
        Timestamp currenttime = new Timestamp(currentdate.getTime());
        if(getMuteDuration(discordID) == null) {
            return false;
        } else {
            if(currenttime.compareTo(getMuteDuration(discordID)) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean deleteMute(String discordID) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("DELETE FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ps.executeQuery();
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean hasMute(String discordID) {

        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("SELECT * FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if(rs.getString("discordID") != null) {
                return true;
            } else {
                return false;
            }
        }catch (SQLException e) {
            return false;
        }
    }

}
