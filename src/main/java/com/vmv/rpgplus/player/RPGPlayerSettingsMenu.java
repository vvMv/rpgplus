package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.core.minecraft.gui.Item;
import com.vmv.core.minecraft.gui.PrivateInventory;
import com.vmv.rpgplus.skill.*;
import fr.minuskube.netherboard.Netherboard;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RPGPlayerSettingsMenu {

    public static RPGPlayerSettingsMenu instance;

    public RPGPlayerSettingsMenu() {
        instance = this;
    }

    public static RPGPlayerSettingsMenu getInstance() {
        return instance;
    }

    public void openMenu(Player p, RPGPlayer target) {

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
                        sendClickSound(p);
                    }
                }, "&7Enable/Disable abilities");

        menu.openInventory(p);

    }

    public void openSkillsMenu(Player p, RPGPlayer target) {

        Player p2 = Bukkit.getPlayer(target.getUuid());
        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_ability_title").replaceAll("%p", p2.getName())), 54, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));
        int count = 0;

        for (Skill skill : SkillManager.getInstance().getSkills()) {
            menu.setItem(new ItemStack(skill.getDisplay()), skill.getSkillColor() + WordUtils.capitalizeFully(skill.getSkillType().name()), count,
                    new PrivateInventory.ClickRunnable() {
                        @Override
                        public void run(InventoryClickEvent e) {
                            p.closeInventory();
                            sendClickSound(p);
                            openAbilityPointsMenu(p, target, skill);
                        }
                    }, "&7Edit your " + skill.getSkillType().name().toLowerCase() + " abilities");

            List<Ability> abilities = AbilityManager.getAbilities(skill.getSkillType()).stream().sorted(Comparator.comparingDouble(Ability::getRequiredLevel)).collect(Collectors.toList()); //sorts abiltiy by level
            int acount = 0;
            for (Ability ability : abilities) {
                if (ability.isPassive()) continue;
                menu.setItem(new ItemStack(target.hasAbilityLevelRequirement(ability) ? (target.hasAbilityEnabled(ability) ? Material.GREEN_WOOL : Material.ORANGE_WOOL) : Material.RED_WOOL), ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), ((acount + 1) * 9) + count,
                        new PrivateInventory.ClickRunnable() {
                            @Override
                            public void run(InventoryClickEvent e) {
                                if (target.hasAbilityLevelRequirement(ability)) {
                                    target.toggleAbilityEnabled(ability);
                                    //p.closeInventory();
                                    openSkillsMenu(p, target);
                                    sendConfirmedSound(p);
                                    ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_ability_toggle").replaceAll("%a", ability.getFormattedName()).replaceAll("%s", target.hasAbilityEnabled(ability) ? FileManager.getLang().getString("settings_ability_toggle_activated") : FileManager.getLang().getString("settings_ability_toggle_deactivated")));
                                } else {
                                    sendDeniedSound(p);
                                    ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_ability_toggle_denied").replaceAll("%a", ability.getFormattedName()).replaceAll("%r", String.valueOf(ability.getRequiredLevel())));
                                }
                            }
                        }, "&fActive: " + (target.hasAbilityEnabled(ability) && target.hasAbilityLevelRequirement(ability) ? "&aTrue" : "&cFalse"), "&fRequired: " + (target.hasAbilityLevelRequirement(ability) ? "&aLevel " + ability.getRequiredLevel() : "&cLevel " + ability.getRequiredLevel()));
                acount++;
            }
            count++;
        }

        addBackButton(menu, target);
        menu.openInventory(p);

    }

    public void openAbilityPointsMenu(Player p, RPGPlayer target, Skill skill) {
        Player p2 = Bukkit.getPlayer(target.getUuid());
        int points = (int) target.getAbilityPoints(skill);
        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_points_title").replace("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replace("%n", points + "").replace("%t", (int)target.getOverallPoints(skill) + "")), 54, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));

        int count = 0;
        for (Ability ability : AbilityManager.getAbilities(skill.getSkillType())) {
            //double allocated = target.getPointAllocation(ability);
            menu.setItem(Item.createItem(ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), Material.ENCHANTED_BOOK, "", 1), count);

            int count2 = 1;
            for (AbilityAttribute attribute : ability.getAttributes()) {
                boolean max = target.getPointAllocation(ability, attribute) >= attribute.getValueMaxPoint(ability) ? true : false;
                boolean noPoints = points <= 0 ? true : false;
                boolean canLevel = !max && !noPoints;
                menu.setItem(new ItemStack(max ? Material.PURPLE_STAINED_GLASS_PANE : noPoints ? Material.YELLOW_STAINED_GLASS_PANE : Material.GREEN_STAINED_GLASS_PANE), "&2&l" + WordUtils.capitalizeFully(attribute.name().replace("_", " ")), count + (9 * count2),
                        new PrivateInventory.ClickRunnable() {
                            @Override
                            public void run(InventoryClickEvent e) {
                                if (target.attemptSetPointAllocation(ability, attribute, (int) target.getPointAllocation(ability, attribute) + 1, false)) {
                                    sendConfirmedSound(p);
                                } else {
                                    sendDeniedSound(p);
                                }
                                openAbilityPointsMenu(p, target, skill);
                            }
                        }, "&7" + attribute.getDescription() + " for " + ability.getName().toLowerCase(), " ", max ? "&e" + WordUtils.capitalizeFully(attribute.getIdentifier()) + " &7" + (attribute.getBaseValue (ability) + (attribute.getValuePerPoint(ability) * target.getPointAllocation(ability, attribute))) : "&e" + WordUtils.capitalizeFully(attribute.getIdentifier()) + " &7" + (attribute.getBaseValue (ability) + (attribute.getValuePerPoint(ability) * target.getPointAllocation(ability, attribute)) + "&e -> &7" + (attribute.getBaseValue (ability) + (target.getPointAllocation(ability, attribute) * attribute.getValuePerPoint(ability)) + attribute.getValuePerPoint(ability))), "&ePoints &7" + (int) target.getPointAllocation(ability, attribute) + "&e / &7" + (int) attribute.getValueMaxPoint(ability), " ", max ? "&cAttribute is at max level!" : noPoints ? "&cYou don't have enough points!" : "&aClick to spend 1 point!");
                count2++;
            }
            count++;
        }

        menu.setItem(new ItemStack(Material.ENDER_EYE, points < 1 ? 1 : (int) points), ChatColor.YELLOW + "Points: " + points, 49,
                new PrivateInventory.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {

                    }
                }, "&7Click on the green tiles", "&7to upgrade your abilities");
        addBackButton(menu, target);

        menu.openInventory(p);
    }

