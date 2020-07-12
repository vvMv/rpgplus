package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
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
        inventoryContents.set(1, 0, ClickableItem.of(InventoryUtils.getItem(XMaterial.EXPERIENCE_BOTTLE, FileManager.getLang().getString("inventory.item_notifications_experience"), 1, FileManager.getLang().getString("inventory.item_notifications_experience_value").replace("%b", target.getSettingBoolean(PlayerSetting.EXPERIENCE_POPUPS) ? FileManager.getLang().getString("inventory.bool_true") : FileManager.getLang().getString("inventory.bool_false"))), e -> {
            target.toggleSetting(PlayerSetting.EXPERIENCE_POPUPS);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        inventoryContents.set(0, 1, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 1, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 1, ClickableItem.of(InventoryUtils.getItem(XMaterial.BOOK, FileManager.getLang().getString("inventory.item_notifications_levelup"), 1, FileManager.getLang().getString("inventory.item_notifications_levelup_value").replace("%b", target.getSettingBoolean(PlayerSetting.LEVELUP_MESSAGES) ? FileManager.getLang().getString("inventory.bool_true") : FileManager.getLang().getString("inventory.bool_false"))), e -> {
            target.toggleSetting(PlayerSetting.LEVELUP_MESSAGES);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        inventoryContents.set(0, 2, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 2, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 2, ClickableItem.of(InventoryUtils.getItem(XMaterial.CLOCK, FileManager.getLang().getString("inventory.item_notifications_pointreminder"), 1, FileManager.getLang().getString("inventory.item_notifications_pointreminder_value").replace("%b", target.getSettingBoolean(PlayerSetting.REMINDER_MESSAGES) ? FileManager.getLang().getString("inventory.bool_true") : FileManager.getLang().getString("inventory.bool_false"))), e -> {
            target.toggleSetting(PlayerSetting.REMINDER_MESSAGES);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        inventoryContents.set(0, 3, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.EXPERIENCE_ACTIONBAR) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(2, 3, ClickableItem.empty(InventoryUtils.getItem(target.getSettingBoolean(PlayerSetting.EXPERIENCE_ACTIONBAR) ? XMaterial.GREEN_STAINED_GLASS_PANE : XMaterial.RED_STAINED_GLASS_PANE)));
        inventoryContents.set(1, 3, ClickableItem.of(InventoryUtils.getItem(XMaterial.OAK_SIGN, FileManager.getLang().getString("inventory.item_notifications_experience_actionbar"), 1, FileManager.getLang().getString("inventory.item_notifications_experience_actionbar_value").replace("%b", target.getSettingBoolean(PlayerSetting.EXPERIENCE_ACTIONBAR) ? FileManager.getLang().getString("inventory.bool_true") : FileManager.getLang().getString("inventory.bool_false"))), e -> {
            target.toggleSetting(PlayerSetting.EXPERIENCE_ACTIONBAR);
            InventoryUtils.sendConfirmedSound(player);
            init(player, inventoryContents);
        }));

        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }

}
