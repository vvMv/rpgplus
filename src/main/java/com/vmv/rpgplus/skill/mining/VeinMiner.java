package com.vmv.rpgplus.skill.mining;

import com.vmv.core.minecraft.misc.BarTimer;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class VeinMiner extends Ability implements Listener {
    public VeinMiner(String name, SkillType st) {
        super(name, st);
    }

    @EventHandler
    public void BreakBlockEvent(BlockBreakEvent e) {

        if (!checkReady(e.getPlayer())) return;

        new BarTimer(e.getPlayer(), getAbilityConfigSection().getDouble("duration"), getName() + " active");
        Bukkit.broadcastMessage("Activated vein miner");

    }

}
