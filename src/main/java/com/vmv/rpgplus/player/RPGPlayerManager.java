package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.database.Database;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RPGPlayerManager implements Listener {

    private static RPGPlayerManager instance;
    private ArrayList<RPGPlayer> players;

    public List<String> dataToSave;

    public RPGPlayerManager() {
        RPGPlus.getInstance().registerEvents(this);
        this.instance = this;
        players = new ArrayList<RPGPlayer>();
        dataToSave = new ArrayList<String>();
        registerPlayers();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RPGPlus.getInstance(), () -> savePlayerData(true), RPGPlus.getInstance().getConfig().getLong("General.saveData"),  RPGPlus.getInstance().getConfig().getLong("General.saveData"));
    }

    public ArrayList<RPGPlayer> getPlayers() {
        return players;
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public RPGPlayer getPlayer(UUID uuid){
        for (RPGPlayer p : players) {
            if (p.getUuid().equals(uuid)){
                return p;
            }
        }
        return null;
    }

    public RPGPlayer getPlayer(Player p) {
        return getPlayer(p.getUniqueId());
    }

    public void addPlayer(RPGPlayer p) {
        players.add(p);
    }

    @EventHandler
    public void checkJoin(PlayerJoinEvent e) {
        if (getPlayer(e.getPlayer().getUniqueId()) == null) {
            InformationHandler.printMessage(InformationType.INFO, "Creating database record for " + e.getPlayer().getName());
            HashMap<SkillType, Double> xp = new HashMap<SkillType, Double>();
            for (SkillType s : SkillType.values()) {
                xp.put(s, 0.0);
            }
            addPlayer(new RPGPlayer(e.getPlayer().getUniqueId(), xp));
        }

        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_experience(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);
    }

    @EventHandler
    public void onExperienceGain(ExperienceModifyEvent e) {
        int progress = Integer.parseInt(String.valueOf(MathUtils.round(e.getRPGPlayer().getLevel(e.getSkill()), 2)).split("\\.")[1]);
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.DARK_GREEN + "[");
        for (int i = 0; i < 20; i++) {
            if (progress >= i * 5) {
                message.append(ChatColor.GREEN + "|");
            } else {
                message.append(ChatColor.GRAY + "|");
            }
        }
        message.append(ChatColor.DARK_GREEN + "]");
        ChatUtil.sendActionMessage(e.getPlayer(), WordUtils.capitalizeFully(e.getSkill().toString()) + " +" + MathUtils.round(e.getExp(), 2) + " " + message.toString());


    }

    @EventHandler
    public void onLevelUp(LevelModifyEvent e) {

        List<String> msg = FileManager.getLang().getStringList("levelUp");
        Player p = e.getPlayer();

        for (String s : msg) {
            ChatUtil.sendCenteredChatMessage(p, s
                    .replaceAll("%s", StringUtils.capitalize(e.getSkill().toString().toLowerCase()))
                    .replaceAll("%f", String.valueOf(e.getFromLevel()))
                    .replaceAll("%a", String.valueOf(MathUtils.round((SkillManager.getInstance().getExperience(e.getLevel() + 1)  - RPGPlayerManager.getInstance().getPlayer(p).getExperience(e.getSkill())), 1)))
                    .replaceAll("%n", String.valueOf(e.getLevel() + 1))
                    .replaceAll("%t", String.valueOf(e.getLevel())));
        }

        try {
            p.playSound(p.getLocation(), Sound.valueOf(RPGPlus.getInstance().getConfig().getString("Sounds.LevelUp")), 1f, 1f);
        } catch (Exception e2) {
            InformationHandler.printMessage(InformationType.ERROR, "Config value for Sounds.LevelUp '" + RPGPlus.getInstance().getConfig().getString("Sounds.LevelUp") + "' is invalid");
        }
    }

    private void registerPlayers() {
        ResultSet data = Database.getInstance().selectData("SELECT * FROM player_experience");

        try {
            int c = 0;
            while (data.next()) {
                HashMap<SkillType, Double> xp = new HashMap<SkillType, Double>();
                UUID uuid = UUID.fromString(data.getString("uuid"));
                for (SkillType s : SkillType.values()) {
                    xp.put(s, data.getDouble(s.toString().toLowerCase()));
                }
                RPGPlayerManager.getInstance().addPlayer(new RPGPlayer(uuid, xp));
                c++;
            }
            InformationHandler.printMessage(InformationType.INFO, "Registered " + c + " RPG Players");
        } catch (SQLException e) {
            InformationHandler.printMessage(InformationType.ERROR, "The connection to the database has been closed");
        } catch (NullPointerException e) {
            InformationHandler.printMessage(InformationType.INFO, "There are no RPG Players");
        }
    }

    public void savePlayerData(boolean asTask) {
        InformationHandler.printMessage(InformationType.INFO, "Saving player data... [" + dataToSave.size() + "]");
        Instant start = Instant.now();

        dataToSave.forEach(data -> {
            String uuid = data.split(":")[0];
            String skill = data.split(":")[1];
            Database.getInstance().updateData("player_experience", skill, getPlayer(UUID.fromString(uuid)).getExperience(SkillType.valueOf(skill.toUpperCase())), "uuid", "=", uuid, asTask);
        });

        Instant finish = Instant.now();
        InformationHandler.printMessage(InformationType.INFO, "Finished! Took " + Duration.between(start, finish).toMillis() + "ms.");
        dataToSave.clear();
    }

    public List<String> getDataToSave() {
        return dataToSave;
    }
}
