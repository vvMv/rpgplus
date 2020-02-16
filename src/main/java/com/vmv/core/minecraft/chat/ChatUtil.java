package com.vmv.core.minecraft.chat;

import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.UUID;

public class ChatUtil {

    private static HashMap<UUID, Integer> barPriority = new HashMap<UUID, Integer>();
    private static HashMap<UUID, Long> barTimestamp = new HashMap<UUID, Long>();

    public static void sendChatMessage(CommandSender s, String message) {
        if (s == null) return;
        if (message.length() == 0) return;
        s.sendMessage(applyColour(FileManager.getLang().getString("prefix") + message));
    }

    public static void sendCenteredChatMessage(Player p, String message) {
        CenteredMessage.send(p, applyColour(message));
    }

    public static void sendActionMessage(Player p, String message) {
        sendActionMessage(p, message, RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.other"));
    }

    public static void sendActionMessage(Player p, String message, int priority) {

        if (barPriority.get(p.getUniqueId()) == null) barPriority.put(p.getUniqueId(), 0);
        if (barTimestamp.get(p.getUniqueId()) == null) barTimestamp.put(p.getUniqueId(), (long) 0);

        if (priority >= barPriority.get(p.getUniqueId()) || System.currentTimeMillis() - barTimestamp.get(p.getUniqueId()) >= RPGPlus.getInstance().getConfig().getInt("actionbar.priority.timer")) {
            ActionMessage.send(p, message);
            barPriority.put(p.getUniqueId(), priority);
            barTimestamp.put(p.getUniqueId(), System.currentTimeMillis());
        }
    }


    private static String applyColour(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}