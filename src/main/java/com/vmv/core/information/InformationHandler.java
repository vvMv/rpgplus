package com.vmv.core.information;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class InformationHandler {

    public InformationType type;
    public String info;
    public static Plugin plugin;

    public InformationHandler(Plugin plugin) {
        this.plugin = plugin;
        printMessage(InformationType.INFO, "Information handler registered");
    }

    public static void printMessage(InformationType type, String info) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "[" + plugin.getName() + "]" + type.getChatColor() + " [" + type.toString() + "] &r" + info));
    }

}
