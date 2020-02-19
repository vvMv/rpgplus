package com.vmv.rpgplus.skill.archery;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SplitShot extends Ability implements Listener {

    private int extra;

    public SplitShot(String name, SkillType st) {
        super(name, st);
        this.description = "fire a rally of arrows";
        this.extra = getAbilityConfigSection().getInt("extra");
        this.cooldown = 0;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        double playerLevel = RPGPlayerManager.getInstance().getPlayer((Player) e.getEntity()).getLevel(SkillType.ARCHERY);
        int amount = ((int) (playerLevel / extra)) + 1;
        int angle = getAbilityConfigSection().getInt("angle");
        int separation = angle / amount;
        Player p = (Player) e.getEntity();

        Location loc = e.getEntity().getLocation();

        if (((Player) e.getEntity()).getGameMode() != GameMode.CREATIVE) {
            int am = 0;
            for (ItemStack item : p.getInventory().getContents()) {
                if (item == null) continue;
                if (item.getType() == Material.ARROW) am += item.getAmount();
            }
            if (amount > am) amount = am;
            p.getInventory().removeItem(new ItemStack(Material.ARROW, amount));
        }

        for (int i = 0; i < amount; i++) {

            //loc.setYaw((e.getEntity().getLocation().getYaw() -(angle/2)) + (i * separation));
            Arrow a = e.getEntity().launchProjectile(Arrow.class);
            Vector velocity = e.getProjectile().getVelocity();
            rotateVector(velocity, (e.getEntity().getLocation().getYaw() -(angle/2)) + (i * separation));

//            double arrowAngle = 45;
//            double totalAngle = loc.getYaw() + arrowAngle;
//            double arrowDirX = Math.sin(totalAngle);
//            double arrowDirZ = Math.cos(totalAngle);
//            Vector arrowDir = new Vector(arrowDirX, loc.getDirection().getY(), arrowDirZ);
//
//
            a.setVelocity(velocity);

            a.setShooter(e.getEntity());
            a.setPickupStatus(p.getGameMode() == GameMode.CREATIVE ? AbstractArrow.PickupStatus.CREATIVE_ONLY : AbstractArrow.PickupStatus.ALLOWED);
            a.setCustomName("splitshot_arrow");
            a.setBounce(false);
            a.setDamage(((Arrow) e.getProjectile()).getDamage());
            a.setFireTicks(e.getProjectile().getFireTicks());
            //a.getWorld().playSound(a.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1F, 1F);
        }
        e.setCancelled(true);
    }

    private Vector rotateVector(Vector vector, double whatAngle) {
        double sin = Math.sin(whatAngle);
        double cos = Math.cos(whatAngle);
        double x = vector.getX() * cos + vector.getZ() * sin;
        double z = vector.getX() * -sin + vector.getZ() * cos;

        return vector.setX(x).setZ(z);
    }
}