package com.vmv.rpgplus.inventory;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import org.apache.commons.lang.WordUtils;
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
            inventoryContents.set(0, xcount, ClickableItem.of(InventoryUtils.getItem(skill.getDisplay(), skill.getSkillColor() + WordUtils.capitalizeFully(skill.getSkillType().name()), 1, "&7Press below to toggle", "&7your " + skill.getSkillType().name().toLowerCase() + " abilities"), e -> {
                InventoryUtils.sendClickSound(player);
                InventoryUtils.getInventory(InventoryUtils.getTitle("settings_points_title", player).replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%n", (int) rp.getAbilityPoints(skill) + "").replace("%t", (int)rp.getOverallPoints(skill) + ""), 6, new RPGPointsMenu(), skill.getSkillType().toString()).open(player);
            }));

            List<Ability> abilities = AbilityManager.getInstance().getAbilities(skill.getSkillType()).stream().sorted(Comparator.comparingDouble(Ability::getRequiredLevel)).collect(Collectors.toList()); //sorts abiltiy by level
            int ycount = 1;
            for (Ability ability : abilities) {
                if (ability.isPassive()) continue;
                inventoryContents.set(ycount, xcount, ClickableItem.of(InventoryUtils.getItem(rp.hasAbilityLevelRequirement(ability) ? (rp.hasAbilityEnabled(ability) ? XMaterial.GREEN_WOOL : XMaterial.ORANGE_WOOL) : XMaterial.RED_WOOL, ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), 1, "&fActive: " + (rp.hasAbilityEnabled(ability) && rp.hasAbilityLevelRequirement(ability) ? "&aTrue" : "&cFalse"), "&fRequired: " + (rp.hasAbilityLevelRequirement(ability) ? "&aLevel " + ability.getRequiredLevel() : "&cLevel " + ability.getRequiredLevel())), e -> {
                    if (rp.hasAbilityLevelRequirement(ability)) {
                        rp.toggleAbilityEnabled(ability);
                        init(player, inventoryContents);
                        InventoryUtils.sendConfirmedSound(player);
                        ChatUtil.sendChatMessage(player, FileManager.getLang().getString("settings_ability_toggle").replaceAll("%a", ability.getFormattedName()).replaceAll("%s", rp.hasAbilityEnabled(ability) ? FileManager.getLang().getString("settings_ability_toggle_activated") : FileManager.getLang().getString("settings_ability_toggle_deactivated")));
                    } else {
                        InventoryUtils.sendDeniedSound(player);
                        ChatUtil.sendChatMessage(player, FileManager.getLang().getString("settings_ability_toggle_denied").replaceAll("%a", ability.getFormattedName()).replaceAll("%r", String.valueOf(ability.getRequiredLevel())));
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
