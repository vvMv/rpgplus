package com.vmv.core.information;

import org.bukkit.ChatColor;

public enum InformationType {

    INFO(ChatColor.GRAY),
    WARN(ChatColor.YELLOW),
    ERROR(ChatColor.RED);

    private ChatColor c;

    InformationType(ChatColor c) {
        this.c = c;
    }

    public ChatColor getChatColor() {
        return c;
    }
}
