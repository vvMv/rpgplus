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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RPGPlayerManager implements Listener {

    private static RPGPlayerManager instance;
    private ArrayList<RPGPlayer> players;

    public RPGPlayerManager() {
        RPGPlus.getInstance().registerEvents(this);
        this.instance = this;
        players = new ArrayList<RPGPlayer>();
        registerPlayers();
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
        Bukkit.broadcastMessage("checking join");
        if (getPlayer(e.getPlayer().getUniqueId()) == null) {
            RPGPlus.getInstance().getLogger().info("Creating database record for " + e.getPlayer().getName());
            addPlayer(new RPGPlayer(e.getPlayer().getUniqueId(), null));
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
            //RPGPlus.getInstance().getLogger().info("Config value for Sound.LevelUp '" + RPGPlus.getInstance().getConfig().getString("Sounds.LevelUp") + "' is invalid");
            InformationHandler.printMessage(InformationType.ERROR, "Config value for Sound.LevelUp '" + RPGPlus.getInstance().getConfig().getString("Sounds.LevelUp") + "' is invalid");
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
            RPGPlus.getInstance().getLogger().info("Registered " + c + " RPG Players");
        } catch (SQLException e) {
            RPGPlus.getInstance().getLogger().info("The connection to the database has been closed");
        } catch (NullPointerException e) {
            RPGPlus.getInstance().getLogger().info("There are no RPG Players");
        }
    }

}
