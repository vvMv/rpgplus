package com.vmv.core.minecraft.chat;

import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatUtil {

    public static void sendChatMessage(CommandSender s, String message) {
        if (s == null) return;
        if (message.length() == 0) return;
        s.sendMessage(applyColour(FileManager.getLang().getString("prefix") + message));
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
