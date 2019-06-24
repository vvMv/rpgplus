package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.vmv.core.config.FileManager;
import com.vmv.core.config.PluginFile;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

@CommandAlias("rpg")
public class Commands extends BaseCommand {

    @Default
    public void onDefault(Player p, int level) {
        RPGPlayerManager.getInstance().getPlayer(p.getUniqueId()).setXP(SkillType.STAMINA, SkillManager.getInstance().getExperience(level));
    }

    @Subcommand("stats")
    @CommandPermission("rpgplus.player")
    public void displayStats(Player p) {
        for (SkillType st : SkillType.values()) {
            p.sendMessage(st.toString() + " " + RPGPlayerManager.getInstance().getPlayer(p).getLevel(st));
        }
    }

    @Subcommand("debug")
    @CommandPermission("rpgplus.admin")
    public void debug(CommandSender sender) {
        List<String> mats = SkillManager.getInstance().getSkill(SkillType.ARCHERY).getConfig().getStringList("materials");
        mats.forEach(m -> sender.sendMessage(m));

    }

    @Subcommand("setlevel|setlev|setlvl")
    @CommandPermission("rpgplus.setlevel|rpgplus.admin")
    @CommandCompletion("@players @skills @range:1-100")
    public void setLevel(CommandSender p, OfflinePlayer player, String skill, int level) {
        RPGPlayerManager.getInstance().getPlayer(player.getUniqueId()).setXP(SkillType.valueOf(skill.toUpperCase()), SkillManager.getInstance().getExperience(level));
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("setLevelSender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("setLevelReceiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        }
    }

    @Subcommand("addlevel|addlev|addlvl")
    @CommandPermission("rpgplus.addlevel|rpgplus.admin")
    @CommandCompletion("@players @skills @range:1-100")
    public void addLevel(CommandSender p, OfflinePlayer player, String skill, int level) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        rp.setXP(SkillType.valueOf(skill.toUpperCase()), SkillManager.getInstance().getExperience(rp.getLevel(SkillType.valueOf(skill.toUpperCase())) + level));
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("addLevelSender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("addLevelReceiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        }
    }

    @Subcommand("setexp|setexperience|setxp")
    @CommandPermission("rpgplus.setexperience|rpgplus.admin")
    @CommandCompletion("@players @skills")
    public void setExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayerManager.getInstance().getPlayer(player.getUniqueId()).setXP(SkillType.valueOf(skill.toUpperCase()), experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("setExperienceSender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("setExperienceReceiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

    @Subcommand("addexp|addexperience|addxp")
    @CommandPermission("rpgplus.addexperience|rpgplus.admin")
    @CommandCompletion("@players @skills")
    public void addExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        rp.setXP(SkillType.valueOf(skill.toUpperCase()), rp.getExperience(SkillType.valueOf(skill.toUpperCase())) + experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("addExperienceSender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("addExperienceReceiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

}
