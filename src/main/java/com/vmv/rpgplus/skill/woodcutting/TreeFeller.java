package com.vmv.rpgplus.skill.woodcutting;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class TreeFeller extends Ability implements Listener {

    public static List<Material> leaves = new ArrayList<>();
    public static List<Material> logs = new ArrayList<>();

    private int maxsize;
    private double delay;

    public TreeFeller(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        delay = getAbilityConfigSection().getDouble("delay");
        maxsize = getAbilityConfigSection().getInt("maxsize");
        try { getAbilityConfigSection().getStringList("logs").forEach(b -> logs.add(Material.valueOf(b))); } catch (IllegalArgumentException e) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.tree_feller.logs", e.getMessage(), "This error is coming from woodcutting.yml" ); }
        try { getAbilityConfigSection().getStringList("leaves").forEach(b -> leaves.add(Material.valueOf(b))); } catch (IllegalArgumentException e) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.tree_feller.leaves", e.getMessage(), "This error is coming from woodcutting.yml" ); }
    }

    @EventHandler
    public void onChop(BlockBreakEvent e) {

        if (e.isCancelled()) return;

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (!logs.contains(e.getBlock().getType())) return;
        if (checkReady(e.getPlayer())) {
            setActive(e.getPlayer(), getDuration(e.getPlayer()));
        }

        if (isActive(e.getPlayer())) {
            new TreeCutter(e.getPlayer(), e.getBlock(), delay, maxsize).runTaskAsynchronously(RPGPlus.getInstance());
            e.setCancelled(true);
        }

    }

}

