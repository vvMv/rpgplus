package com.vmv.rpgplus.skill;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Skill implements Listener {

    private boolean enabled;
    private SkillType skillType;
    private int maxLevel = 100;
    private double pointsPerLevel = 0.5;
    private List<Material> materials = new ArrayList<>();
    private XMaterial display = XMaterial.BARRIER;

    public Skill(SkillType skillType) {
        this.skillType = skillType;
        reload();
    }

    public void reload() {
        FileManager.getSkillFile(getSkillType()).reload();
        this.maxLevel = getConfig().getInt("max_level");
        this.pointsPerLevel = getConfig().getDouble("points_per_level");
        this.enabled = getConfig().getBoolean("enabled");
        try {
            getConfig().getStringList("materials").forEach(m -> materials.add(XMaterial.valueOf(m).parseMaterial()));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid materials in " + skillType.toString().toLowerCase() + ".yml");
        }
        try {
            display = XMaterial.valueOf(getConfig().getString("display"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid display material in " + skillType.toString().toLowerCase() + ".yml");
        }
    }

    protected abstract void registerAbilities();

    protected abstract void registerEvents();

    public FileConfiguration getConfig() {
        return FileManager.getSkillFile(skillType);
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public String getFormattedName() {
        return ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("skill." + this.getSkillType().toString().toLowerCase()));
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public XMaterial getDisplay() {
        return display;
    }

    public double getPointsPerLevel() {
        return pointsPerLevel;
    }

    public AbilityCycleType getCycleType() {
        try {
            return AbilityCycleType.valueOf(getConfig().getString("ability_cycle"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "ability_cycle in " + skillType.toString().toLowerCase() + ".yml is invalid");
        }
        return null;
    }

    public ChatColor getSkillColor() {
        try {
            return ChatColor.valueOf(getConfig().getString("skill_color"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.WARN, "Invalid configuration value skill_color at " + getSkillType().name().toLowerCase() + ".yml");
        }
        return ChatColor.WHITE;
    }

    public void registerEvents(Listener... listeners) {
        RPGPlus.getInstance().registerEvents(listeners);
    }

    public void registerAbilities(Listener... listeners) {
        AbilityManager.getInstance().registerAbility(listeners);
    }

    public boolean hasBuildPermission(Location blockLocation, Player player) {
        return true;
        //TODO FIX return (DependencyManager.getInstance().testWorldGuardFlag(blockLocation, player, Flags.BUILD));

    }

    public boolean hasMaterial(Player player) {
        if (getConfig().getBoolean("exp_requires_material")) {
            return Arrays.stream(SkillManager.getInstance().getSkill(getSkillType()).getMaterials().toArray()).anyMatch(m -> m == player.getInventory().getItemInMainHand().getType());
        }
        return true;
    }

    public boolean hasDamagePermission(Player player) {
        return true;
        //return !DependencyManager.getInstance().testWorldGuardFlag(player.getLocation(), player, Flags.PVP);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
