package de.ferrixx.bot.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Warns {

    public static void addWarn(String discordID, String reason, String message, String creator) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("INSERT INTO warns(discordID, reason, message, creator) VALUES (?, ?, ?, ?);");
            ps.setString(1, discordID);
            ps.setString(2, reason);
            ps.setString(3, message);
            ps.setString(4, creator);
            ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Integer getWarns(String discordID) {
        try {
            PreparedStatement ps = (PreparedStatement) MySQL.getResultSet("SELECT COUNT(*) AS warn_count FROM warns WHERE discordID=?");
            ps.setString(1, discordID); // Possible NullPointerException
            ResultSet rs = ps.executeQuery();
            return rs.getInt("warn_count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
