package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import com.vmv.rpgplus.skill.SkillManager;
import org.bukkit.plugin.Plugin;

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

    private void registerCommands(BaseCommand... commands) {
        for (BaseCommand b : commands) {
            manager.registerCommand(b);
        }
    }

    public void registerCommandCompletions() {
        manager.getCommandCompletions().registerCompletion("skills", a -> {
            return SkillManager.getInstance().getSkills().stream().map(skill -> skill.getSkillType().toString().toLowerCase()).collect(Collectors.toList());
        });
    }

}
