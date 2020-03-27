package com.vmv.core.information;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class InformationHandler {

    private InformationType type;
    private String info;
    private static Plugin plugin;

    public InformationHandler(Plugin plugin) {
        this.plugin = plugin;
        printMessage(InformationType.INFO, "Information handler registered");
    }

    private static void printMessage(InformationType type, String info) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[" + plugin.getName() + "]" + type.getChatColor() + " " + type.toString() + ": &r" + info));
    }

    public static void printMessage(InformationType type, String info, String... additional) {
        printMessage(type, info);
        for (String s : additional) {
            printMessage(type, s);
        }
    }

}
