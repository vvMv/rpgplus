package com.vmv.rpgplus.event;

import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class LevelModifyEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final SkillType skill;
    private final int toLevel, fromLevel;
    private final RPGPlayer rpgPlayer;

    public LevelModifyEvent(RPGPlayer rp, SkillType skill, int fromLevel, int toLevel) {
        super(Bukkit.getPlayer(rp.getUuid()));
        this.skill = skill;
        this.toLevel = toLevel;
        this.fromLevel = fromLevel;
        this.rpgPlayer = rp;
    }

    public SkillType getSkill() {
        return this.skill;
    }

    public int getLevel() {
        return toLevel;
    }

    public int getFromLevel() {
        return fromLevel;
    }

    public int getToLevel() {
        return toLevel;
    }

    public RPGPlayer getRpgPlayer() {
        return rpgPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}