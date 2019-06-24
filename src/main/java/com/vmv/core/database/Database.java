package com.vmv.core.database;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private Plugin plugin;
    public static Database instance;
    public static String url;

    /**
     *
     * @param plugin instance of the main plugin
     * @param fileName what the database file will be called e.g "core.db"
     * @param location where the database will be generated
     */
    public Database(Plugin plugin, String fileName, File location) {
        this(plugin, fileName, location, null);
    }

    /**
     *
     * @param queriesFileLocation location for the queries .sql file to excecute
     */
    public Database(Plugin plugin, String fileName, File location, String queriesFileLocation) {
        this.plugin = plugin;
        url = "jdbc:sqlite:" + location + "/" + fileName;
        instance = this;
        initialize(fileName);
        if (queriesFileLocation != null) {
            try {
                ScriptRunner sr = new ScriptRunner(DriverManager.getConnection(url));
                sr.runScript(new BufferedReader(new FileReader(queriesFileLocation)));
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static Database getInstance() {
        return instance;
    }

    private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void initialize(String url) {
        try (Connection conn = this.connect()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                InformationHandler.printMessage(InformationType.INFO, "Using Database driver " + meta.getDriverName());
                InformationHandler.printMessage(InformationType.INFO, "A database connection has been established.");
                createTables();
            }
        } catch (SQLException e) {
            InformationHandler.printMessage(InformationType.ERROR, "A database connection has not been established.");
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    public void executeSQL(final String sql, boolean aSync) {
        executeSQL(sql, aSync, true);
    }

    public void executeSQL(final String sql, boolean aSync, final boolean debug) {
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = DriverManager.getConnection(url); Statement statement = conn.createStatement()) {
                    statement.execute(sql);
                } catch (SQLException e) {
                    if (debug) {
                        System.out.println(e.getMessage());
                    }
                } finally {
                    try {
                        DriverManager.getConnection(url).close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (aSync) {
            r.runTaskAsynchronously(plugin);
        } else {
            r.runTask(plugin);
        }

    }

    public void insertData(String tablename, String fieldnames, String values, boolean aSync) {
        executeSQL("INSERT INTO " + tablename + " (" + fieldnames + ") VALUES (" + values + ")", aSync);
    }

    public void updateData(String tablename, String fieldname, Object object, String column, String logic_gate, String value) {
        executeSQL("UPDATE " + tablename + " SET " + fieldname + " = '" + object + "' WHERE " + column + logic_gate + "'" + value + "';", true);
    }

    public void deleteData(String tablename, String fieldname, String logic_gate, String value) {
        executeSQL("DELETE FROM " + tablename + " WHERE " + fieldname + " " + logic_gate + " '" + value + "';", true);
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

    public void createTables() {

        //TODO implement use of ScriptRunner when more tables are created
        List<String> queries = new ArrayList<>();
        queries.add("CREATE TABLE IF NOT EXISTS [player_experience] (uuid VARCHAR PRIMARY KEY NOT NULL)");

        for (SkillType skill : SkillType.values()) {
            queries.add("ALTER TABLE player_experience ADD " + skill.toString().toLowerCase() + " DOUBLE DEFAULT 0");
        }

        for (String sql : queries) {
            executeSQL(sql, false, false);
        }
    }
}