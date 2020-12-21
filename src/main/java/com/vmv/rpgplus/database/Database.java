package com.vmv.rpgplus.database;

import com.vmv.rpgplus.player.RPGPlayer;

import java.sql.ResultSet;
import java.util.UUID;

public interface Database {

    void createTables();

    /**
     * Add a new user to the database.
     *
     * @param uuid The uuid of the player to be added to the database
     */
    void newPlayer(UUID uuid);

    /**
     * Save a user to the database.
     *
     * @return true if successful, false on failure
     */
    boolean savePlayers(boolean aSync);

    /**
     * Save a user to the database.
     *
     * @return true if successful, false on failure
     */
    boolean savePlayer(RPGPlayer player, boolean aSync);

    /**
     * Load a player from the database.
     *
     * @param uuid The uuid of the player to load from the database
     * @return The player's dataset
     */
    RPGPlayer fetchPlayer(UUID uuid);

    ResultSet fetchUUIDs();

    /**
     * Called when the plugin disables
     */
    void onDisable();
}