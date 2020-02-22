package com.vmv.rpgplus.database;

import com.vmv.core.database.Database;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    public static DatabaseManager instance;
    private static Database database;
    public List<String> dataToSave;
    private Plugin plugin;

    public DatabaseManager(Plugin plugin) {
        instance = this;
        database = new Database(plugin, "rpg.db", plugin.getDataFolder());
        this.plugin = plugin;
        dataToSave = new ArrayList<String>();
        createTables();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> savePlayerData(true), RPGPlus.getInstance().getConfig().getLong("general.save_data"),  RPGPlus.getInstance().getConfig().getLong("general.save_data"));
    }

    public void savePlayerData(boolean aSync) {
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Saving player data... [" + dataToSave.size() + "]");
        Instant start = Instant.now();

        dataToSave.forEach(data -> {
            String uuid = data.split(":")[0];
            String skill = data.split(":")[1];
            Database.getInstance().updateData("player_experience", skill, RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getExperience(SkillType.valueOf(skill.toUpperCase())), "uuid", "=", uuid, aSync);
        });

        Instant finish = Instant.now();
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Finished! Took " + Duration.between(start, finish).toMillis() + "ms.");
        dataToSave.clear();
    }

    public List<String> getDataToSave() {
        return dataToSave;
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
