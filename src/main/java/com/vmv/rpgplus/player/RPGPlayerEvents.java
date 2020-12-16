package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.database.DatabaseUtils;
import com.vmv.rpgplus.database.PlayerSetting;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class RPGPlayerEvents implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkJoin(PlayerJoinEvent e) {

        Bukkit.getScheduler().runTaskAsynchronously(RPGPlus.getInstance(), new Runnable() {
            @Override
            public void run() {
                DatabaseUtils.loadPlayer(e.getPlayer());

                RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(e.getPlayer());
                rp.sendAbilityPointReminder();
            }
        });

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkLeave(PlayerQuitEvent e) {
        DatabaseUtils.unloadPlayer(e.getPlayer());
    }

    @EventHandler
    public void onExperienceGain(ExperienceModifyEvent e) {
        if (e.getPlayer() == null) return;
        Skill s = SkillManager.getInstance().getSkill(e.getSkill());

        String decimal = String.valueOf(MathUtils.round(e.getRPGPlayer().getLevel(e.getSkill()), 2)).split("\\.")[1];
        if (decimal.length() == 1) decimal += "0";
        int percent = Integer.parseInt(decimal);

        if (RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).getSettingBoolean(PlayerSetting.EXPERIENCE_ACTIONBAR)) {
            String barString = ChatUtil.getProgressBar(percent, 100, FileManager.getLang().getInt("progress_bar.total_bars"), FileManager.getLang().getString("progress_bar.symbol").charAt(0), ChatColor.valueOf(FileManager.getLang().getString("progress_bar.progress_color").toUpperCase()), ChatColor.valueOf(FileManager.getLang().getString("progress_bar.remaining_color").toUpperCase()));
            ChatUtil.sendChatMessage(e.getPlayer(), FileManager.getLang().getString("progress_bar.style").replace("%s", s.getFormattedName()).replace("%e", String.valueOf(e.getExp())).replace("%b", barString));
            //ChatUtil.sendActionMessage(e.getPlayer(), FileManager.getLang().getString("skill." + e.getSkill().toString().toLowerCase()) + " +" + (int) e.getExp() + "." + decimal + " &2[" + ChatUtil.getProgressBar(percent, 100, 40, '|', ChatColor.GREEN, ChatColor.GRAY) + "&2]");
        }

        if (RPGPlus.getInstance().getConfig().getBoolean("general.experience_holograms") && RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS)) {
            ChatUtil.sendChatMessage(e.getPlayer(), FileManager.getLang().getString("hologram.experience").replace("%s", s.getFormattedName()).replace("%c", s.getSkillColor().toString()).replace("%e", String.valueOf(e.getExp())));
            //ChatUtil.sendFloatingMessage(e.getPlayer(), SkillManager.getInstance().getSkill(e.getSkill()).getSkillColor() + "+" + e.getExp() + "xp", 1);
        }

    }

    @EventHandler
    public void onLevelUp(LevelModifyEvent e) {

        if (e.getPlayer() == null) return;
        Player p = e.getPlayer();
        if (RPGPlayerManager.getInstance().getPlayer(p).getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES)) {
            List<String> msg = FileManager.getLang().getStringList("level_up");

            for (String s : msg) {
                ChatUtil.sendChatMessage(p, s
                        .replace("%s", StringUtils.capitalize(SkillManager.getInstance().getSkill(e.getSkill()).getFormattedName()))
                        .replace("%f", String.valueOf(e.getFromLevel()))
                        .replace("%a", String.valueOf(MathUtils.round(SkillManager.getInstance().getExperience(e.getToLevel() + 1) - e.getToExperience(), 1)))
                        .replace("%n", String.valueOf(e.getLevel() + 1))
                        .replace("%t", String.valueOf(e.getLevel())));
            }
        }
    }
}
