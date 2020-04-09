package com.vmv.rpgplus.dependency;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class DependencyManager {

    private static DependencyManager instance;

    private WorldGuardPlugin worldGuard;
    private GriefPrevention griefPrevention;

    public DependencyManager() {
        instance = this;
        worldGuard = setupWorldGuard();
        if (!getRequired()) RPGPlus.getInstance().getServer().getPluginManager().disablePlugin(RPGPlus.getInstance());
        if (Bukkit.getPluginManager().getPlugin("AAC") != null) {
            RPGPlus.getInstance().registerEvents(new AAC());
            InformationHandler.printMessage(InformationType.INFO, "AAC found and initialized compatibility");
        }
    }

    private WorldGuardPlugin setupWorldGuard() {
        Plugin wg = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
        if (wg == null || !(wg instanceof WorldGuardPlugin)) {
            return null;
        }
        return (WorldGuardPlugin) wg;
    }

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    public GriefPrevention getGriefPrevention() {
        return griefPrevention;
    }

    public boolean testWorldGuardFlag(Location l, Player p, StateFlag flag) {

        if (DependencyManager.getInstance().getWorldGuard() != null) {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            //ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(shooter.getLocation()));

            return query.testState(BukkitAdapter.adapt(l), WorldGuardPlugin.inst().wrapPlayer(p), flag);

        }
        return true;
    }

    private boolean getRequired() {
        return new Required().valid();
    }

    public static DependencyManager getInstance() {
        return instance;
    }
}
