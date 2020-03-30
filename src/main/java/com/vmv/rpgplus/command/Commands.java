package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.vmv.core.config.FileManager;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.inventory.InventoryUtils;
import com.vmv.rpgplus.inventory.RPGMenu;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("rpg")
public class Commands extends BaseCommand {

    @Default
    public void onDefault(Player p) {

        displayMenu(p);


    }

    @Subcommand("menu|settings")
    @CommandPermission("rpgplus.menu")
    @CommandCompletion("@players")
    public void displayMenu(Player p /*@Optional OnlinePlayer target*/) {
        String settingsTitle = FileManager.getLang().getString("inventory.title_menu").replaceAll("%p", p.getName());
        InventoryUtils.getInventory(settingsTitle, 3, new RPGMenu(), "").open(p);
//        if (p.hasPermission("rpgplus.admin") && target != null) {
//            RPGPlayerSettingsMenu.getInstance().openMenu(p, RPGPlayerManager.getInstance().getPlayer(target.player));
//        } else {
//            RPGPlayerSettingsMenu.getInstance().openMenu(p, RPGPlayerManager.getInstance().getPlayer(p));
//        }
    }

    @Subcommand("stats|stat|level|levels")
    @CommandPermission("rpgplus.stats")
    @CommandCompletion("@players")
    public void displayStats(Player p, @Optional OnlinePlayer target) {
        RPGPlayer pl = target == null ? RPGPlayerManager.getInstance().getPlayer(p) : RPGPlayerManager.getInstance().getPlayer(target.getPlayer());

        for (String stats_display : FileManager.getLang().getStringList("stats_display")) {
            if (stats_display.contains("%s")) {
                for (Skill skill : SkillManager.getInstance().getSkills()) {
                    ChatUtil.sendCenteredChatMessage(p, stats_display.replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%l", String.valueOf((int) pl.getLevel(skill.getSkillType()))));
                }
                continue;
            }
            ChatUtil.sendCenteredChatMessage(p, stats_display.replace("%p", Bukkit.getPlayer(pl.getUuid()).getName()).replace("%t", pl.getTotalLevel() + ""));
        }
    }

    @Subcommand("debug")
    @CommandPermission("rpgplus.debug")
    public void debug(Player player) {
        //RPGPlayerManager.getInstance().getPlayer(player).addPointAllocation(AbilityManager.getAbility("dash"), AbilityAttribute.INCREASE_SPEED);
        //InformationHandler.printMessage(InformationType.DEBUG, "dash speed points: " + RPGPlayerManager.getInstance().getPlayer(player).getPointAllocation(AbilityManager.getAbility("dash"), AbilityAttribute.INCREASE_SPEED));
    }

    @Subcommand("setlevel|setlev|setlvl")
    @CommandPermission("rpgplus.setlevel")
    @CommandCompletion("@players @skills @range:1-100")
    public void setLevel(CommandSender p, OfflinePlayer player, String skill, int level) {
        RPGPlayerManager.getInstance().getPlayer(player.getUniqueId()).setXP(SkillType.valueOf(skill.toUpperCase()), SkillManager.getInstance().getExperience(level));
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_level_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("set_level_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%l", String.valueOf(level)));
        }
    }

    @Subcommand("addlevel|addlev|addlvl")
    @CommandPermission("rpgplus.addlevel")
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
    @CommandPermission("rpgplus.setexperience")
    @CommandCompletion("@players @skills")
    public void setExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayerManager.getInstance().getPlayer(player.getUniqueId()).setXP(SkillType.valueOf(skill.toUpperCase()), experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_experience_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("set_experience_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

    @Subcommand("addexp|addexperience|addxp")
    @CommandPermission("rpgplus.addexperience")
    @CommandCompletion("@players @skills")
    public void addExperience(CommandSender p, OfflinePlayer player, String skill, int experience) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        rp.setXP(SkillType.valueOf(skill.toUpperCase()), rp.getExperience(SkillType.valueOf(skill.toUpperCase())) + experience);
        ChatUtil.sendChatMessage(p, FileManager.getLang().getString("add_experience_sender").replaceAll("%p", player.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        if (Bukkit.getPlayer(player.getName()) != null) {
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("add_experience_receiver").replaceAll("%p", p.getName()).replaceAll("%s", skill).replaceAll("%e", String.valueOf(experience)));
        }
    }

    @Subcommand("setattribute|setatt")
    @CommandPermission("rpgplus.setattribute")
    @CommandCompletion("@players @abilities @attributes @range:1-10 @boolean")
    public void setAttribute(CommandSender p, OfflinePlayer player, String ability, String attribute, int points, @Optional String forceUnsafe) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        Ability ab = AbilityManager.getInstance().getAbility(ability);
        AbilityAttribute at = AbilityAttribute.valueOf(attribute.toUpperCase());
        if (forceUnsafe != null && !forceUnsafe.equalsIgnoreCase("true") && !forceUnsafe.equalsIgnoreCase("false")) return;
        if (!ab.getAttributes().contains(at)) {
            ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_attribute_invalid").replace("%ab", ability).replace("%at", attribute));
            return;
        }

        if (rp.attemptSetPointAllocation(ab, at, points, Boolean.valueOf(forceUnsafe))) {
            ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_attribute_sender").replace("%ab", ability).replace("%at", attribute).replace("%n", points + "").replace("%p", player.getName()));
            ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("set_attribute_receiver").replace("%ab", ability).replace("%at", attribute).replace("%n", points + "").replace("%p", p.getName()));
        } else {
            ChatUtil.sendChatMessage(p, FileManager.getLang().getString("set_attribute_failed").replace("%p", player.getName()));
        }
    }

    @Subcommand("resetattributes|resetattribute|resetatt")
    @CommandPermission("rpgplus.resetattribute")
    @CommandCompletion("@players")
    public void resetAttribute(CommandSender p, OfflinePlayer player) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player.getUniqueId());
        ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("reset_attribute_receiver").replace("%p", p.getName()));
        ChatUtil.sendChatMessage(Bukkit.getPlayer(player.getName()), FileManager.getLang().getString("reset_attribute_sender").replace("%p", Bukkit.getPlayer(player.getName()).getName()));

        for (Ability ability : AbilityManager.getInstance().getAbilities()) {
            for (AbilityAttribute attribute : ability.getAttributes()) {
                rp.attemptSetPointAllocation(ability, attribute, 0, true);
            }
        }
    }

    @Subcommand("reload|r|rl")
    @CommandPermission("rpgplus.reload")
    public void reloadConfigurations(CommandSender p) {
        ChatUtil.sendChatMessage(p, "&aReloading config...");
        ChatUtil.sendChatMessage(p, "&aReloading skills...");
        ChatUtil.sendChatMessage(p, "&aReloading lang...");
        FileManager.getLang().reload();
        RPGPlus.getInstance().reloadConfig();
        for (Skill skill : SkillManager.getInstance().getSkills()) {
            skill.reload();
        }
        ChatUtil.sendChatMessage(p, "&aAll configurations reloaded successfully.");
        ChatUtil.sendChatMessage(p, "&7Some configs may require a server restart!");
    }

}
