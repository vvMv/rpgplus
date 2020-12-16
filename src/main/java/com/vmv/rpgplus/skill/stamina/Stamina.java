package com.vmv.rpgplus.skill.stamina;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.UUID;

public class Stamina extends Skill implements Listener {

    private double expDistance;
    public static HashMap<UUID, Double> distance = new HashMap<UUID, Double>();

    public Stamina(SkillType skillType) {
        super(skillType);
        this.expDistance = getConfig().getDouble("experience.distance");
        double time = getConfig().getDouble("experience.time");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RPGPlus.getInstance(), () -> grantExperience(), (long) (time) * 20, (long) (time) * 20);
    }

    @Override
    protected void registerAbilities() {
        registerAbilities(new Dash("dash", getSkillType(), AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_DURATION, AbilityAttribute.INCREASE_SPEED),
                new Health("health", getSkillType(), AbilityAttribute.INCREASE_HEARTS),
                new FeatherFalling("feather_falling", getSkillType(), AbilityAttribute.INCREASE_CHANCE, AbilityAttribute.INCREASE_REDUCTION));
    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler
    public void calculateExperience(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.isFlying()) return;
        if (distance.containsKey(p.getUniqueId())) {
            distance.put(e.getPlayer().getUniqueId(), e.getTo().distance(e.getFrom()) + distance.get(e.getPlayer().getUniqueId()) - Math.abs((e.getTo().getY() - e.getFrom().getY())));
        } else {
            distance.put(e.getPlayer().getUniqueId(), e.getTo().distance(e.getFrom()));
        }
    }

    private void grantExperience() {
        for (RPGPlayer rp : RPGPlayerManager.getInstance().getLoadedPlayers()) {
            if (!distance.containsKey(rp.getUuid())) continue;
            rp.addXP(SkillType.STAMINA, distance.get(rp.getUuid()) * expDistance);
        }
        distance.clear();
    }
}
