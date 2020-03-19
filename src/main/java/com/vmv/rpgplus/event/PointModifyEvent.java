package com.vmv.rpgplus.event;

import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.skill.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PointModifyEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Ability ability;
    private final RPGPlayer rp;

    public PointModifyEvent(RPGPlayer rp, Ability ability) {
        super(Bukkit.getPlayer(rp.getUuid()));
        this.ability = ability;
        this.rp = rp;
    }

    public Ability getAbility() {
        return this.ability;
    }

    public RPGPlayer getRp() {
        return rp;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}