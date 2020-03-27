package com.vmv.core.minecraft.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class PrivateInventory {

	private Inventory inv;
	private static Map<UUID, PrivateInventory> inventories = new HashMap<>();
	private Map<Integer, ClickRunnable> runnables = new HashMap<>();
	private UUID uuid;

	public PrivateInventory(String name, int size, UUID uuid) {
		this(name, size, uuid, null);
	}

	public PrivateInventory(String name, int size, UUID uuid, ItemStack placeholder) {
		this.uuid = uuid;
		if (size == 0) {
			return;
		}
		this.inv = Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', name));
		if (placeholder != null) {
			for (int i = 0; i < size; i++) {
				inv.setItem(i, placeholder);
			}
		}
		this.register();
	}

	public Inventory getInventory() {
		return inv;
	}

	public int getSize() {
		return inv.getSize();
	}

	public void setItem(ItemStack itemstack, Integer slot) {
		inv.setItem(slot, itemstack);
	}

	public void setItem(ItemStack itemstack, String displayname, Integer slot, String... description) {
		setItem(itemstack, displayname, slot, null, description);
	}

	public void setItem(ItemStack itemstack, String displayname, Integer slot, ClickRunnable executeOnClick,
						String... description) {
		ItemStack is = itemstack;
		ItemMeta im = is.getItemMeta();
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		if (displayname != null) {
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayname));
		}
		if (description != null) {
			List<String> lore = new ArrayList<String>();
			for (String string : description) {
				lore.add(ChatColor.translateAlternateColorCodes('&', string));
			}
			im.setLore(lore);
		}
		is.setItemMeta(im);
		inv.setItem(slot, is);
		if (executeOnClick != null) {
			runnables.put(slot, executeOnClick);
		}
	}

	public void removeItem(int slot) {
		inv.setItem(slot, new ItemStack(Material.AIR));
	}

	public static Listener getListener() {
		return new Listener() {
			@EventHandler
			public void onClick(InventoryClickEvent e) {
				HumanEntity clicker = e.getWhoClicked();
				if (clicker instanceof Player) {
					if (e.getCurrentItem() == null) {
						return;
					}
					Player p = (Player) clicker;
					if (p != null) {
						UUID uuid = p.getUniqueId();
						if (inventories.containsKey(uuid)) {
							PrivateInventory current = inventories.get(uuid);
							if (e.getClickedInventory() != current.getInventory()) {
								return;
							}
							e.setCancelled(true);
							int slot = e.getSlot();
							if (current.runnables.get(slot) != null) {
								current.runnables.get(slot).run(e);
							}
						}
					}
				}
			}

			@EventHandler
			public void onClose(InventoryCloseEvent e) {
				if (e.getPlayer() instanceof Player) {
					if (e.getInventory() == null) {
						return;
					}
					Player p = (Player) e.getPlayer();
					UUID uuid = p.getUniqueId();
					if (inventories.containsKey(uuid)) {
						inventories.get(uuid).unRegister();
					}
				}
			}
		};
	}

	public void openInventory(Player player) {
		Inventory inv = getInventory();
		InventoryView openInv = player.getOpenInventory();
		if (openInv != null) {
			Inventory openTop = player.getOpenInventory().getTopInventory();
			if (openTop != null && openTop.equals(inv)) {
				openTop.setContents(inv.getContents());
			} else {
				player.openInventory(inv);
			}
			register();
		}
	}

	private void register() {
		inventories.put(this.uuid, this);
	}

	private void unRegister() {
		inventories.remove(this.uuid);
	}

	@FunctionalInterface
	public interface ClickRunnable {
		public void run(InventoryClickEvent event);
	}

	@FunctionalInterface
	public interface CloseRunnable {
		public void run(InventoryCloseEvent event);
	}

}