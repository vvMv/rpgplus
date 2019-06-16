package com.vmv.core.minecraft.chat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionMessage {
    public static void send(Player player, String message) {

        Player p = Bukkit.getPlayer(player.getName()); // Weird bug leave alone
        BaseComponent[] bc = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', message)).create();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);

    }

    public static void send(Plugin plugin, final Player player, final String message, int duration) {
        send(player, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allows messages
            // shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    send(player, "");
                }
            }.runTaskLater(plugin, duration + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the
        // player's screen.
        while (duration > 60) {
            duration -= 60;
            int sched = duration % 60;
            new BukkitRunnable() {
                @Override
                public void run() {
                    send(player, message);
                }
            }.runTaskLater(plugin, sched);
        }
    }

    public static void sendToAll(Plugin plugin, String message) {
        sendToAll(plugin ,message, -1);
    }

    public static void sendToAll(Plugin plugin,String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(plugin ,p, message, duration);
        }
    }
}
