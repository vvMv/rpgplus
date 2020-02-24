package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.core.minecraft.gui.Item;
import com.vmv.core.minecraft.gui.PrivateInventory;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import fr.minuskube.netherboard.Netherboard;
import jdk.internal.jline.internal.Nullable;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RPGPlayerSettings {

    public static void openMenu(Player p, RPGPlayer target) {

        Player p2 = Bukkit.getPlayer(target.getUuid());
        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_menu_title").replaceAll("%p", p2.getName())), 27, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));

        menu.setItem(new ItemStack(Material.NAME_TAG), "&e&lDisplay Scoreboard", 12,
                new PrivateInventory.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {

                        ItemMeta im = e.getCurrentItem().getItemMeta();
                        im.setLore(Arrays.asList(Netherboard.instance().getBoard(p2) == null ? ChatColor.translateAlternateColorCodes('&', "&7Value: &aTrue") : ChatColor.translateAlternateColorCodes('&', "&7Value: &cFalse"), ChatColor.translateAlternateColorCodes('&', "&8Click to toggle")));
                        e.getCurrentItem().setItemMeta(im);
                        target.toggleScoreboard();
                    }
                }, Netherboard.instance().getBoard(p2) == null ? "&7Value: &cFalse" : "&7Value: &aTrue", "&8Click to toggle");

        menu.setItem(new ItemStack(Material.BLAZE_POWDER), "&e&lAbilities", 10,
                new PrivateInventory.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        p.closeInventory();
                        openSkillsMenu(p, target);
                    }
                }, "&7Enable/Disable abilities");

        menu.openInventory(p);

    }

    private static void openSkillsMenu(Player p, RPGPlayer target) {

        Player p2 = Bukkit.getPlayer(target.getUuid());
        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_skills_title").replaceAll("%p", p2.getName())), 9, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));
        int count = 0;

        for (Skill skill : SkillManager.getInstance().getSkills()) {
            menu.setItem(new ItemStack(skill.getDisplay()), skill.getSkillColor() + WordUtils.capitalizeFully(skill.getSkillType().name()), count,
                    new PrivateInventory.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                            p.closeInventory();
                            openSkillAbilityMenu(p, target, skill);
                        }
                    }, "&7Edit your " + skill.getSkillType().name().toLowerCase() + " abilities");
            count++;
        }

        addBackButton(menu, target);
        menu.openInventory(p);

    }

    private static void openSkillAbilityMenu(Player p, RPGPlayer target, Skill skill) {
        Player p2 = Bukkit.getPlayer(target.getUuid());
        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_skills_ability_title").replaceAll("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replaceAll("%p", p2.getName())), 9, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));

        int count = 0;
        List<Ability> abilities = AbilityManager.getAbilities(skill.getSkillType()).stream().sorted(Comparator.comparingDouble(Ability::getRequiredLevel)).collect(Collectors.toList());
        for (Ability ability : abilities) {
            menu.setItem(new ItemStack(target.hasAbilityLevelRequirement(ability) ? (target.hasAbilityEnabled(ability) ? Material.GREEN_WOOL : Material.ORANGE_WOOL) : Material.RED_WOOL), ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), count,
                    new PrivateInventory.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                            if (target.hasAbilityLevelRequirement(ability)) {
                                target.toggleAbilityEnabled(ability);
                                p.closeInventory();
                                openSkillAbilityMenu(p, target, skill);
                                ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_skills_ability_toggle").replaceAll("%a", ability.getFormattedName()).replaceAll("%s", target.hasAbilityEnabled(ability) ? FileManager.getLang().getString("settings_skills_ability_toggle_activated") : FileManager.getLang().getString("settings_skills_ability_toggle_deactivated")));
                            } else {
                                ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_skills_ability_toggle_denied").replaceAll("%a", ability.getFormattedName()).replaceAll("%r", String.valueOf(ability.getRequiredLevel())));
                            }
                        }
                    }, "&fActive: " + (target.hasAbilityEnabled(ability) && target.hasAbilityLevelRequirement(ability) ? "&aTrue" : "&cFalse"), "&fRequired: " + (target.hasAbilityLevelRequirement(ability) ? "&aLevel " + ability.getRequiredLevel() : "&cLevel " + ability.getRequiredLevel()));
            count++;
        }

        addBackButton(menu, target);
        menu.openInventory(p);
    }

    private static void addBackButton(PrivateInventory invFrom, RPGPlayer target) {
        invFrom.setItem(new ItemStack(Material.BARRIER), "&c&lBack", invFrom.getSize() - 1,
                new PrivateInventory.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        e.getWhoClicked().closeInventory();
                        openMenu((Player) e.getWhoClicked(), target);
                    }
                });

    }

}
