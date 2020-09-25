package com.vmv.rpgplus.dependency;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.database.RPGVersion;
import com.vmv.rpgplus.event.PlaceholderRequestEvent;
import com.vmv.rpgplus.main.RPGPlus;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;

public class DependencyManager {

    private static DependencyManager instance;

    public DependencyManager() {
        instance = this;
        if (Bukkit.getPluginManager().getPlugin("AAC") != null) {
            RPGPlus.getInstance().registerEvents(new AAC());
            InformationHandler.printMessage(InformationType.INFO, "AAC found and initialized compatibility");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderRequestEvent().register();
            InformationHandler.printMessage(InformationType.INFO, "PlaceholderAPI found and initialized compatibility");

        }
        Metrics metrics = new Metrics(RPGPlus.getInstance(), 8091);

        try {
            new RPGVersion(RPGPlus.getInstance());
            RPGVersion.getInstance().checkVersion(RPGPlus.getInstance().getDescription().getVersion());
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.WARN, "Unable to check version");
        }
    }

    public static DependencyManager getInstance() {
        return instance;
    }
}
