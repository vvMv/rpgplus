package com.vmv.rpgplus.skill.stamina;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.event.PointModifyEvent;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Health extends Ability implements Listener {

    private int defaultHearts;

    public Health(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
        this.defaultHearts = getAbilityConfigSection().getInt("hearts");
    }

    @EventHandler
    public void updateHealthEvent(PlayerJoinEvent e) {
        updateHearts(e.getPlayer());
    }

    @EventHandler
    public void healthAbilityEvent(PointModifyEvent e) {
        if (e.getAbility() == AbilityManager.getAbility("health")) updateHearts(e.getPlayer());
    }

    private void updateHearts(Player player) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);
        double amount = defaultHearts + (rp.getPointAllocation(this, AbilityAttribute.INCREASE_HEARTS) * AbilityAttribute.INCREASE_HEARTS.getValuePerPoint(this));
        player.setHealthScale(amount);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(amount);
        InformationHandler.printMessage(InformationType.DEBUG, "set to " + amount);
    }
}
