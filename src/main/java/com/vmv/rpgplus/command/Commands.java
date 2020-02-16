package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.vmv.core.config.FileManager;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.player.RPGPlayerSettings;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@CommandAlias("rpg")
public class Commands extends BaseCommand {

    @Default
    public void onDefault(Player p) {

        p.sendMessage("yeet");
        //RPGPlayerSettings.openMenu(RPGPlayerManager.getInstance().getPlayer(p));


    }

    @Subcommand("menu")
    @CommandPermission("rpgplus.player")
    @CommandCompletion("@players")
    public void displayMenu(Player p, @Optional OnlinePlayer target) {
        if (p.hasPermission("rpgplus.admin") && target != null) {
            RPGPlayerSettings.openMenu(p, RPGPlayerManager.getInstance().getPlayer(target.player));
        } else {
            RPGPlayerSettings.openMenu(p, RPGPlayerManager.getInstance().getPlayer(p));
        }
    }

    @Subcommand("stats")
    @CommandPermission("rpgplus.player")
    public void displayStats(Player p) {
        RPGPlayerManager.getInstance().getPlayer(p).toggleScoreboard();
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
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_level_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("set_level_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        }
    }

    @Subcommand("addlevel|addlev|addlvl")
    @CommandPermission("rpgplus.addlevel|rpgplus.admin")
    @CommandCompletion("@players @skills @range:1-100")
    public void addLevel(CommandSender p, OfflinePlayer player, String skill, int level) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        rp.setXP(SkillType.valueOf(skill.toUpperCase()), SkillManager.getInstance().getExperience(rp.getLevel(SkillType.valueOf(skill.toUpperCase())) + level));
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("add_level_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("add_level_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        }
    }

    @Subcommand("setexp|setexperience|setxp")
    @CommandPermission("rpgplus.setexperience|rpgplus.admin")
    @CommandCompletion("@players @skills")
    public void setExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayerManager.getInstance().getPlayer(player.getUniqueId()).setXP(SkillType.valueOf(skill.toUpperCase()), experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_experience_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("set_experience_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

    @Subcommand("addexp|addexperience|addxp")
    @CommandPermission("rpgplus.addexperience|rpgplus.admin")
    @CommandCompletion("@players @skills")
    public void addExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        rp.setXP(SkillType.valueOf(skill.toUpperCase()), rp.getExperience(SkillType.valueOf(skill.toUpperCase())) + experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("add_experience_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("add_experience_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

}
