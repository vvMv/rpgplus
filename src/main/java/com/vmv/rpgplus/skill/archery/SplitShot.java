package com.vmv.rpgplus.skill.archery;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SplitShot extends Ability implements Listener {

    public SplitShot(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.description = "fire a rally of arrows";
        this.cooldown = 0;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent e) {

        if (!checkReady(e.getEntity())) return;

        Player p = (Player) e.getEntity();
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(p);
        int amount = (int) rp.getAttributeValue(this, AbilityAttribute.INCREASE_ARROWS);
        int angle = getAbilityConfigSection().getInt("angle");
        int separation = angle / amount;

        if (((Player) e.getEntity()).getGameMode() != GameMode.CREATIVE && !e.getBow().containsEnchantment(Enchantment.ARROW_INFINITE)) {
            int am = 0;
            for (ItemStack item : p.getInventory().getContents()) {
                if (item == null) continue;
                if (item.getType() == XMaterial.ARROW.parseMaterial()) am += item.getAmount();
            }
            if (amount > am) amount = am;
            p.getInventory().removeItem(new ItemStack(XMaterial.ARROW.parseMaterial(), amount));
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
            a.setPickupStatus(((Player) e.getEntity()).getGameMode() == GameMode.CREATIVE || e.getBow().containsEnchantment(Enchantment.ARROW_INFINITE) ? Arrow.PickupStatus.CREATIVE_ONLY : Arrow.PickupStatus.ALLOWED);
            a.setCustomName("splitshot_arrow");
            a.setBounce(false);
            //a.setDamage(((Arrow) e.getProjectile()).getDamage());
            a.setFireTicks(e.getProjectile().getFireTicks());
        }
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), XSound.ENTITY_ARROW_SHOOT.parseSound(), 1F, 1F);
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