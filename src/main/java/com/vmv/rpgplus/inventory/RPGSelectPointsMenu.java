package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.Player;

public class RPGSelectPointsMenu implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);
        inventoryContents.fill(ClickableItem.empty(InventoryUtils.getItem(XMaterial.GRAY_STAINED_GLASS_PANE)));

        int xcount = 0;
        for (Skill skill : SkillManager.getInstance().getSkills()) {
            inventoryContents.set(0, xcount, ClickableItem.of(InventoryUtils.getItem(skill.getDisplay(), skill.getSkillColor() + WordUtils.capitalizeFully(skill.getSkillType().name()), 1, "&7You have " + skill.getSkillColor() + (int)rp.getAbilityPoints(skill) + " &7points"), e -> {
                InventoryUtils.sendClickSound(player);
                InventoryUtils.getInventory(InventoryUtils.getTitle("settings_points_title", player).replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%n", (int) rp.getAbilityPoints(skill) + "").replace("%t", (int)rp.getOverallPoints(skill) + ""), 6, new RPGPointsMenu(), skill.getSkillType().toString()).open(player);
            }));
            xcount++;
        }

        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
