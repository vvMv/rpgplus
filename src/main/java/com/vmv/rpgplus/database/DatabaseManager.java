package com.vmv.rpgplus.database;

import com.vmv.core.database.Database;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    public static DatabaseManager instance;
    private static Database database;

    public DatabaseManager(Plugin plugin) {
        instance = this;
        database = new Database(plugin, "rpg.db", plugin.getDataFolder());
        createTables();
    }

    public void createTables() {

        List<String> queries = new ArrayList<>();
        getDatabase().executeSQL("CREATE TABLE IF NOT EXISTS [player_experience] (uuid VARCHAR PRIMARY KEY NOT NULL)", false, false); //must be created before altered
        getDatabase().executeSQL("CREATE TABLE IF NOT EXISTS [player_settings] (uuid VARCHAR PRIMARY KEY NOT NULL)", false, false); //must be created before altered

        //alters player_experience to add all skills, updates current database if new skill is added
        for (SkillType skill : SkillType.values()) {
            queries.add("ALTER TABLE player_experience ADD " + skill.toString().toLowerCase() + " DOUBLE DEFAULT 0");
        }

        //create database entry based of player settings enum and set its default value
        for (PlayerSetting setting : PlayerSetting.values()) {
            queries.add("ALTER TABLE player_settings ADD " + setting.toString().toLowerCase() + " DOUBLE DEFAULT " + setting.getDefaultValue());
        }

        for (String sql : queries) {
            getDatabase().executeSQL(sql, false, false);
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }

    public Database getDatabase() {
        return database;
    }
}
