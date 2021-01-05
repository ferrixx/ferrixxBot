package de.ferrixx.bot.utils;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import java.sql.*;
import java.util.Date;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Mutes {

    public static void addMute(String discordID, String reason, Timestamp unmute, Timestamp created) {
        MySQL.update("INSERT INTO mutes(discordID, reason, unmute, created) VALUES ('"+discordID+"', '"+reason+"', '"+unmute+"', '"+created+"');");
    }

    public static Timestamp getMuteDuration(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT unmute FROM mutes WHERE discordID='"+discordID+"'");

        try {
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
        ResultSet rs = MySQL.getResultSet("SELECT created FROM mutes WHERE discordID='"+discordID+"'");

        try {
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
        MySQL.update("DELETE FROM mutes WHERE discordID='"+discordID+"'");

        try {
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    public static boolean hasMute(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT * FROM mutes WHERE discordID='"+discordID+"'");

        try {
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
