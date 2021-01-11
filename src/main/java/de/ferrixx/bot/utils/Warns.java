package de.ferrixx.bot.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class Warns {

    public static void addWarn(String discordID, String reason, String message, String creator) {
        MySQL.update("INSERT INTO warns(discordID, reason, message, creator) VALUES ('"+discordID+"', '"+reason+"', '"+message+"', '"+creator+"');");
    }

    public static Integer getWarns(String discordID) {
        ResultSet rs = MySQL.getResultSet("SELECT COUNT(*) AS warn_count FROM warns WHERE discordID='"+discordID+"'");

        try {
            rs.next();
            return rs.getInt("warn_count");
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
