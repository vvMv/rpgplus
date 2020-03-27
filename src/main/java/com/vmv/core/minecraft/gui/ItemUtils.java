package com.vmv.core.minecraft.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemUtils {

    public static ItemStack createItem(String name, Material material, String lore, int amount) {

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        ArrayList<String> lore1 = new ArrayList<>();

        lore1.add(ChatColor.translateAlternateColorCodes('&', lore));

        meta.setLore(lore1);
        item.setItemMeta(meta);
        return item;

    }

}
