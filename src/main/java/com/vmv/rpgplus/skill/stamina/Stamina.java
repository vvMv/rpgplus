package com.vmv.rpgplus.skill.stamina;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

public class Stamina extends Skill implements Listener {

    private double expDistance, time;
    public static HashMap<UUID, Double> distance = new HashMap<UUID, Double>();

    public Stamina(SkillType skillType) {
        super(skillType);
        this.expDistance = getConfig().getDouble("experience.distance");
        this.time = getConfig().getDouble("experience.time");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RPGPlus.getInstance(), () -> grantExperience(), (long) (time) * 20, (long) (time) * 20);
    }

    @Override
    protected void registerAbilities() {
        registerAbilities(new Dash("dash", getSkillType()));
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
        for (RPGPlayer rp : RPGPlayerManager.getInstance().getPlayers()) {
            if (!distance.containsKey(rp.getUuid())) continue;
            rp.addXP(SkillType.STAMINA, distance.get(rp.getUuid()) * expDistance);
            distance.remove(rp.getUuid());
        }
    }
}
