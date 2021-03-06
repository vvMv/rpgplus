package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.*;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

public class RPGPointsMenu implements InventoryProvider {
    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        int xcount = 0;
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);
        Skill skill = SkillManager.getInstance().getSkill(SkillType.valueOf(inventoryContents.inventory().getId().toUpperCase()));

        inventoryContents.fill(ClickableItem.empty(InventoryUtils.getItem(XMaterial.GRAY_STAINED_GLASS_PANE)));

        for (Ability ability : AbilityManager.getInstance().getAbilities(skill.getSkillType())) {
            inventoryContents.set(0, xcount, ClickableItem.empty(InventoryUtils.getItem(XMaterial.BOOK, "&e" + ability.getFormattedName(), 1, FileManager.getLang().getString("inventory.item_points_upgrade_lore"))));

            int ycount = 1;
            for (AbilityAttribute attribute : ability.getAttributes()) {
                boolean max = rp.getPointAllocation(ability, attribute) >= attribute.getValueMaxPoint(ability) ? true : false;
                boolean noPoints = rp.getAbilityPoints(skill) < 1 ? true : false;
                inventoryContents.set(ycount, xcount, ClickableItem.of(InventoryUtils.getItem(max ? XMaterial.BLUE_STAINED_GLASS_PANE : noPoints ? XMaterial.YELLOW_STAINED_GLASS_PANE : XMaterial.GREEN_STAINED_GLASS_PANE, "&2&l" + attribute.getFormattedName(), 1, "&7" + ability.getFormattedName() + " " + attribute.getDescription(), " ", max ? "&e" + attribute.getFormattedName().substring(attribute.getFormattedName().lastIndexOf(" ")+1) + " &7" + (attribute.getBaseValue (ability) + (attribute.getValuePerPoint(ability) * rp.getPointAllocation(ability, attribute))) : "&e" + attribute.getFormattedName().substring(attribute.getFormattedName().lastIndexOf(" ")+1) + " &7" + (attribute.getBaseValue (ability) + (attribute.getValuePerPoint(ability) * rp.getPointAllocation(ability, attribute)) + "&e -> &7" + (attribute.getBaseValue (ability) + (rp.getPointAllocation(ability, attribute) * attribute.getValuePerPoint(ability)) + attribute.getValuePerPoint(ability))), "&e" + FileManager.getLang().getString("inventory.item_points_tile_points") + " &7" + (int) rp.getPointAllocation(ability, attribute) + "&e / &7" + (int) attribute.getValueMaxPoint(ability), " ", max ? FileManager.getLang().getString("inventory.item_points_tile_max") : noPoints ? FileManager.getLang().getString("inventory.item_points_tile_none") : FileManager.getLang().getString("inventory.item_points_tile_spend")), e -> {
                    if (rp.attemptSetPointAllocation(ability, attribute, (int) rp.getPointAllocation(ability, attribute) + 1, false)) {
                        InventoryUtils.sendConfirmedSound(player);
                    } else {
                        InventoryUtils.sendDeniedSound(player);
                    }
                    InventoryUtils.getInventory(InventoryUtils.getTitle("inventory.title_points", player).replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%n", (int) rp.getAbilityPoints(skill) + "").replace("%t", (int)rp.getOverallPoints(skill) + ""), 6, new RPGPointsMenu(), skill.getSkillType().toString()).open(player);
                    //init(player, inventoryContents);
                }));
                ycount++;
            }
            xcount++;
        }

        //menu.setItem(new ItemStack(XMaterial.ENDER_EYE.parseMaterial(), points < 1 ? 1 : (int) points), ChatColor.YELLOW + "Points: " + points, 49, "&7Click on the green tiles", "&7to upgrade your abilities");

        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
