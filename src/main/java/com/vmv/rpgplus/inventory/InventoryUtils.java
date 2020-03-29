package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.vmv.core.config.FileManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryManager;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class InventoryUtils {

    public static InventoryManager manager;

    public InventoryUtils(Plugin plugin) {
        manager = new InventoryManager((JavaPlugin) plugin);
        manager.init();
    }

    public static InventoryManager getManager() {
        return manager;
    }

    public static SmartInventory getInventory(String title, int rows, InventoryProvider provider, String id) {
        SmartInventory inv = SmartInventory.builder()
                .provider(provider)
                .size(rows, 9)
                .title(ChatColor.translateAlternateColorCodes('&', title))
                .manager(getManager())
                .id(id)
                .build();
        return inv;
    }

    public static void addBackItem(Player player, InventoryContents contents) {
        contents.set(contents.inventory().getRows()-1, contents.inventory().getColumns()-1, ClickableItem.of(getItem(XMaterial.BARRIER, "&c&lMenu"), e -> {
            sendClickSound(player);
            getInventory(getTitle("settings_menu_title", player), 3, new RPGMenu(), "").open(player);
        }));
    }

    public static ItemStack getItem(XMaterial material) {
        return getItem(material, " ");
    }

    public static ItemStack getItem(XMaterial material, String name) {
        return getItem(material, name,1);
    }

    public static ItemStack getItem(XMaterial material, String name, int amount,  String... lore) {
        ItemStack i = material.parseItem();
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        List<String> l = new ArrayList<>();
        for (String s : lore) { l.add(ChatColor.translateAlternateColorCodes('&', s)); }
        meta.setLore(l);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        i.setItemMeta(meta);
        return i;
    }

    public static String getTitle(String langPath, Player player) {
        return FileManager.getLang().getString(langPath).replace("%p", player.getName());
    }

    public static void sendDeniedSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), XSound.ENTITY_VILLAGER_NO.parseSound(), 1f, 1f);
    }

    public static void sendConfirmedSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), XSound.ENTITY_CHICKEN_EGG.parseSound(), 1f, 1f);
    }

    public static void sendClickSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), XSound.BLOCK_COMPARATOR_CLICK.parseSound(), 1f, 1f);
    }


}
