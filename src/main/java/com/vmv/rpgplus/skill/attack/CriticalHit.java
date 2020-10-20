package com.vmv.rpgplus.skill.attack;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CriticalHit extends Ability implements Listener {
    public CriticalHit(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
        this.description = "Gives chance of a critical hit";
    }

    @EventHandler
    public void criticalHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        if (e.isCancelled()) return;

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer((Player) e.getDamager());

        double critChance = rp.getAttributeValue(this, AbilityAttribute.INCREASE_CHANCE);
        double critAmount = rp.getAttributeValue(this, AbilityAttribute.INCREASE_CRITICAL);

        if (MathUtils.getRandom(100, 1) <= critChance) {
            e.setDamage(e.getDamage() * critAmount);
            Location l = e.getEntity().getLocation();
            e.getEntity().getWorld().playSound(l, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.0f, 1.0f);
            //e.getEntity().getWorld().spawnParticle(Particle.REDSTONE, l.add(0, 1.5, 0), 0, 1, 0, 0, 0, new Particle.DustOptions(Color.GREEN, 2));
        }

    }

}
