package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
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
            inventoryContents.set(0, xcount, ClickableItem.of(InventoryUtils.getItem(skill.getDisplay(), skill.getFormattedName(), 1, FileManager.getLang().getString("inventory.item_select_skill_icon_lore").replace("%c", "&" + skill.getSkillColor().getChar()).replace("%n", (int)rp.getAbilityPoints(skill) + "")), e -> {
                InventoryUtils.sendClickSound(player);
                InventoryUtils.getInventory(InventoryUtils.getTitle("inventory.title_points", player).replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%n", (int) rp.getAbilityPoints(skill) + "").replace("%t", (int)rp.getOverallPoints(skill) + ""), 6, new RPGPointsMenu(), skill.getSkillType().toString()).open(player);
            }));
            xcount++;
        }

        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents inventoryContents) {

    }
}
