package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
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
        inventoryContents.set(1, 1, ClickableItem.of(InventoryUtils.getItem(XMaterial.BLAZE_POWDER, "&e&lAbilities", 1, "&7Toggle your active abilties"), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("settings_ability_title", player), 6, new RPGAbilitiesMenu(), "").open(player);
        }));
        inventoryContents.set(1, 3, ClickableItem.of(InventoryUtils.getItem(XMaterial.ENDER_EYE, "&e&lAbility Points", 1, "&7Spend your ability points (" + (int)rp.getAbilityPoints() + ")"), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("settings_select_points_title", player), 1, new RPGSelectPointsMenu(), "").open(player);
        }));
        inventoryContents.set(1, 5, ClickableItem.of(InventoryUtils.getItem(XMaterial.PAPER,"&e&lNotifications", 1, "&7Toggle rpg notifications"), e -> {
            InventoryUtils.sendClickSound(player);
            InventoryUtils.getInventory(InventoryUtils.getTitle("settings_notification_title", player), 3, new RPGNotificationsMenu(), "").open(player);
        }));
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
