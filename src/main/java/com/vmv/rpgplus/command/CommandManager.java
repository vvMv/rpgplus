package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.Plugin;

public class CommandManager {

    //Documentation for Aikar's command framework https://github.com/aikar/commands/wiki/Using-ACF
    private BukkitCommandManager manager;

    public CommandManager(Plugin plugin){
        manager = new BukkitCommandManager(plugin);
        manager.createRootCommand("rpg");
        registerCommands(new Commands());
    }

    private void registerCommands(BaseCommand... commands) {
        for (BaseCommand b : commands) {
            manager.registerCommand(b);
        }
    }
}
