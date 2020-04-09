package com.vmv.rpgplus.dependency;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.Bukkit;

public class DependencyManager {

    private static DependencyManager instance;

    public DependencyManager() {
        instance = this;
        if (!getRequired()) RPGPlus.getInstance().getServer().getPluginManager().disablePlugin(RPGPlus.getInstance());
        if (Bukkit.getPluginManager().getPlugin("AAC") != null) {
            RPGPlus.getInstance().registerEvents(new AAC());
            InformationHandler.printMessage(InformationType.INFO, "AAC found and initialized compatibility");
        }
    }

    private boolean getRequired() {
        return new Required().valid();
    }

    public static DependencyManager getInstance() {
        return instance;
    }
}
