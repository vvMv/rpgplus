package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class ExplosiveArrow extends Ability implements Listener {

    private ArrayList<Projectile> arrows = new ArrayList<Projectile>();

    public ExplosiveArrow(String name, SkillType st) {
        super(name, st);
        this.description = "Fires an explosive arrow";
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;
        arrows.add((Projectile) e.getProjectile());

    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent e) {
        if (arrows.contains(e.getEntity())) {
            arrows.remove(e.getEntity());
            e.getEntity().getWorld().createExplosion(e.getEntity().getLocation(), (float)getAbilityConfigSection().getDouble("power"), false, getAbilityConfigSection().getBoolean("destructive"));
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 3.0f, 1.0f);
        }

    }

}
