package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.HashMap;

public class TeleportArrow extends Ability implements Listener {

    private static final int TELEPORT_INVALID_TIME_MILLIS = 10000;
    private HashMap<Projectile, Long> arrows;

    public TeleportArrow(String name, SkillType st) {
        super(name, st);
        this.description = "Fire an arrow that teleports you to its location";
        this.arrows = new HashMap<Projectile, Long>();
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        arrows.put((Projectile) e.getProjectile(), System.currentTimeMillis());
        e.getProjectile().setGlowing(true);

    }

    @EventHandler
    public void onArrowLand(ProjectileHitEvent e) {
        if (arrows.containsKey(e.getEntity())) {
            if (System.currentTimeMillis() - arrows.get(e.getEntity()) > TELEPORT_INVALID_TIME_MILLIS) {
                arrows.remove(e.getEntity());
                return;
            }
            Player p = (Player) e.getEntity().getShooter();
            Location loc = e.getEntity().getLocation();
            p.teleport(new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
            p.damage(getAbilityConfigSection().getInt("selfdamage"));
            e.getEntity().setGlowing(false);
            arrows.remove(e.getEntity());
        }
    }
}
