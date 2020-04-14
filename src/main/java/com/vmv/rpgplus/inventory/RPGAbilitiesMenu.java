package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RPGAbilitiesMenu implements InventoryProvider {
    @Override
    public void init(Player player, InventoryContents inventoryContents) {

        int xcount = 0;
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);

        inventoryContents.fill(ClickableItem.empty(InventoryUtils.getItem(XMaterial.GRAY_STAINED_GLASS_PANE)));

        for (Skill skill : SkillManager.getInstance().getSkills()) {
            inventoryContents.set(0, xcount, ClickableItem.of(InventoryUtils.getItem(skill.getDisplay(), skill.getFormattedName(), 1, FileManager.getLang().getString("inventory.item_abilities_toggle_icon")), e -> {
                List<Ability> abs = AbilityManager.getInstance().getAbilities(skill.getSkillType());
                if (abs.size() > 0) {
                    if (!rp.hasAbilityLevelRequirement(abs.get(0))) return;
                    if (rp.hasAbilityEnabled(abs.get(0))) abs.forEach(ability -> rp.toggleAbilityEnabled(ability, false));
                    else abs.forEach(ability -> rp.toggleAbilityEnabled(ability, true));
                    InventoryUtils.sendConfirmedSound(player);
                    init(player, inventoryContents);
                }
            }));
            List<Ability> abilities = AbilityManager.getInstance().getAbilities(skill.getSkillType()).stream().sorted(Comparator.comparingDouble(Ability::getRequiredLevel)).collect(Collectors.toList()); //sorts ability by level
            int ycount = 1;
            for (Ability ability : abilities) {
                inventoryContents.set(ycount, xcount, ClickableItem.of(InventoryUtils.getItem(rp.hasAbilityLevelRequirement(ability) ? (rp.hasAbilityEnabled(ability) ? XMaterial.GREEN_WOOL : XMaterial.ORANGE_WOOL) : XMaterial.RED_WOOL, ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), 1, FileManager.getLang().getString("inventory.item_abilities_toggle_active").replace("%b", (rp.hasAbilityEnabled(ability) && rp.hasAbilityLevelRequirement(ability) ? FileManager.getLang().getString("inventory.bool_true") : FileManager.getLang().getString("inventory.bool_false"))), FileManager.getLang().getString("inventory.item_abilities_toggle_required").replace("%c", (rp.hasAbilityLevelRequirement(ability) ? "&a" : "&c")).replace("%l", ability.getRequiredLevel() + "")), e -> {
                    if (rp.hasAbilityLevelRequirement(ability)) {
                        rp.toggleAbilityEnabled(ability);
                        init(player, inventoryContents);
                        InventoryUtils.sendConfirmedSound(player);
                    } else {
                        InventoryUtils.sendDeniedSound(player);
                    }
                })); ycount++;
            } xcount++;
        }
        InventoryUtils.addBackItem(player, inventoryContents);
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
