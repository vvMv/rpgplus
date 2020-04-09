package com.vmv.rpgplus.dependency;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityManager;
import me.konsolas.aac.api.HackType;
import me.konsolas.aac.api.PlayerViolationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AAC implements Listener {

    @EventHandler
    public void preventMiningViolation(PlayerViolationEvent e) {
        Ability a = AbilityManager.getInstance().getAbility("vein_miner");
        if (a == null) return;
        if (e.getHackType() != HackType.FASTBREAK) return;
        if (a.getActive().containsKey(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void preventWoodcuttingViolation(PlayerViolationEvent e) {
        Ability a = AbilityManager.getInstance().getAbility("tree_feller");
        if (a == null) return;
        if (e.getHackType() != HackType.FASTBREAK) return;
        if (a.getActive().containsKey(e.getPlayer())) e.setCancelled(true);
    }

}
