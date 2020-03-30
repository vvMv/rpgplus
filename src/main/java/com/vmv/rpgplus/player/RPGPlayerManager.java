package com.vmv.rpgplus.player;

import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

public class RPGPlayerManager implements Listener {

    private static RPGPlayerManager instance;
    private ArrayList<RPGPlayer> players;

    public RPGPlayerManager() {
        instance = this;
        players = new ArrayList<RPGPlayer>();
        RPGPlus.getInstance().registerEvents(this, new RPGPlayerEvents());
    }

    public ArrayList<RPGPlayer> getPlayers() {
        return players;
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public RPGPlayer getPlayer(UUID uuid) {
        for (RPGPlayer p : players) {
            if (p.getUuid().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public RPGPlayer getPlayer(Player p) {
        return getPlayer(p.getUniqueId());
    }

    public void addPlayer(RPGPlayer p) {
        players.add(p);
    }

}
