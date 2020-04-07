package com.vmv.rpgplus.skill.stamina;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FeatherFalling extends Ability implements Listener {
    public FeatherFalling(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
        this.description = "Chance to take reduced fall damage";
    }

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer((Player) e.getEntity());
        double reduction = rp.getAttributeValue(this, AbilityAttribute.INCREASE_REDUCTION);
        double chance = rp.getAttributeValue(this, AbilityAttribute.INCREASE_CHANCE);


        if (MathUtils.getRandomPercentage() <= chance) {
            e.setDamage((e.getFinalDamage() / 100) * (100 - reduction));
        }
    }

}
