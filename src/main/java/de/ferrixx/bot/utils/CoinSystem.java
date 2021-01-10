package de.ferrixx.bot.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class CoinSystem {

    public static boolean isUserExists(String discordID) {
        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT coins FROM users WHERE discordID=?;");
            try {
                ps.setString(1, discordID);
            } catch(NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            }
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateCoins(String discordID, Integer coins) {
        int newcoins = getCoins(discordID) + coins;
        if (isUserExists(discordID)) {
            try {
                PreparedStatement ps = MySQL.getPreparedStatement("UPDATE users SET coins=? WHERE discordID=?");
                try {
                    ps.setInt(1, newcoins);
                } catch(NullPointerException nullPointerException) {
                    nullPointerException.printStackTrace();
                }
                ps.setString(2, discordID);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            try {
                PreparedStatement ps = MySQL.getPreparedStatement("INSERT INTO users(discordID, coins) VALUES (?, ?);");
                try {
                    ps.setString(1, discordID);
                } catch(NullPointerException nullPointerException) {
                    nullPointerException.printStackTrace();
                }
                ps.setInt(2, coins);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    public static void addLevel(String discordID, Integer level) {
        MySQL.update("UPDATE users SET level=" + level + " WHERE discordID=" + discordID + ";");
        try {
            PreparedStatement ps = MySQL.getPreparedStatement("UPDATE users SET level=? WHERE discordID=?");
            try {
                ps.setInt(1, level);
            } catch(NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            }
            ps.setString(2, discordID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getCoins(String discordID) {

        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT coins FROM users WHERE discordID=?");
            try {
                ps.setString(1, discordID);
            } catch(NullPointerException nullPointerException) {
                nullPointerException.printStackTrace();
            }
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("coins");
        } catch (SQLException e) {
            return 0;
        }
    }

    public static Integer getLevel(String discordID) {
        try {
            PreparedStatement ps = MySQL.getPreparedStatement("SELECT level FROM users WHERE discordID=?");
            ps.setString(1, discordID);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("level");
        } catch (SQLException e) {
            return 0;
        }
    }

}
