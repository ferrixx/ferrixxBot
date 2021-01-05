package de.ferrixx.bot.utils;

import de.ferrixx.bot.settings.privates;

import java.sql.*;
/*
 * Copyright (c) 2020 ferrixxDE - Justin Ippen
 */

public class MySQL {

    public static Connection con;

    public static void devconnect() {
        if(!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord?characterEncoding=utf8&autoReconnect=true", privates.username, privates.devpassword);
                System.out.println("Datenbank Verbindung aufgebaut!");
            }catch (SQLException e) {
                System.out.println("Fehler beim Aufbau der Datenbank Verbindung!" + e.getMessage());
            }
        }
    }

    public static void connect() {
        if(!isConnected()) {
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/discord?characterEncoding=utf8&autoReconnect=true", privates.username, privates.password);
                System.out.println("Datenbank Verbindung aufgebaut!");
            }catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Fehler beim Aufbau der Datenbank Verbindung!" + e.getMessage());
            }
        }
    }

    public static void close() {
        if(isConnected()) {
            try {
                con.close();
            } catch(SQLException e) {
            }
        }
    }

    public static boolean isConnected() {
        return (con == null ? false : true);
    }

    public static Connection getConnection() {
        return con;
    }

    public static void update(String qry) {
        if(isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch(SQLException e) {
            }
        }
    }

    public static ResultSet getResultSet(String qry) {
        if(isConnected()) {
            try {
                PreparedStatement ps = con.prepareStatement(qry);
                return ps.executeQuery();
            } catch(SQLException e) {
            }
        }
        return null;
    }

}
