package com.vmv.core.minecraft.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static void sendChatMessage(String prefix, CommandSender s, String message) {
        s.sendMessage(applyColour(prefix + "&7" + message));
    }

    public static void sendCenteredChatMessage(Player p, String message) {
        CenteredMessage.send(p, applyColour(message));
    }

    public static void sendActionMessage(Player p, String message) {
        ActionMessage.send(p, applyColour(message));
    }

    private static String applyColour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
