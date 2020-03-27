package com.vmv.rpgplus.event;

import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.skill.Ability;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class AbilityCycleEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Ability ability;

    public AbilityCycleEvent(RPGPlayer rpgPlayer, Ability a) {
        super(Bukkit.getPlayer(rpgPlayer.getUuid()));
        this.ability = a;
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

    public Ability getAbility() {
        return ability;
    }
}
