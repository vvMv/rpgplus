package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.database.Database;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.database.PlayerSetting;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RPGPlayerManager implements Listener {

    private static RPGPlayerManager instance;
    private ArrayList<RPGPlayer> players;

    public RPGPlayerManager() {
        instance = this;
        players = new ArrayList<RPGPlayer>();
        new RPGPlayerSettingsMenu();
        RPGPlus.getInstance().registerEvents(this);
    }

    public ArrayList<RPGPlayer> getPlayers() {
        return players;
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public RPGPlayer getPlayer(UUID uuid) {
        for (RPGPlayer p : players) {
            if (p.getUuid().equals(uuid)) {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkJoin(PlayerJoinEvent e) {
        if (getPlayer(e.getPlayer().getUniqueId()) == null) {
            InformationHandler.printMessage(InformationType.INFO, "Creating database record for " + e.getPlayer().getName());
            HashMap<SkillType, Double> xp = new HashMap<SkillType, Double>();
            HashMap<PlayerSetting, String> settings = new HashMap<PlayerSetting, String>();
            HashMap<String, Double> pointAllocations = new HashMap<String, Double>();
            for (SkillType s : SkillType.values()) {
                xp.put(s, 0.0);
            }
            for (PlayerSetting setting : PlayerSetting.values()) {
                settings.put(setting, setting.getDefaultValue());
            }
            for (Ability ability : AbilityManager.getAbilities()) {
                for (AbilityAttribute attribute : ability.getAttributes()) {
                    String loc = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
                    pointAllocations.put(loc, 0.0);
                }
            }
            AbilityManager.getAbilities().forEach(ability -> ability.getAttributes().forEach(attribute -> pointAllocations.put(ability.toString() + ":" + attribute.toString(), 0.0)));
            addPlayer(new RPGPlayer(e.getPlayer().getUniqueId(), xp, settings, pointAllocations));
        }

        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_experience(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);
        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_settings(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);
        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_allocations(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), () -> { rp.sendAbilityPointReminder();}, 100);
    }

    @EventHandler
    public void onitempickup(EntityPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onExperienceGain(ExperienceModifyEvent e) {
        if (e.getPlayer() == null) return;
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
        ChatUtil.sendActionMessage(e.getPlayer(), WordUtils.capitalizeFully(e.getSkill().toString()) + " +" + MathUtils.round(e.getExp(), 2) + " " + message.toString(), RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.experience_gain"));


    }

    @EventHandler
    public void onLevelUp(LevelModifyEvent e) {

        if (e.getPlayer() == null) return;
        List<String> msg = FileManager.getLang().getStringList("level_up");
        Player p = e.getPlayer();

        for (String s : msg) {
            ChatUtil.sendCenteredChatMessage(p, s
                    .replaceAll("%s", StringUtils.capitalize(e.getSkill().toString().toLowerCase()))
                    .replaceAll("%f", String.valueOf(e.getFromLevel()))
                    .replaceAll("%a", String.valueOf(MathUtils.round((SkillManager.getInstance().getExperience(e.getLevel() + 1) - RPGPlayerManager.getInstance().getPlayer(p).getExperience(e.getSkill())), 1)))
                    .replaceAll("%n", String.valueOf(e.getLevel() + 1))
                    .replaceAll("%t", String.valueOf(e.getLevel())));
        }

        try {
            p.playSound(p.getLocation(), Sound.valueOf(RPGPlus.getInstance().getConfig().getString("sounds.level_up")), 1f, 1f);
        } catch (Exception e2) {
            InformationHandler.printMessage(InformationType.ERROR, "Config value for sounds.level_up '" + RPGPlus.getInstance().getConfig().getString("sounds.level_up") + "' is invalid");
        }
    }
}
