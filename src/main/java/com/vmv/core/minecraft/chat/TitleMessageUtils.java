package com.vmv.core.minecraft.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TitleMessageUtils {

    private static int time;

    public static void send(Player player, String message) {

        if (player == null) return;
        Player p = Bukkit.getPlayer(player.getName()); // Weird bug leave alone

        String lines[] = message.split("<subtitle>");
        String title = ChatUtil.applyColour(lines[0]);
        String subTitle = "";
        if (lines.length > 1) subTitle = ChatUtil.applyColour(lines[1]);
        p.sendTitle(title, subTitle, 10, 70, 20);

    }

    public static void sendToAll(Plugin plugin, String title) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(p, title);
        }
    }

}
