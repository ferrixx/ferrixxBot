package de.ferrixx.bot.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class CoinSystem {

    public static boolean isUserExists(String discordID) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("SELECT coins FROM users WHERE discordID=?;");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            return  rs.next();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateCoins(String discordID, Integer coins) {
        int newcoins = getCoins(discordID)+coins;
        if(isUserExists(discordID)) {
            try {
                PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("UPDATE users SET coins=? WHERE discordID=?");
                ps.setInt(1, newcoins);
                ps.setString(2, discordID);
                ps.executeUpdate();
                ps.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try{
                PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("INSERT INTO users(discordID, coins) VALUES (?, ?);");
                ps.setString(1, discordID);
                ps.setInt(2, coins);
                ps.executeUpdate();
                ps.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void addLevel(String discordID, Integer level) {
        MySQL.update("UPDATE users SET level="+level+" WHERE discordID="+discordID+";");
        try{
            PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("UPDATE users SET level=? WHERE discordID=?");
            ps.setInt(1, level);
            ps.setString(2, discordID);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getCoins(String discordID) {

        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("SELECT coins FROM users WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("coins");
        } catch (SQLException e) {
            return 0;
        }
    }

    public static Integer getLevel(String discordID) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getPreparedStatement("SELECT level FROM users WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("level");
        } catch (SQLException e) {
            return 0;
        }
    }

}
