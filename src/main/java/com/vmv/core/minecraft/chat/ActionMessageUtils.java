package com.vmv.core.minecraft.chat;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionMessageUtils {

    private static int time;

    public static void send(Player player, String message) {

        if (player == null) return;

        Player p = Bukkit.getPlayer(player.getName()); // Weird bug leave alone
        BaseComponent[] bc = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', message)).create();
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, bc);

    }


    public static void send(Plugin plugin, final Player player, final String message, int duration) {
        time = duration;
        send(player, message);

        if (time >= 0) {
            // Sends empty message at the end of the duration. Allows messages
            // shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    send(player, "");
                }
            }.runTaskLater(plugin, time + 1);
        }

        // Re-sends the messages every 3 seconds so it doesn't go away from the
        // player's screen.
        while (time > 60) {
            time -= 60;
            int sched = time % 60;
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
            send(plugin, p, message, duration);
        }
    }
}
