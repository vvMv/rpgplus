package com.vmv.rpgplus.skill.archery;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class MultiArrow extends Ability implements Listener {

    public MultiArrow(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.description = "Fire multiple arrows one after another";
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        final Vector velocity = e.getProjectile().getVelocity();
        Player p = ((Player) e.getEntity());
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(p);
        double firstArrowSpeed = velocity.length();
        int arrowAmount = (int) rp.getAttributeValue(this, AbilityAttribute.INCREASE_ARROWS);
        for (int i = 0; i < arrowAmount; i++) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!e.getBow().containsEnchantment(Enchantment.ARROW_INFINITE) && p.getGameMode() != GameMode.CREATIVE) {
                        if (!p.getInventory().containsAtLeast(new ItemStack(Material.ARROW), 1)) return;
                        p.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
                    }
                    Arrow a = p.launchProjectile(Arrow.class);
                    Vector playerCameraVector = p.getPlayer().getEyeLocation().getDirection().normalize();
                    a.setVelocity(playerCameraVector.multiply(firstArrowSpeed));
                    a.setShooter(e.getEntity());
                    a.setPickupStatus(((Player) e.getEntity()).getGameMode() == GameMode.CREATIVE || e.getBow().containsEnchantment(Enchantment.ARROW_INFINITE) ? Arrow.PickupStatus.CREATIVE_ONLY : Arrow.PickupStatus.ALLOWED);
                    a.setCustomName("multishot_arrow");
                    a.setBounce(false);
                    a.setDamage(((Arrow) e.getProjectile()).getDamage());
                    a.setFireTicks(e.getProjectile().getFireTicks());
                    e.getEntity().setNoDamageTicks(0);
                    a.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
                }
            }, i * getAbilityConfigSection().getInt("delay"));
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
