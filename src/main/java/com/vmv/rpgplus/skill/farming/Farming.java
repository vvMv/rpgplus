package com.vmv.rpgplus.skill.farming;

import com.sk89q.worldguard.protection.flags.Flags;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.DependencyManager;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class Farming extends Skill implements Listener {

    ArrayList<String> farmed = new ArrayList<String>();

    public Farming(SkillType skillType) {
        super(skillType);
    }

    @Override
    protected void registerAbilities() {

    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkBlockBreak(BlockBreakEvent e) {

        InformationHandler.printMessage(InformationType.DEBUG, "broken " + e.getBlock().getY());

        if (e.isCancelled()) return;
        if (!super.hasBuildPermission(e.getBlock().getLocation(), e.getPlayer()) || !super.hasMaterial(e.getPlayer())) return;

        if (!farmed.contains(e.getBlock().toString())) {
            for (String b : getConfig().getConfigurationSection("experience").getKeys(false)) {
                if (e.getBlock().getType() == Material.valueOf(b)) {
                    if (e.getBlock().getRelative(BlockFace.UP).getType() == e.getBlock().getType()) {
                        InformationHandler.printMessage(InformationType.DEBUG, "true");
                        checkBlockBreak(new BlockBreakEvent(e.getBlock().getRelative(BlockFace.UP), e.getPlayer()));
                        //new BlockBreakEvent(e.getBlock().getRelative(BlockFace.UP), e.getPlayer());
                    }
                    double xp = MathUtils.getRandom(getConfig().getDouble("experience." + b + ".max"), getConfig().getDouble("experience." + b + ".min"));
//                    for (int y = e.getBlock().getY(); y < e.getBlock().getWorld().getHighestBlockYAt(e.getBlock().getLocation()); y++) {
//                        if (farmed.contains(e.getBlock().getLocation(new Location(e.getBlock().getWorld(), e.getBlock().getX(), y, e.getBlock().getZ())).getBlock().toString())) return;
//                        if (e.getBlock().getLocation(new Location(e.getBlock().getWorld(), e.getBlock().getX(), y, e.getBlock().getZ())).getBlock().getType() == Material.valueOf(b)) {
//                            xp += MathUtils.getRandom(getConfig().getDouble("experience." + b + ".max"), getConfig().getDouble("experience." + b + ".min"));
//                        } else {
//                            break;
//                        }
//                    }
                    RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).addXP(SkillType.FARMING, xp);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        farmed.add(e.getBlock().toString());
    }

}
