package com.vmv.rpgplus.main;

import com.vmv.core.config.FileManager;
import com.vmv.core.database.Database;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.misc.BarTimerManager;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.command.CommandManager;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.SkillManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGPlus extends JavaPlugin {

    private static RPGPlus instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        new InformationHandler(this);
        new CommandManager(this);
        new Database(this, "rpg.db", getDataFolder());
        new FileManager(this);
        new BarTimerManager(this);
        new SkillManager();
        new AbilityManager();
        new RPGPlayerManager();
    }

    public static RPGPlus getInstance() {
        return instance;
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, getInstance());
        }
    }
}
