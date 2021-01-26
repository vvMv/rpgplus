package com.vmv.rpgplus.database;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DatabaseUtils {

    public static Database databaseManager;

    public static List<String> expDataToSave = new ArrayList<String>();
    public static List<String> settingDataToSave = new ArrayList<String>();
    public static List<String> pointDataToSave = new ArrayList<String>();

    public static Database getDatabaseManager() {

        if (databaseManager != null) {
            return databaseManager;
        }

        Plugin plugin = RPGPlus.getInstance();
        FileConfiguration config = plugin.getConfig();
        String ip = config.getString("database.host");
        String port = config.getString("database.port");
        String database = config.getString("database.database");
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        databaseManager = config.getBoolean("database.mysql") ? new DatabaseManagerMySQL(RPGPlus.getInstance(), ip, port, database, username, password) : new DatabaseManagerSQLLite(plugin, "rpg.db", plugin.getDataFolder());
        InformationHandler.printMessage(InformationType.INFO, "Registered database manager using " + (config.getBoolean("database.mysql") ? "MySQL" : "SQLLite"));
        databaseManager.createTables();
        loadUUIDs(databaseManager.fetchUUIDs());
        Bukkit.getOnlinePlayers().forEach(DatabaseUtils::loadPlayer);
        return databaseManager;
    }

    private static void loadUUIDs(ResultSet uuids) {
        try {
            while (uuids.next()) {
                RPGPlayerManager.getInstance().addUUID(UUID.fromString(uuids.getString("uuid")));
            }
            uuids.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        InformationHandler.printMessage(InformationType.INFO, "Found " + RPGPlayerManager.getInstance().getPlayerUUIDs().size() + " RPG Players");
    }

    public static void loadPlayer(Player p) {
        for (RPGPlayer loadedPlayer : RPGPlayerManager.getInstance().getLoadedPlayers()) { if (loadedPlayer.getUuid().equals(p.getUniqueId())) return; }
        long b = System.currentTimeMillis();
        if (!RPGPlayerManager.getInstance().getPlayerUUIDs().contains(p.getUniqueId())) {

            if (FileManager.getConfig().getBoolean("general.database_messages")) InformationHandler.printMessage(InformationType.INFO, "Creating database record for " + p.getName());
            HashMap<SkillType, Double> xp = new HashMap<SkillType, Double>();
            HashMap<PlayerSetting, Boolean> settings = new HashMap<PlayerSetting, Boolean>();
            HashMap<String, Double> pointAllocations = new HashMap<String, Double>();
            for (SkillType s : SkillType.values()) {
                xp.put(s, 0.0);
            }
            for (PlayerSetting setting : PlayerSetting.values()) {
                settings.put(setting, setting.getDefaultValue());
            }
            for (Ability ability : AbilityManager.getInstance().getAbilities()) {
                for (AbilityAttribute attribute : ability.getAttributes()) {
                    String loc = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
                    pointAllocations.put(loc, 0.0);
                }
            }
            AbilityManager.getInstance().getAbilities().forEach(ability -> ability.getAttributes().forEach(attribute -> pointAllocations.put(ability.toString() + ":" + attribute.toString(), 0.0)));
            RPGPlayerManager.getInstance().loadPlayer(new RPGPlayer(p.getUniqueId(), xp, settings, pointAllocations));
            RPGPlayerManager.getInstance().addUUID(p.getUniqueId());
            DatabaseUtils.getDatabaseManager().newPlayer(p.getUniqueId());
        } else {
            RPGPlayerManager.getInstance().loadPlayer(getDatabaseManager().fetchPlayer(p.getUniqueId()));
        }
        InformationHandler.printMessage(InformationType.INFO, "Loaded player profile for " + p.getName() + ", took " + (System.currentTimeMillis() - b + "ms"));
    }

    public static void unloadPlayer(Player p) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(p);
        getDatabaseManager().savePlayer(rp, true);
        RPGPlayerManager.getInstance().getLoadedPlayers().remove(rp);
    }

    public static List<String> getExpDataToSave() {
        return expDataToSave;
    }

    public static List<String> getPointDataToSave() {
        return pointDataToSave;
    }

    public static List<String> getSettingDataToSave() {
        return settingDataToSave;
    }

}
