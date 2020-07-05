package com.vmv.rpgplus.main;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.misc.BarTimerManager;
import com.vmv.rpgplus.command.CommandManager;
import com.vmv.rpgplus.database.DatabaseManager;
import com.vmv.rpgplus.dependency.DependencyManager;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.PlaceholderRequestEvent;
import com.vmv.rpgplus.inventory.InventoryUtils;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.mining.OreLocator;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Slime;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGPlus extends JavaPlugin {

    private static RPGPlus instance;

    @Override
    public void onEnable() {

        instance = this;
        saveDefaultConfig();
        new InformationHandler(this);
        new DependencyManager();
        new CommandManager(this);
        new FileManager(this);
        new BarTimerManager(this);
        new SkillManager();
        new AbilityManager();
        new RPGPlayerManager();
        new InventoryUtils(this);
        new DatabaseManager(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {new PlaceholderRequestEvent().register();}
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(ArmorStand.class).forEach(entity -> {
            if (entity.getName().length() <= 2) return;
            if (entity.getName().substring(entity.getName().length() - 2).equalsIgnoreCase("xp")) entity.remove();
        }));
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClass(Slime.class).forEach(entity -> { if (entity.isGlowing()) entity.remove(); }));

        Metrics metrics = new Metrics(this, 8091);

//        if (System.currentTimeMillis() > Long.valueOf("1593899909011")) {
//            InformationHandler.printMessage(InformationType.INFO, "Trial has finished, disabling the plugin");
//            getServer().getPluginManager().disablePlugin(this);
//        } else {
//            InformationHandler.printMessage(InformationType.INFO, "You are running a trial version of RPGPlus, this will last until 04/07/2020");
//        }

    }

    @Override
    public void onDisable() {
        try { OreLocator.unregisterColorTeams(); } catch (Exception ignore) {}

        DatabaseManager.getInstance().savePlayerData(false);
        InformationHandler.printMessage(InformationType.INFO, "Removing experience drops [" + ExperienceModifyEvent.getAnimationStands().size() + "]");
        ExperienceModifyEvent.getAnimationStands().forEach(armorStand -> armorStand.remove());
        InformationHandler.printMessage(InformationType.INFO, "Removing locator entities [" + OreLocator.magmas.size() + "]");
        OreLocator.killAllSlimes();
    }

    public static RPGPlus getInstance() {
        return instance;
    }

    public void registerEvents(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getServer().getPluginManager().registerEvents(listener, getInstance());
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return FileManager.getConfig();
    }
}
