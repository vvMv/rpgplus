package com.vmv.rpgplus.player;

import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;

public class RPGPlayerManager implements Listener {

    private static RPGPlayerManager instance;
    private ArrayList<RPGPlayer> loadedPlayers;
    private ArrayList<UUID> playerUUIDs;

    public RPGPlayerManager() {
        instance = this;
        loadedPlayers = new ArrayList<RPGPlayer>();
        playerUUIDs = new ArrayList<UUID>();
        RPGPlus.getInstance().registerEvents(this, new RPGPlayerEvents());
    }

    public ArrayList<RPGPlayer> getLoadedPlayers() {
        return loadedPlayers;
    }

    public static RPGPlayerManager getInstance() {
        return instance;
    }

    public RPGPlayer getPlayer(UUID uuid) {
        for (RPGPlayer p : loadedPlayers) {
            if (p.getUuid().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public RPGPlayer getPlayer(Player p) {
        return getPlayer(p.getUniqueId());
    }

    public void loadPlayer(RPGPlayer p) {
        loadedPlayers.add(p);
    }

    public void unloadPlayer(UUID uuid) {
        loadedPlayers.remove(uuid);
    }

    public void addUUID(UUID uuid) {
        playerUUIDs.add(uuid);
    }

    public ArrayList<UUID> getPlayerUUIDs() {
        return playerUUIDs;
    }
}
