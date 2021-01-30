package com.vmv.rpgplus.dependency;

import com.google.common.base.Preconditions;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.database.RPGVersion;
import com.vmv.rpgplus.event.PlaceholderRequestEvent;
import com.vmv.rpgplus.main.RPGPlus;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class DependencyManager {

    private static DependencyManager instance;
    private final List<AntiCheatHook> anticheatHooks = new ArrayList<>();

    public DependencyManager() {
        instance = this;
        registerAntiCheatHookIfEnabled("AAC5", AACHook::new);

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

    public List<AntiCheatHook> getAnticheatHooks() {
        return Collections.unmodifiableList(anticheatHooks);
    }

    private void registerAntiCheatHookIfEnabled(String pluginName, Supplier<? extends AntiCheatHook> hookSupplier) {
        if (!RPGPlus.getInstance().getServer().getPluginManager().isPluginEnabled(pluginName)) {
            return;
        }

        AntiCheatHook hook = hookSupplier.get();
        if (!registerAntiCheatHook(hook)) {
            InformationHandler.printMessage(InformationType.INFO, "Tried to register hook for plugin " + pluginName + " but one was already registered.");
            return;
        }

        if (hook instanceof Listener) {
            RPGPlus.getInstance().getServer().getPluginManager().registerEvents((Listener) hook, RPGPlus.getInstance());
        }

        InformationHandler.printMessage(InformationType.INFO, "Anti cheat detected. Enabling anti cheat support for \"" + hook.getPluginName() + "\"");
    }

    public boolean registerAntiCheatHook(@NotNull AntiCheatHook hook) {
        Preconditions.checkNotNull(hook, "Cannot register a null anticheat hook implementation");

        if (!hook.isSupported()) {
            return false;
        }

        for (AntiCheatHook anticheatHook : anticheatHooks) {
            if (anticheatHook.getPluginName().equals(hook.getPluginName())) {
                return false;
            }
        }

        return anticheatHooks.add(hook);
    }

    public static DependencyManager getInstance() {
        return instance;
    }
}
