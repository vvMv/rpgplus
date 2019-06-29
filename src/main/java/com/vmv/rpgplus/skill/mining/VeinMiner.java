package com.vmv.rpgplus.skill.mining;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.misc.BarTimer;
import com.vmv.core.minecraft.misc.BarTimerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;

public class VeinMiner extends Ability implements Listener {

    public List<Material> blocks = new ArrayList<>();

    public VeinMiner(String name, SkillType st) {
        super(name, st);
        try {
            getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(Material.valueOf(b)));
        } catch (IllegalArgumentException e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.vein_miner.blocks", e.getMessage(), "This error is coming from mining.yml" );
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e) {

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) setActive(e.getPlayer(), getDuration());

        if (isActive(e.getPlayer())) {

        }

    }

}
