package com.vmv.rpgplus.skill.attack;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Random;

import static com.vmv.core.minecraft.chat.CenteredMessage.DefaultFontInfo.p;

public class Attack extends Skill implements Listener {
    public Attack(SkillType skillType) {
        super(skillType);
        registerEvents(this);
    }

    @EventHandler
    public void onSwordSwing(EntityDamageByEntityEvent e) {

//        if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
//            for (NPC n : CitizensAPI.getNPCRegistry()) {
//                if (e.getEntity() == n.getEntity()) {
//                    return;
//                }
//            }
//        }

        if (!(e.getDamager() instanceof Player) || e.getEntity() instanceof ArmorStand || e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }

        double xp = MathUtils.round(e.getDamage(), 2);
        RPGPlayerManager.getInstance().getPlayer((Player) e.getDamager()).addXP(SkillType.ATTACK, xp);
    }


}
