package de.ferrixx.bot.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Mutes {

    public static void addMute(String discordID, String reason, Timestamp unmute, Timestamp created) {
        try {
            PreparedStatement ps = MySQL.getPreparedStatement("INSERT INTO mutes(discordID, reason, unmute, created) VALUES (? ,? ,? ,? );");
            ps.setString(1, discordID);
            ps.setString(2, reason);
            ps.setTimestamp(3, unmute);
            ps.setTimestamp(4, created);
            ps.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Timestamp getMuteDuration(String discordID) {

        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT unmute FROM mutes WHERE discordID=?");

            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Timestamp.valueOf(rs.getString("unmute"));
            }
            return null;
        } catch (SQLException e) {
            return null;
        }
    }

    public static Timestamp getCreatedTime(String discordID) {

        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT created FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return Timestamp.valueOf(rs.getString("created"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkifMuted(String discordID) {
        Date currentdate = new Date();
        Timestamp currenttime = new Timestamp(currentdate.getTime());
        if (getMuteDuration(discordID) == null) {
            return false;
        } else {
            return currenttime.compareTo(getMuteDuration(discordID)) > 0;
        }
    }

    public static void deleteMute(String discordID) {
        try {
            PreparedStatement ps = MySQL.getPreparedStatement("DELETE FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasMute(String discordID) {

        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT * FROM mutes WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getString("discordID") != null;
        } catch (SQLException e) {
            return false;
        }
    }

}
