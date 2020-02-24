package com.vmv.rpgplus.skill.archery;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.DependencyManager;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
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
        registerAbilities(new MultiArrow("multi_arrow", ARCHERY),
                new ExplosiveArrow("explosive_arrow", ARCHERY),
                new TeleportArrow("teleport_arrow", ARCHERY),
                new SplitShot("split_shot", ARCHERY));
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
