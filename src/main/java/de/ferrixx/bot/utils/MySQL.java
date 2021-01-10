package de.ferrixx.bot.utils;

import de.ferrixx.bot.settings.Privates;

import java.sql.*;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class MySQL {

    public static Connection con;

    public static void devconnect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord?characterEncoding=utf8&autoReconnect=true", Privates.username, Privates.devpassword);
                System.out.println("Datenbank Verbindung aufgebaut!");
            } catch (SQLException e) {
                System.out.println("Fehler beim Aufbau der Datenbank Verbindung!" + e.getMessage());
            }
        }
    }

    public static void connect() {
        if (!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord?characterEncoding=utf8&autoReconnect=true", Privates.username, Privates.password);
                System.out.println("Datenbank Verbindung aufgebaut!");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Fehler beim Aufbau der Datenbank Verbindung!" + e.getMessage());
            }
        }
    }

    public static void close() {
        if (isConnected()) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createTable() {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `discord`.`mutes` ( `id` INT NOT NULL AUTO_INCREMENT , `discordID` TEXT NOT NULL , `reason` TEXT NOT NULL , `unmute` TEXT NOT NULL , `created` TEXT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `discord`.`users` ( `discordID` TEXT NOT NULL , `coins` INT NOT NULL DEFAULT '0' , `level` INT NOT NULL DEFAULT '0' , PRIMARY KEY (`discordID`)) ENGINE = InnoDB;");
                con.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS `discord`.`warns` ( `id` INT NOT NULL AUTO_INCREMENT , `discordID` TEXT NOT NULL , `reason` INT NOT NULL , `message` INT NOT NULL , `creator` VARCHAR(255) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isConnected() {
        return (con != null);
    }

    public static Connection getConnection() {
        return con;
    }

    public static void update(String qry) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultSet getResultSet(String qry) {
        if (isConnected()) {
            try {
                PreparedStatement ps = con.prepareStatement(qry);
                return ps.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static PreparedStatement getPreparedStatement(String qry) {
        if (isConnected()) {
            try {
                return con.prepareStatement(qry);
            } catch (SQLException e) {

            }
        }
        return null;
    }

}
