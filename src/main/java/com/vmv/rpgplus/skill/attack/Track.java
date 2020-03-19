package com.vmv.rpgplus.skill.attack;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class Track extends Ability implements Listener {
    
    public Track(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.description = "Makes the target glow";
    }

    @EventHandler
    public void onTrackHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof LivingEntity)) return;
        if (!checkReady((LivingEntity) e.getDamager())) return;

        if (e.getEntity().isGlowing()) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK && e.getCause() != EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;
        if (!isHoldingAbilityItem((Player) e.getDamager())) return;

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), new Runnable() {
            public void run() {
                e.getEntity().setGlowing(false);
            }
        }, (long) (getDuration((Player) e.getDamager()) * 20));

        e.getEntity().setGlowing(true);
    }

}
