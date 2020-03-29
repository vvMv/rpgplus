package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.rpgplus.database.PlayerSetting;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.entity.Player;

public class RPGNotificationsMenu implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        RPGPlayer target = RPGPlayerManager.getInstance().getPlayer(player);
        inventoryContents.fill(ClickableItem.empty(InventoryUtils.getItem(XMaterial.GRAY_STAINED_GLASS_PANE)));

        inventoryContents.set(0, 0, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 0, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 0, ClickableItem.of(InventoryUtils.getItem(XMaterial.EXPERIENCE_BOTTLE, "&e&lShow Experience Popups", 1, target.getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS) ? "&7Value: &aTrue" : "&7Value: &cFalse", "&8Click to toggle"), e -> {
            target.toggleSetting(PlayerSetting.EXPERIENCE_POPUPS);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        inventoryContents.set(0, 1, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 1, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 1, ClickableItem.of(InventoryUtils.getItem(XMaterial.BOOK, "&e&lShow Level Up Messages", 1, target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? "&7Value: &aTrue" : "&7Value: &cFalse", "&8Click to toggle"), e -> {
            target.toggleSetting(PlayerSetting.LEVELUP_MESSAGES);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        inventoryContents.set(0, 2, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 2, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 2, ClickableItem.of(InventoryUtils.getItem(XMaterial.CLOCK, "&e&lShow Point Reminder Messages", 1, target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? "&7Value: &aTrue" : "&7Value: &cFalse", "&8Click to toggle"), e -> {
            target.toggleSetting(PlayerSetting.REMINDER_MESSAGES);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