//    private static void openSkillAbilityMenu(Player p, RPGPlayer target, Skill skill) {
//        Player p2 = Bukkit.getPlayer(target.getUuid());
//        PrivateInventory menu = new PrivateInventory(ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("settings_skills_ability_title").replaceAll("%s", WordUtils.capitalizeFully(skill.getSkillType().toString())).replaceAll("%p", p2.getName())), 9, p.getUniqueId(), Item.createItem(" ", Material.GRAY_STAINED_GLASS_PANE, "", 1));
//
//        int count = 0;
//        List<Ability> abilities = AbilityManager.getAbilities(skill.getSkillType()).stream().sorted(Comparator.comparingDouble(Ability::getRequiredLevel)).collect(Collectors.toList());
//        for (Ability ability : abilities) {
//            menu.setItem(new ItemStack(target.hasAbilityLevelRequirement(ability) ? (target.hasAbilityEnabled(ability) ? Material.GREEN_WOOL : Material.ORANGE_WOOL) : Material.RED_WOOL), ChatColor.translateAlternateColorCodes('&', "&e" + ability.getFormattedName()), count,
//                    new PrivateInventory.ClickRunnable() {
//                        @Override
//                        public void run(InventoryClickEvent e) {
//                            if (target.hasAbilityLevelRequirement(ability)) {
//                                target.toggleAbilityEnabled(ability);
//                                p.closeInventory();
//                                openSkillAbilityMenu(p, target, skill);
//                                ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_skills_ability_toggle").replaceAll("%a", ability.getFormattedName()).replaceAll("%s", target.hasAbilityEnabled(ability) ? FileManager.getLang().getString("settings_skills_ability_toggle_activated") : FileManager.getLang().getString("settings_skills_ability_toggle_deactivated")));
//                            } else {
//                                ChatUtil.sendChatMessage(p, FileManager.getLang().getString("settings_skills_ability_toggle_denied").replaceAll("%a", ability.getFormattedName()).replaceAll("%r", String.valueOf(ability.getRequiredLevel())));
//                            }
//                        }
//                    }, "&fActive: " + (target.hasAbilityEnabled(ability) && target.hasAbilityLevelRequirement(ability) ? "&aTrue" : "&cFalse"), "&fRequired: " + (target.hasAbilityLevelRequirement(ability) ? "&aLevel " + ability.getRequiredLevel() : "&cLevel " + ability.getRequiredLevel()));
//            count++;
//        }
//
//        addBackButton(menu, target);
//        menu.openInventory(p);
//    }

    private void addBackButton(PrivateInventory invFrom, RPGPlayer target) {
        invFrom.setItem(new ItemStack(Material.BARRIER), "&c&lBack", invFrom.getSize() - 1,
                new PrivateInventory.ClickRunnable() {
                    @Override
                    public void run(InventoryClickEvent e) {
                        e.getWhoClicked().closeInventory();
                        openMenu((Player) e.getWhoClicked(), target);
                        sendClickSound((Player) e.getWhoClicked());
                    }
                });

    }

    private void sendDeniedSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
    }

    private void sendConfirmedSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
    }

    private void sendClickSound(Player p) {
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1f, 1f);
    }



}
