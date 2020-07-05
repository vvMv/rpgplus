package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.ImmutableList;
import com.vmv.rpgplus.database.PlayerSetting;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandManager {

    //Documentation for Aikar's command framework https://github.com/aikar/commands/wiki/Using-ACF
    private BukkitCommandManager manager;

    public CommandManager(Plugin plugin){
        manager = new BukkitCommandManager(plugin);
        manager.createRootCommand("rpg");
        registerCommands(new Commands());
        registerCommandCompletions();
    }

    public void registerCommands(BaseCommand... commands) {
        for (BaseCommand b : commands) {
            manager.registerCommand(b);
        }
    }

    public void registerCommandCompletions() {
        manager.getCommandCompletions().registerCompletion("skills", a -> {
            return SkillManager.getInstance().getSkills().stream().map(skill -> skill.getSkillType().toString().toLowerCase()).collect(Collectors.toList());
        });
        manager.getCommandCompletions().registerCompletion("abilities", a -> {
            return AbilityManager.getInstance().getAbilities().stream().map(ability -> ability.getName().toLowerCase()).collect(Collectors.toList());
        });
        manager.getCommandCompletions().registerCompletion("attributes", a -> {
            return Arrays.stream(AbilityAttribute.values()).map(attribute -> attribute.toString().toLowerCase()).collect(Collectors.toList());
        });
        manager.getCommandCompletions().registerCompletion("boolean", a -> {
            return ImmutableList.of("true", "false");
        });
        manager.getCommandCompletions().registerCompletion("settings", a -> {
            return Arrays.stream(PlayerSetting.values()).map(setting -> setting.toString()).collect(Collectors.toList());
        });
        manager.getCommandCompletions().registerCompletion("rpgplayers", a -> {
            List<String> playernames = new ArrayList<>();
            for (RPGPlayer player : RPGPlayerManager.getInstance().getPlayers()) {
                if (Bukkit.getOfflinePlayer(player.getUuid()) == null) continue;
                playernames.add(Bukkit.getOfflinePlayer(player.getUuid()).getName());
            }
            return playernames;
        });
    }

}
