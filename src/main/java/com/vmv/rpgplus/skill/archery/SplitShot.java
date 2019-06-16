package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.Vector;

public class SplitShot extends Ability implements Listener {

    private int extra;

    public SplitShot(String name, SkillType st) {
        super(name, st);
        this.description = "fire a rally of arrows";
        this.extra = getAbilityConfigSection().getInt("extra");
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        //double playerLevel = RPGPlayerManager.getInstance().getPlayer((Player) e.getEntity()).getLevel(SkillType.ARCHERY);
        //int amount = (int) (playerLevel / extra) + 1;
        int angle = 50;//getAbilityConfigSection().getInt("angle");
        int separation = angle / 5;

        for (int i = 0; i < 5; i++) {
            Arrow a = e.getEntity().launchProjectile(Arrow.class);
            Vector velocity = e.getProjectile().getVelocity();
            velocity.multiply(i);
            a.setVelocity(velocity);
            a.setShooter(e.getEntity());
            a.setPickupStatus(Arrow.PickupStatus.CREATIVE_ONLY);
            a.setCustomName("splitshot_arrow");
            a.setBounce(false);
            a.setCritical(true);
            a.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.5F, 1.0F * 0.15F);

        }
        e.setCancelled(true);
    }
}
