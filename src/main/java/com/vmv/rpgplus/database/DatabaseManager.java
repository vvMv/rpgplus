package com.vmv.rpgplus.database;

import com.vmv.core.database.Database;
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
import org.bukkit.plugin.Plugin;

import javax.management.Attribute;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    public static DatabaseManager instance;
    private static Database database;
    public List<String> expDataToSave = new ArrayList<String>(), settingDataToSave = new ArrayList<String>(), pointDataToSave = new ArrayList<String>();
    private Plugin plugin;

    public DatabaseManager(Plugin plugin) {
        this.instance = this;
        this.plugin = plugin;
        this.database = new Database(plugin, "rpg.db", plugin.getDataFolder());
        this.createTables();
        this.registerPlayers();
        //this.pruneTables();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> savePlayerData(true), RPGPlus.getInstance().getConfig().getLong("general.save_data"),  RPGPlus.getInstance().getConfig().getLong("general.save_data"));
    }

    public void savePlayerData(boolean aSync) {
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Saving player data... [" + (settingDataToSave.size() + expDataToSave.size() + pointDataToSave.size()) + "]");
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

        pointDataToSave.forEach(data -> {
            String uuid = data.split(":")[0].toLowerCase();
            String ability = data.split(":")[1].toLowerCase();
            String attribute = data.split(":")[2].toLowerCase();
            double points = RPGPlayerManager.getInstance().getPlayer(UUID.fromString(uuid)).getPointAllocation(AbilityManager.getAbility(ability), AbilityAttribute.valueOf(attribute.toUpperCase()));
            Database.getInstance().updateData("player_allocations", "'" + ability + " " + attribute + "'", points, "uuid", "=", uuid, aSync);


            //Database.getInstance().insertData("player_point_allocations", "uuid, ability, attribute, points", "'" + uuid + "', '" + ability + "', '" + attribute + "', '" + points + "'", aSync);
            //Database.getInstance().executeSQL("insert into player_point_allocations(uuid, ability, attribute, points) VALUES('" + uuid + "', '" + ability + "', '" + attribute + "', '" + points + "');", aSync);
            //Database.getInstance().executeSQL("update player_point_allocations set points='" + points + "' where uuid = '" + uuid + "' AND " + "ability = '" + ability + "' AND " + "attribute = '" + attribute + "'" +
            //        " IF @@ROWCOUNT=0 " + "insert into player_point_allocations(uuid, ability, attribute, points) VALUES('" + uuid + "', '" + ability + "', '" + attribute + "', '" + points + "');"
             //       , aSync);
            //            Database.getInstance().executeSQL("IF EXISTS(select * from player_point_allocations where uuid = '" + uuid + "' AND " + "ability = '" + ability + "' AND " + "attribute = '" + attribute + "'" + ") " +
//                    "update player_point_allocations set points = '" + points + "' where uuid = '" + uuid + "' AND " + "ability = '" + ability + "' AND " + "attribute = '" + attribute + "' " +
//                    "ELSE " +
//                    "insert into player_point_allocations(uuid, ability, attribute, points) VALUES('" + uuid + "', '" + ability + "', '" + attribute + "', '" + points + "');", aSync);
            //Database.getInstance().executeSQL("INSERT OR UPDATE INTO player_point_allocations(uuid, ability, attribute, points) VALUES('" + uuid + "', '" + ability + "', '" + attribute + "', '" + points + "');", aSync);
        });

        Instant finish = Instant.now();
        if (plugin.getConfig().getBoolean("general.save_messages")) InformationHandler.printMessage(InformationType.INFO, "Finished! Took " + Duration.between(start, finish).toMillis() + "ms.");
        expDataToSave.clear();
        settingDataToSave.clear();
        pointDataToSave.clear();
    }

    public List<String> getExpDataToSave() {
        return expDataToSave;
    }

    public List<String> getPointDataToSave() {
        return pointDataToSave;
    }

    public List<String> getSettingDataToSave() {
        return settingDataToSave;
    }

    public void createTables() {

        List<String> queries = new ArrayList<>();
        getDatabase().executeSQL("CREATE TABLE IF NOT EXISTS [player_experience] (uuid VARCHAR PRIMARY KEY NOT NULL)", false, false); //must be created before altered
        getDatabase().executeSQL("CREATE TABLE IF NOT EXISTS [player_settings] (uuid VARCHAR PRIMARY KEY NOT NULL)", false, false); //must be created before altered
        getDatabase().executeSQL("CREATE TABLE IF NOT EXISTS [player_allocations] (uuid VARCHAR PRIMARY KEY NOT NULL)", false, false); //must be created before altered

        //alters player_experience to add all skills, updates current database if new skill is added
        for (SkillType skill : SkillType.values()) {
            queries.add("ALTER TABLE player_experience ADD " + skill.toString().toLowerCase() + " DOUBLE DEFAULT 0");
        }

        //create database entry based of player settings enum and set its default value
        for (PlayerSetting setting : PlayerSetting.values()) {
            queries.add("ALTER TABLE player_settings ADD " + setting.toString().toLowerCase() + " DOUBLE DEFAULT " + setting.getDefaultValue());
        }

        for (Ability ability : AbilityManager.getAbilities()) {
            for (AbilityAttribute attribute : ability.getAttributes()) {
                queries.add("ALTER TABLE player_allocations ADD '" + ability.getName().toLowerCase() + " " + attribute.name().toLowerCase() + "' DOUBLE DEFAULT 0");
            }
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

    private void registerPlayers() {
        ResultSet data = Database.getInstance().selectData("SELECT uuid FROM player_experience");

        Instant start = Instant.now();
        int c = 0;
        try {
            while (data.next()) {
                HashMap<SkillType, Double> xp = new HashMap<SkillType, Double>();
                HashMap<PlayerSetting, String> settings = new HashMap<PlayerSetting, String>();
                HashMap<String, Double> pointAllocations = new HashMap<String, Double>();

                UUID uuid = UUID.fromString(data.getString("uuid"));

                ResultSet experienceData = Database.getInstance().selectData("SELECT * FROM player_experience WHERE uuid = '" + uuid.toString() + "'");
                while (experienceData.next()) {
                    for (SkillType s : SkillType.values()) {
                        xp.put(s, experienceData.getDouble(s.toString().toLowerCase()));
                    }
                }

                ResultSet settingsData = Database.getInstance().selectData("SELECT * FROM player_settings WHERE uuid = '" + uuid.toString() + "'");
                while (settingsData.next()) {
                    for (PlayerSetting setting : PlayerSetting.values()) {
                        settings.put(setting, settingsData.getString(setting.toString().toLowerCase()));
                    }
                }

                ResultSet pointsData = Database.getInstance().selectData("SELECT * FROM player_allocations WHERE uuid = '" + uuid.toString() + "'");
                while (pointsData.next()) {
                    for (Ability ability : AbilityManager.getAbilities()) {
                        for (AbilityAttribute attribute : ability.getAttributes()) {
                            String loc = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
                            String qName = ability.getName().toLowerCase() + " " + attribute.name().toLowerCase();
                            pointAllocations.put(loc, pointsData.getDouble(qName));
                        }
                    }
                }

//                AbilityManager.getAbilities().forEach(ability -> ability.getAttributes().forEach(attribute -> pointAllocations.put(ability.toString() + ":" + attribute.toString(), 0.0)));
//                for (Ability ability : AbilityManager.getAbilities()) {
//                    for (AbilityAttribute attribute : ability.getAttributes()) {
//                        String select = "select * from player_point_allocations where uuid='" + uuid + "' AND ability='" + ability.getName().toLowerCase() + "' AND attribute='" + attribute.name().toLowerCase() + "' order by points desc limit 1";
//                        ResultSet pointsData = Database.getInstance().selectData(select);
//                        while (pointsData.next()) {
//                            String at = pointsData.getString("attribute");
//                            String ab = pointsData.getString("ability");
//                            Double points = pointsData.getDouble("points");
//                            pointAllocations.put(ab + ":" + at, points);
//                            InformationHandler.printMessage(InformationType.DEBUG, "put " + ab + " " + at + " " + points);
//                        }
//                    }
//                }

                RPGPlayerManager.getInstance().addPlayer(new RPGPlayer(uuid, xp, settings, pointAllocations));
                c++;
            }

            InformationHandler.printMessage(InformationType.INFO, "Registered " + c + " RPG Players in " + Duration.between(start, Instant.now()).toMillis() + "ms");
        } catch (SQLException e) {
            InformationHandler.printMessage(InformationType.ERROR, "There was an error with the database");
            e.printStackTrace();
        } catch (NullPointerException e) {
            InformationHandler.printMessage(InformationType.INFO, "There are no RPG Players");
        }
    }
}
