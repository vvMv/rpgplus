package com.vmv.rpgplus.skill.fishing;

import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class ReplenishHunger extends Ability implements Listener {

    public ReplenishHunger(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
    }

    @EventHandler
    public void replenishHunger(PlayerFishEvent e) {
        if (RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).getLevel(SkillType.FISHING) < getRequiredLevel()) return;
        if (!isEnabled(e.getPlayer())) return;
        if (e.getCaught() == null) return;
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        e.getPlayer().setFoodLevel(e.getPlayer().getFoodLevel() + 1);
    }


}
