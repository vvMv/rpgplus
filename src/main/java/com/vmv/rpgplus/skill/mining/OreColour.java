package com.vmv.rpgplus.skill.mining;

import org.bukkit.ChatColor;

public enum OreColour {

    COAL_ORE(ChatColor.BLACK, "rpg_coal"),
    IRON_ORE(ChatColor.GOLD, "rpg_iron"),
    GOLD_ORE(ChatColor.YELLOW,"rpg_gold") ,
    LAPIS_ORE(ChatColor.BLUE,"rpg_gold"),
    DIAMOND_ORE(ChatColor.AQUA, "rpg_diamond"),
    REDSTONE_ORE(ChatColor.RED, "rpg_redstone"),
    EMERALD_ORE(ChatColor.GREEN, "rpg_emerald"),
    NETHER_QUARTZ_ORE(ChatColor.WHITE, "rpg_quartz"),
    NETHER_GOLD_ORE(ChatColor.GOLD, "rpg_nethergold");

    private ChatColor color;
    private String teamname;

    OreColour(ChatColor color, String teamname) {
        this.color = color;
        this.teamname = teamname;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getTeamname() {
        return teamname;
    }
}
