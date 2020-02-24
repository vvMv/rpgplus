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
    public List<String> expDataToSave, settingDataToSave;
    private Plugin plugin;

    public DatabaseManager(Plugin plugin) {
        instance = this;
        database = new Database(plugin, "rpg.db", plugin.getDataFolder());
        this.plugin = plugin;
        expDataToSave = new ArrayList<String>();
        settingDataToSave = new ArrayList<String>();
        createTables();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> savePlayerData(true), RPGPlus.getInstance().getConfig().getLong("general.save_data"),  RPGPlus.getInstance().getConfig().getLong("general.save_data"));
    }

    public void savePlayerData(boolean aSync) {
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Saving player data... [" + (settingDataToSave.size() + expDataToSave.size()) + "]");
        Instant start = Instant.now();

        expDataToSave.forEach(data -> {
            String uuid = data.split(":")[0];
            String skill = data.split(":")[1].toUpperCase();
            Database.getInstance().updateData("player_experience", skill, RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getExperience(SkillType.valueOf(skill)), "uuid", "=", uuid, aSync);
        });

        settingDataToSave.forEach(data -> {
            String uuid = data.split(":")[0];
            String setting = data.split(":")[1].toUpperCase();
            Database.getInstance().updateData("player_settings", setting, RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getSettingValue(PlayerSetting.valueOf(setting)), "uuid", "=", uuid, aSync);
        });

        Instant finish = Instant.now();
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Finished! Took " + Duration.between(start, finish).toMillis() + "ms.");
        expDataToSave.clear();
        settingDataToSave.clear();
    }

    public List<String> getExpDataToSave() {
        return expDataToSave;
    }


    public List<String> getSettingDataToSave() {
        return settingDataToSave;
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
