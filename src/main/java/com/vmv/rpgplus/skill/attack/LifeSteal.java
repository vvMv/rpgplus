package com.vmv.rpgplus.skill.attack;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Random;

public class LifeSteal extends Ability implements Listener {

    private Random r;

    public LifeSteal(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
        this.description = "Chance to steal health when in combat";
        this.r = new Random();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void lifeStealHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        if (e.isCancelled()) return;

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer((Player) e.getDamager());

        double stealChance = rp.getAttributeValue(this, AbilityAttribute.INCREASE_LIFE_STEAL_CHANCE);
        double stealAmount = rp.getAttributeValue(this, AbilityAttribute.INCREASE_LIFE_STEAL);

        if (MathUtils.getRandom(100, 1) <= stealChance) {
            try {
                ((Player) e.getDamager()).setHealth(((Player) e.getDamager()).getHealth() + (int) Math.ceil((e.getDamage() / (stealAmount / 10))));
            } catch (Exception ignore) {
                //already full health
            }
            Location l = e.getEntity().getLocation();
            e.getEntity().getWorld().spawnParticle(Particle.REDSTONE, l.add(0, 1.5, 0), 0, 1, 0, 0, 0, new Particle.DustOptions(Color.GREEN, 2));
        }

    }

}
