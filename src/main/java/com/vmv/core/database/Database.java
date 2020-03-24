package com.vmv.core.database;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;

public class Database {

    private Plugin plugin;
    public static Database instance;
    public static String url;
    private Connection c = null;

    /**
     *
     * @param plugin instance of the main plugin
     * @param fileName what the database file will be called e.g "core.db"
     * @param location where the database will be generated
     */
    public Database(Plugin plugin, String fileName, File location) {
        this.plugin = plugin;
        url = "jdbc:sqlite:" + location + "/" + fileName;
        instance = this;
    }

    public static Database getInstance() {
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (c == null) {
            c = (Connection) DriverManager.getConnection(url);
        } else {
            c.close();
            c = (Connection) DriverManager.getConnection(url);
        }
        return c;
    }

    public void executeSQL(final String sql, boolean aSync) {
        executeSQL(sql, aSync, true);
    }

    public void executeSQL(final String sql, boolean aSync, final boolean debug) {
        if (aSync) {
            BukkitRunnable r = new BukkitRunnable() {
                @Override
                public void run() {
                    try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
                        statement.execute(sql);
                    } catch (SQLException e) {
                        if (debug) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
            };
            r.runTaskAsynchronously(plugin);
        } else {
            try (Connection conn = DriverManager.getConnection(url); Statement statement = conn.createStatement()) {
                statement.execute(sql);
            } catch (SQLException e) {
                return; //Returning all errors here because sqllite doesnt support alter table if not exists so it will error with "already exists"
            }
        }

    }

    public void insertData(String tablename, String fieldnames, String values, boolean aSync) {
        executeSQL("INSERT INTO " + tablename + " (" + fieldnames + ") VALUES (" + values + ");", aSync);
    }

    public void updateData(String tablename, String fieldname, Object object, String column, String logic_gate, String value, boolean aSync) {
        String sql = "UPDATE " + tablename + " SET " + fieldname + " = '" + object + "' WHERE " + column + logic_gate + "'" + value + "';";
        executeSQL(sql, aSync);
    }

    public void deleteData(String tablename, String fieldname, String logic_gate, String value, boolean aSync) {
        executeSQL("DELETE FROM " + tablename + " WHERE " + fieldname + " " + logic_gate + " '" + value + "';", aSync);
    }

    public ResultSet selectData(String sql) {
        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}