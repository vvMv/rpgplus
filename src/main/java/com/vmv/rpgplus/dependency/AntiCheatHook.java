package com.vmv.rpgplus.dependency;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface AntiCheatHook {

    /**
     * Exempt a player from a fast-break check in the hooked anticheat
     *
     * @param player the player to exempt
     */
    public void exempt(@NotNull Player player);

    /**
     * Unexempt a player from a fast-break check in the hooked anticheat
     *
     * @param player the player to unexempt
     */
    public void unexempt(@NotNull Player player);

    /**
     * Get the name of the plugin representing this hook
     *
     * @return this plugin hook
     */
    @NotNull
    public String getPluginName();

    public default boolean isSupported() {
        return true;
    }
}
