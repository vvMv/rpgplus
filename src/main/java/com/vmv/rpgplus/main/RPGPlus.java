package com.vmv.rpgplus.main;

import com.vmv.core.config.FileManager;
import com.vmv.core.database.Database;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.gui.PrivateInventory;
import com.vmv.core.minecraft.misc.BarTimerManager;
import com.vmv.rpgplus.database.DatabaseManager;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.command.CommandManager;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.mining.OreLocator;
import org.bukkit.Bukkit;
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
        new DependencyManager(this);
        new CommandManager(this);
        new DatabaseManager(this);
        new FileManager(this);
        new BarTimerManager(this);
        new SkillManager();
        new AbilityManager();
        new RPGPlayerManager();
        registerEvents(PrivateInventory.getListener());
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClasses(ArmorStand.class).forEach(entity -> { if (entity.getName().substring(entity.getName().length() - 2).equalsIgnoreCase("xp")) entity.remove(); }));
        Bukkit.getWorlds().forEach(world -> world.getEntitiesByClasses(Slime.class).forEach(entity -> { if (entity.isGlowing()) entity.remove(); }));
    }

    @Override
    public void onDisable() {
        RPGPlayerManager.getInstance().savePlayerData(false);
        InformationHandler.printMessage(InformationType.INFO, "Removing experience drops [" + ExperienceModifyEvent.getAnimationStands().size() + "]");
        ExperienceModifyEvent.getAnimationStands().forEach(armorStand -> armorStand.remove());
        InformationHandler.printMessage(InformationType.INFO, "Removing locator entities [" + OreLocator.slimes.size() + "]");
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
}
