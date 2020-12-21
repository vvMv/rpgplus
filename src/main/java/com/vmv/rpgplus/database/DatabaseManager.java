package com.vmv.rpgplus.database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.sql.*;

public class DatabaseManager {

    private Plugin plugin;
    private static DatabaseManager instance;
    private String url;
    private Connection c = null;
    private static HikariConfig config;
    private static HikariDataSource ds;

    /**
     * SQLLite constructor
     * @param plugin instance of the main plugin
     * @param fileName what the database file will be called e.g "core.db"
     * @param location where the database will be generated
     */
    protected DatabaseManager(Plugin plugin, String fileName, File location) {
        this.plugin = plugin;
        instance = this;
        this.url = "jdbc:sqlite:" + location + "/" + fileName;
    }

    /**
     * MYSQL constructor
     * @param plugin instance of the main plugin
     */
    protected DatabaseManager(Plugin plugin, String ip, String port, String database, String username, String password) {
        this.plugin = plugin;
        instance = this;
        config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port + "/" + database + "?user=" + username + "&password=" + password);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty( "cachePrepStmts" , "true" );
        config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        try {
            ds = new HikariDataSource(config);
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "Please check your MySQL credentials are entered correctly in config.yml");
            InformationHandler.printMessage(InformationType.ERROR, "If you're unfamiliar with MYSQL please set database.mysql to false");
            InformationHandler.printMessage(InformationType.ERROR, "The plugin has been disabled as there is no connection to the database.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    protected static DatabaseManager getInstance() {
        return instance;
    }

    protected Connection getConnection() throws SQLException {
        if (config != null) {
            return ds.getConnection();
        } else {
            if (c == null) {
                c = (Connection) DriverManager.getConnection(url);
            } else {
                c.close();
                c = (Connection) DriverManager.getConnection(url);
            }
            return c;
        }
    }

    protected void executeSQL(final String sql, boolean aSync) {
        executeSQL(sql, aSync, true);
    }

    protected void executeSQL(final String sql, boolean aSync, final boolean debug) {
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
            try (Connection conn = getConnection(); Statement statement = conn.createStatement()) {
                statement.execute(sql);
            } catch (MySQLSyntaxErrorException e) {
                return;
            } catch (SQLException ex) {
                if (config != null) ex.printStackTrace(); //Ignoring sqllite errors as doesnt support alter table if not exists so it will error with "already exists"
            }
        }

    }

    protected ResultSet selectData(String sql) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void insertData(String tablename, String fieldnames, String values, boolean aSync) {
        executeSQL("INSERT INTO " + tablename + " (" + fieldnames + ") VALUES (" + values + ");", aSync);
    }

    public void updateData(String tablename, String fieldname, Object object, String column, String logic_gate, String value, boolean aSync) {
        executeSQL("UPDATE " + tablename + " SET " + fieldname + " = '" + object + "' WHERE " + column + logic_gate + "'" + value + "';", aSync);
    }

    public void deleteData(String tablename, String fieldname, String logic_gate, String value, boolean aSync) {
        executeSQL("DELETE FROM " + tablename + " WHERE " + fieldname + " " + logic_gate + " '" + value + "';", aSync);
    }
}