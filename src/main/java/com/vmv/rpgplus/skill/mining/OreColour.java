package com.vmv.rpgplus.skill.mining;

import org.bukkit.ChatColor;

public enum OreColour {

    COAL_ORE(ChatColor.BLACK),
    IRON_ORE(ChatColor.GOLD),
    GOLD_ORE(ChatColor.YELLOW),
    LAPIS_ORE(ChatColor.BLUE),
    DIAMOND_ORE(ChatColor.AQUA),
    REDSTONE_ORE(ChatColor.RED),
    EMERALD_ORE(ChatColor.GREEN);

    private ChatColor color;

    OreColour(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }
}
