package com.vmv.rpgplus.skill.archery;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.vmv.rpgplus.skill.SkillType.ARCHERY;

public class Archery extends Skill implements Listener {

    public Archery(SkillType skillType) {
        super(skillType);
    }

    @Override
    public void registerAbilities() {
        registerAbilities(new MultiArrow("multi_arrow", ARCHERY, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_ARROWS),
                new ExplosiveArrow("explosive_arrow", ARCHERY, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_EXPLOSION),
                new TeleportArrow("teleport_arrow", ARCHERY, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.DECREASE_SELFDAMAGE),
                new SplitShot("split_shot", ARCHERY, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_ARROWS));
    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void damageByArrow(EntityDamageByEntityEvent e) {

        if (e.isCancelled()) return;

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            for (NPC n : CitizensAPI.getNPCRegistry()) {
                if (e.getEntity() == n.getEntity()) {
                    return;
                }
            }
        }

        if (e.getEntity() instanceof ArmorStand || !(e.getEntity() instanceof LivingEntity)) {
            return;
        }

        if (!(e.getDamager() instanceof Arrow)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        //if (!(e.getEntity() instanceof Player)) return;

        Arrow a = (Arrow) e.getDamager();
        if (!(a.getShooter() instanceof Player)) return;

        Player shooter = (Player) a.getShooter();

        double distanceXP = shooter.getLocation().distance(e.getEntity().getLocation()) / getConfig().getDouble("divide_distance_exp");

        if (distanceXP > getConfig().getDouble("max_distance_exp")) {
            distanceXP = getConfig().getDouble("max_distance_exp");
        }

        if (!super.hasMaterial(shooter) || !super.hasDamagePermission(shooter)) return;

        if (!getConfig().getBoolean("allow_distance_exp")) distanceXP = 0;

        double xp = MathUtils.round(distanceXP + e.getDamage(), 2);

        RPGPlayerManager.getInstance().getPlayer(shooter).addXP(ARCHERY, xp);

    }

}
