package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class RPGMenu implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents inventoryContents) {
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);
        inventoryContents.fill(ClickableItem.empty(InventoryUtils.getItem(XMaterial.GRAY_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 3, ClickableItem.of(InventoryUtils.getItem(XMaterial.BLAZE_POWDER, FileManager.getLang().getString("inventory.item_abilities"), 1, FileManager.getLang().getString("inventory.item_abilities_lore")), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("inventory.title_abilities", player), 6, new RPGAbilitiesMenu(), "").open(player);
        }));
        inventoryContents.set(1, 5, ClickableItem.of(InventoryUtils.getItem(XMaterial.ENDER_EYE, FileManager.getLang().getString("inventory.item_ability_points"), 1, FileManager.getLang().getString("inventory.item_ability_points_lore").replace("%n", String.valueOf((int)rp.getAbilityPoints()))), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("inventory.title_select_skill", player), 1, new RPGSelectPointsMenu(), "").open(player);
        }));
        inventoryContents.set(1, 8, ClickableItem.of(InventoryUtils.getItem(XMaterial.PAPER, FileManager.getLang().getString("inventory.item_notifications"), 1, FileManager.getLang().getString("inventory.item_notifications_lore")), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("inventory.title_notifications", player), 3, new RPGNotificationsMenu(), "").open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
