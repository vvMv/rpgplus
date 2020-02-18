package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class MultiArrow extends Ability implements Listener {

    private int extra;

    public MultiArrow(String name, SkillType st) {
        super(name, st);
        this.description = "Fire multiple arrows one after another";
        this.extra = getAbilityConfigSection().getInt("extra");
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        final Vector velocity = e.getProjectile().getVelocity();
        double playerLevel = RPGPlayerManager.getInstance().getPlayer((Player) e.getEntity()).getLevel(SkillType.ARCHERY);

        for (int i = 0; i < (playerLevel / extra) + 1; i++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Arrow a = e.getEntity().launchProjectile(Arrow.class);
                    a.setVelocity(velocity);
                    a.setShooter(e.getEntity());
                    a.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
                    a.setCustomName("multishot_arrow");
                    a.setBounce(false);
                    a.setDamage(((Arrow) e.getProjectile()).getDamage());
                    a.setCritical(true);
                    e.getEntity().setNoDamageTicks(0);
                    a.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.5F, 1.0F * 0.15F);

                }
            }, i * 3);
        }

        e.setCancelled(true);

    }

}
