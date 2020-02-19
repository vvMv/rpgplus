package com.vmv.rpgplus.skill.archery;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
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
        Player p = ((Player) e.getEntity());

        for (int i = 0; i < (playerLevel / extra) + 1; i++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (p.getGameMode() != GameMode.CREATIVE) {
                        if (!p.getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) return;
                        p.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                    }
                    Arrow a = p.launchProjectile(Arrow.class);
                    double firstArrowSpeed = (Math.abs(velocity.getX()) + Math.abs(velocity.getY()) + Math.abs(velocity.getZ()));
                    Vector playerCameraVector = p.getPlayer().getEyeLocation().getDirection().normalize();
                    a.setVelocity(playerCameraVector.multiply(firstArrowSpeed));
                    a.setShooter(e.getEntity());
                    a.setPickupStatus(((Player) e.getEntity()).getGameMode() == GameMode.CREATIVE ? Arrow.PickupStatus.CREATIVE_ONLY : Arrow.PickupStatus.ALLOWED);
                    a.setCustomName("multishot_arrow");
                    a.setBounce(false);
                    a.setDamage(((Arrow) e.getProjectile()).getDamage());
                    a.setFireTicks(e.getProjectile().getFireTicks());
                    a.setCritical(true);
                    e.getEntity().setNoDamageTicks(0);
                    a.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }, i * 2);
        }

        e.setCancelled(true);

    }

    @EventHandler
    public void onEntityHit(EntityDamageByEntityEvent e) {
        if (e.getDamager().getType() != EntityType.ARROW) return;
        if (!(e.getEntity() instanceof LivingEntity)) return;
        Projectile arrow = (Arrow) e.getDamager();
        if (!arrow.getName().equalsIgnoreCase("multishot_arrow")) return;
        ((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(0);
        ((LivingEntity) e.getEntity()).setNoDamageTicks(0);
    }
}
