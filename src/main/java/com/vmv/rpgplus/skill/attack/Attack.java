package com.vmv.rpgplus.skill.attack;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static com.vmv.rpgplus.skill.SkillType.ATTACK;

public class Attack extends Skill implements Listener {
    public Attack(SkillType skillType) {
        super(skillType);
    }

    @Override
    protected void registerAbilities() {
        registerAbilities(new Track("track", ATTACK, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_DURATION),
                new LifeSteal("life_steal", ATTACK, AbilityAttribute.INCREASE_LIFE_STEAL, AbilityAttribute.INCREASE_CHANCE),
                new CriticalHit("critical_hit", ATTACK, AbilityAttribute.INCREASE_CRITICAL, AbilityAttribute.INCREASE_CHANCE));
    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwordSwing(EntityDamageByEntityEvent e) {

        if (e.isCancelled()) return;

        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            for (NPC n : CitizensAPI.getNPCRegistry()) {
                if (e.getEntity() == n.getEntity()) {
                    return;
                }
            }
        }

        if (!(e.getDamager() instanceof Player) || e.getEntity() instanceof ArmorStand || e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        if (!super.hasMaterial((Player) e.getDamager()) || !super.hasDamagePermission((Player) e.getDamager())) return;

        double xp = MathUtils.round(e.getDamage() * getConfig().getDouble("damage_multiply_exp"), 2);
        RPGPlayerManager.getInstance().getPlayer((Player) e.getDamager()).addXP(ATTACK, xp);
    }


}
