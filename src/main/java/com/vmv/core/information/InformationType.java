package com.vmv.core.information;

import org.bukkit.ChatColor;

public enum InformationType {

    INFO(ChatColor.AQUA),
    DEBUG(ChatColor.DARK_AQUA),
    WARN(ChatColor.YELLOW),
    ERROR(ChatColor.RED),
    NONE(ChatColor.GRAY);

    private ChatColor c;

    InformationType(ChatColor c) {
        this.c = c;
    }

    public ChatColor getChatColor() {
        return c;
    }
}
