package com.vmv.rpgplus.skill.stamina;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.event.AbilityToggleEvent;
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
        if (e.getPlayer() == null) return;
        if (e.getAbility() == AbilityManager.getInstance().getAbility("health")) updateHearts(e.getPlayer());
    }

    @EventHandler
    public void healthToggled(AbilityToggleEvent e) {
        updateHearts(e.getPlayer());
        if (e.getToggleTo()) {
            InformationHandler.printMessage(InformationType.DEBUG, "toggled true");
        } else {
            InformationHandler.printMessage(InformationType.DEBUG, "to false");
        }
    }

    private void updateHearts(Player player) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);
        double amount = RPGPlayerManager.getInstance().getPlayer(player).hasAbilityEnabled(this) ? defaultHearts + (rp.getPointAllocation(this, AbilityAttribute.INCREASE_HEARTS) * AbilityAttribute.INCREASE_HEARTS.getValuePerPoint(this)) : defaultHearts;
        player.setHealthScale(amount);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(amount);
    }
}
