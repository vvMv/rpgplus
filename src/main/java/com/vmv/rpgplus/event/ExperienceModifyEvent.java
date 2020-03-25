package com.vmv.rpgplus.event;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.ArrayList;

public class ExperienceModifyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final RPGPlayer rp;
    private final SkillType skill;
    private double exp;

    private static ArrayList<ArmorStand> animationStands = new ArrayList<>();

    public ExperienceModifyEvent(RPGPlayer rp, SkillType skill, double exp) {
        super(Bukkit.getPlayer(rp.getUuid()));
        this.rp = rp;
        this.skill = skill;
        this.exp = MathUtils.round(exp, 2);
    }

    public double getExp() {
        return this.exp;
    }

    public SkillType getSkill() {
        return this.skill;
    }

    public RPGPlayer getRPGPlayer() {
        return this.rp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public static ArrayList<ArmorStand> getAnimationStands() {
        return animationStands;
    }
}