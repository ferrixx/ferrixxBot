package de.ferrixx.bot.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class CoinSystem {

    public static boolean isUserExists(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT coins FROM users WHERE discordID="+discordID+";");
        try {
            return rs.next();
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void updateCoins(String discordID, Integer coins) {
        int newcoins = getCoins(discordID)+coins;
        if(isUserExists(discordID)) {
            MySQL.update("UPDATE users SET coins="+newcoins+" WHERE discordID="+discordID+";");
        } else {
            MySQL.update("INSERT INTO users(discordID, coins) VALUES ('"+discordID+"', '"+coins+"');");

        }
    }

    public static void addLevel(String discordID, Integer level) {
        MySQL.update("UPDATE users SET level="+level+" WHERE discordID="+discordID+";");
    }

    public static Integer getCoins(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT coins FROM users WHERE discordID='" + discordID + "'");

        try {
            rs.next();
            return rs.getInt("coins");
        } catch (SQLException e) {
            return 0;
        }
    }

    public static Integer getLevel(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT level FROM users WHERE discordID='" + discordID + "'");

        try {
            rs.next();
            return rs.getInt("level");
        } catch (SQLException e) {
            return 0;
        }
    }

}
