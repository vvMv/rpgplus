package com.vmv.rpgplus.skill.archery;

import com.cryptomorin.xseries.XSound;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;

public class ExplosiveArrow extends Ability implements Listener {

    private ArrayList<Projectile> arrows = new ArrayList<Projectile>();

    public ExplosiveArrow(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.description = "Fires an arrow that will explode on impact";
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
            float size = (float) RPGPlayerManager.getInstance().getPlayer((Player) e.getEntity().getShooter()).getAttributeValue(this, AbilityAttribute.INCREASE_EXPLOSION);
            Location l = e.getEntity().getLocation();
            e.getEntity().getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), size, false, getAbilityConfigSection().getBoolean("destructive"));
            e.getEntity().getWorld().playSound(e.getEntity().getLocation(), XSound.ENTITY_DRAGON_FIREBALL_EXPLODE.parseSound(), 3.0f, 1.0f);
            e.getEntity().remove();
        }

    }

}
