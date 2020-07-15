package com.vmv.core.minecraft.chat;

import com.google.common.base.Strings;
import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatUtil {

    private static HashMap<UUID, Integer> barPriority = new HashMap<UUID, Integer>();
    private static HashMap<UUID, Long> barTimestamp = new HashMap<UUID, Long>();

    public static void sendChatMessage(CommandSender s, String message) {
        sendChatMessage(s, message, RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.other"));
    }

    public static void sendChatMessage(CommandSender s, String message, int priority) {
        if (s == null) return;
        if (message.length() == 0) return;
        if (message.startsWith("<center>") || message.startsWith("<centre>") && s instanceof Player) {
            sendCenteredChatMessage((Player) s, message.replace("<center>", "").replace("<centre>", ""));
        } else if (message.startsWith("<actionbar>") && s instanceof Player) {
            sendActionMessage((Player) s, message.replace("<actionbar>", ""), priority);
        } else if (message.startsWith("<floating>") && s instanceof Player) {
            sendFloatingMessage((Player) s, message.replace("<floating>", ""), 2.5);
        } else {
            s.sendMessage(applyColour(FileManager.getLang().getString("prefix") + message));
        }
    }

    private static void sendCenteredChatMessage(Player p, String message) {
        CenteredMessageUtils.send(p, applyColour(message));
    }

//    private static void sendActionMessage(Player p, String message) {
//        sendActionMessage(p, message, RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.other"));
//    }

    private static void sendActionMessage(Player p, String message, int priority) {

        barPriority.putIfAbsent(p.getUniqueId(), 0);
        barTimestamp.putIfAbsent(p.getUniqueId(), (long) 0);

        if (priority >= barPriority.get(p.getUniqueId()) || System.currentTimeMillis() - barTimestamp.get(p.getUniqueId()) >= RPGPlus.getInstance().getConfig().getInt("actionbar.priority.timer")) {
            ActionMessageUtils.send(p, message);
            barPriority.put(p.getUniqueId(), priority);
            barTimestamp.put(p.getUniqueId(), System.currentTimeMillis());
        }
    }

    private static void sendFloatingMessage(Player p, String message, double seconds) {
        FloatingMessageUtils.send(p, applyColour(message), seconds);
    }

    public static String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor, ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);
        return Strings.repeat("" + completedColor + symbol, progressBars) + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

    private static String applyColour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}