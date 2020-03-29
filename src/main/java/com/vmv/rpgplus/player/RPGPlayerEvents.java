package com.vmv.rpgplus.player;

import com.cryptomorin.xseries.XSound;
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
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.List;

public class RPGPlayerEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkJoin(PlayerJoinEvent e) {
        if (RPGPlayerManager.getInstance().getPlayer(e.getPlayer().getUniqueId()) == null) {
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
            for (Ability ability : AbilityManager.getInstance().getAbilities()) {
                for (AbilityAttribute attribute : ability.getAttributes()) {
                    String loc = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
                    pointAllocations.put(loc, 0.0);
                }
            }
            AbilityManager.getInstance().getAbilities().forEach(ability -> ability.getAttributes().forEach(attribute -> pointAllocations.put(ability.toString() + ":" + attribute.toString(), 0.0)));
            RPGPlayerManager.getInstance().addPlayer(new RPGPlayer(e.getPlayer().getUniqueId(), xp, settings, pointAllocations));
        }

        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_experience(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);
        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_settings(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);
        Database.getInstance().executeSQL("INSERT OR IGNORE INTO player_allocations(uuid) VALUES('" + e.getPlayer().getUniqueId() + "')", true);

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(e.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), () -> { rp.sendAbilityPointReminder();}, 100);
    }

    @EventHandler
    public void onExperienceGain(ExperienceModifyEvent e) {
        if (e.getPlayer() == null) return;

        String decimal = String.valueOf(MathUtils.round(e.getRPGPlayer().getLevel(e.getSkill()), 2)).split("\\.")[1];
        if (decimal.length() == 1) decimal += "0";
        int percent = Integer.parseInt(decimal);

        ChatUtil.sendActionMessage(e.getPlayer(), "&f" + WordUtils.capitalizeFully(e.getSkill().toString()) + " +" + (int) e.getExp() + "." + decimal + " &2[" + ChatUtil.getProgressBar(percent, 100, 40, '|', ChatColor.GREEN, ChatColor.GRAY) + "&2]");
        if (!RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS)) return;
        ChatUtil.sendFloatingMessage(e.getPlayer(), SkillManager.getInstance().getSkill(e.getSkill()).getSkillColor() + "+" + e.getExp() + "xp", 1);
    }

    @EventHandler
    public void onLevelUp(LevelModifyEvent e) {

        if (e.getPlayer() == null) return;
        Player p = e.getPlayer();
        if (RPGPlayerManager.getInstance().getPlayer(p).getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES)) {
            List<String> msg = FileManager.getLang().getStringList("level_up");

            for (String s : msg) {
                ChatUtil.sendCenteredChatMessage(p, s
                        .replace("%s", StringUtils.capitalize(e.getSkill().toString().toLowerCase()))
                        .replace("%f", String.valueOf(e.getFromLevel()))
                        .replace("%a", String.valueOf(MathUtils.round((SkillManager.getInstance().getExperience(e.getLevel() + 1) - RPGPlayerManager.getInstance().getPlayer(p).getExperience(e.getSkill())), 1)))
                        .replace("%n", String.valueOf(e.getLevel() + 1))
                        .replace("%t", String.valueOf(e.getLevel())));
            }
        }

        try {
            p.playSound(p.getLocation(), XSound.matchXSound(Sound.valueOf(RPGPlus.getInstance().getConfig().getString("sounds.level_up"))).parseSound(), 1f, 1f);
        } catch (Exception e2) {
            InformationHandler.printMessage(InformationType.ERROR, "Config value for sounds.level_up '" + RPGPlus.getInstance().getConfig().getString("sounds.level_up") + "' is invalid");
        }
    }

}
