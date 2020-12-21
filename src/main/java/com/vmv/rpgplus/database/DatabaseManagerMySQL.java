package com.vmv.rpgplus.database;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseManagerMySQL extends DatabaseManager implements Database {

    public DatabaseManagerMySQL(Plugin plugin, String ip, String port, String database, String username, String password) {
        super(plugin, ip, port, database, username, password);
    }

    @Override
    public void createTables() {
        InformationHandler.printMessage(InformationType.DEBUG, "Checking database tables...");
        executeSQL("CREATE TABLE IF NOT EXISTS player_experience (uuid CHAR(36) NOT NULL, PRIMARY KEY (uuid))", false);
        executeSQL("CREATE TABLE IF NOT EXISTS player_settings (uuid CHAR(36) NOT NULL, PRIMARY KEY (uuid))", false);
        executeSQL("CREATE TABLE IF NOT EXISTS player_allocations (uuid CHAR(36) NOT NULL, PRIMARY KEY (uuid))", false);

        List<String> queries = Arrays.stream(SkillType.values()).map(skill -> "ALTER TABLE player_experience ADD " + skill.toString().toLowerCase() + " DOUBLE DEFAULT 0").collect(Collectors.toList());
        Arrays.stream(PlayerSetting.values()).map(setting -> "ALTER TABLE player_settings ADD " + setting.toString().toLowerCase() + " DOUBLE DEFAULT " + (setting.getDefaultValue() ? "1.0" : "0.0")).forEach(queries::add);
        AbilityManager.getInstance().getAbilities().forEach(ability -> ability.getAttributes().stream().map(attribute -> "ALTER TABLE player_allocations ADD `" + ability.getName().toLowerCase() + " " + attribute.name().toLowerCase() + "` DOUBLE DEFAULT 0").forEach(queries::add));

        queries.forEach(q -> InformationHandler.printMessage(InformationType.DEBUG, q));

        queries.forEach(sql -> executeSQL(sql, false, false));
    }

    @Override
    public void newPlayer(UUID uuid) {
        executeSQL("INSERT IGNORE INTO player_experience(uuid) VALUES('" + uuid + "')", true);
        executeSQL("INSERT IGNORE INTO player_settings(uuid) VALUES('" + uuid + "')", true);
        executeSQL("INSERT IGNORE INTO player_allocations(uuid) VALUES('" + uuid + "')", true);
    }

    @Override
    public boolean savePlayers(boolean aSync) {
        if (DatabaseUtils.getExpDataToSave().isEmpty() && DatabaseUtils.getSettingDataToSave().isEmpty() && DatabaseUtils.getPointDataToSave().isEmpty()) return true;
        Instant start = Instant.now();

        for (RPGPlayer loadedPlayer : RPGPlayerManager.getInstance().getLoadedPlayers()) {
            savePlayer(loadedPlayer, aSync);
        }

        Instant finish = Instant.now();
        if (RPGPlus.getInstance().getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Player data saved![" + (DatabaseUtils.getSettingDataToSave().size() + DatabaseUtils.getExpDataToSave().size() + DatabaseUtils.getPointDataToSave().size()) + "] Took " + Duration.between(start, finish).toMillis() + "ms.");
        DatabaseUtils.getExpDataToSave().clear();
        DatabaseUtils.getSettingDataToSave().clear();
        DatabaseUtils.getPointDataToSave().clear();

        return true;
    }

    @Override
    public boolean savePlayer(RPGPlayer player, boolean aSync) {

        List<String> toRemoveExp = new ArrayList<>();
        List<String> toRemoveSetting = new ArrayList<>();
        List<String> toRemovePoint = new ArrayList<>();

        DatabaseUtils.getExpDataToSave().forEach(data -> {
            String uuid = data.split(":")[0];
            String skill = data.split(":")[1].toUpperCase();
            if (uuid.equals(player.getUuid().toString())) {
                updateData("player_experience", skill, RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getExperience(SkillType.valueOf(skill)), "uuid", "=", uuid, aSync);
                toRemoveExp.add(data);
            }
        });

        DatabaseUtils.getSettingDataToSave().forEach(data -> {
            String uuid = data.split(":")[0];
            String setting = data.split(":")[1].toUpperCase();
            if (uuid.equals(player.getUuid().toString())) {
                updateData("player_settings", setting, RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getSettingValue(PlayerSetting.valueOf(setting)) ? "1.0" : "0.0", "uuid", "=", uuid, aSync);
                toRemoveSetting.add(data);
            }
        });

        DatabaseUtils.getPointDataToSave().forEach(data -> {
            String uuid = data.split(":")[0].toLowerCase();
            String ability = data.split(":")[1].toLowerCase();
            String attribute = data.split(":")[2].toLowerCase();
            double points = RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getPointAllocation(AbilityManager.getInstance().getAbility(ability), AbilityAttribute.valueOf(attribute.toUpperCase()));
            if (uuid.equals(player.getUuid().toString())) {
                updateData("player_allocations", "'" + ability + " " + attribute + "'", points, "uuid", "=", uuid, aSync);
                toRemovePoint.add(data);
            }
        });

        DatabaseUtils.getExpDataToSave().removeAll(toRemoveExp);
        DatabaseUtils.getSettingDataToSave().removeAll(toRemoveSetting);
        DatabaseUtils.getPointDataToSave().removeAll(toRemovePoint);

        InformationHandler.printMessage(InformationType.INFO, "Saved player " + player.getUuid());

        return true;
    }

    @Override
    public RPGPlayer fetchPlayer(UUID uuid) {

        long time = System.currentTimeMillis();

        try {
            HashMap<SkillType, Double> xp = new HashMap<>();
            HashMap<PlayerSetting, Boolean> settings = new HashMap<>();
            HashMap<String, Double> pointAllocations = new HashMap<>();

            ResultSet experienceData = selectData("SELECT * FROM player_experience WHERE uuid = '" + uuid.toString() + "'");

            while (experienceData.next()) {
                for (SkillType s : SkillType.values()) {
                    xp.put(s, experienceData.getDouble(s.toString().toLowerCase()));
                }
            }

            ResultSet settingsData = selectData("SELECT * FROM player_settings WHERE uuid = '" + uuid.toString() + "'");
            while (settingsData.next()) {
                for (PlayerSetting setting : PlayerSetting.values()) {
                    settings.put(setting, settingsData.getString(setting.toString().toLowerCase()).equals("1.0"));
                }
            }

            ResultSet pointsData = selectData("SELECT * FROM player_allocations WHERE uuid = '" + uuid.toString() + "'");
            while (pointsData.next()) {
                for (Ability ability : AbilityManager.getInstance().getAbilities()) {
                    for (AbilityAttribute attribute : ability.getAttributes()) {
                        String loc = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
                        String qName = ability.getName().toLowerCase() + " " + attribute.name().toLowerCase();
                        pointAllocations.put(loc, pointsData.getDouble(qName));
                    }
                }
            }

            experienceData.close();
            settingsData.close();
            pointsData.close();

            return new RPGPlayer(uuid, xp, settings, pointAllocations);

        } catch (SQLException e) {

            InformationHandler.printMessage(InformationType.ERROR, "There was an error with the database");
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public ResultSet fetchUUIDs() {
        return selectData("SELECT * FROM player_experience");
    }

    @Override
    public void onDisable() {
        savePlayers(false);
    }
}
