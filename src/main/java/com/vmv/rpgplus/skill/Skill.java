package com.vmv.rpgplus.skill;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public abstract class Skill {

    private SkillType skillType;
    private ChatColor expDropColor;
    private int maxLevel = 100;
    private List<Material> materials = new ArrayList<>();
    private AbilityCycleType cycleType;

    public Skill(SkillType skillType) {
        this.skillType = skillType;
        this.expDropColor = getExpDropColor();
        this.cycleType = getCycleType();
        this.maxLevel = getConfig().getInt("maxLevel");
        try {
            getConfig().getStringList("materials").forEach(m -> materials.add(Material.valueOf(m)));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid materials in " + skillType.toString().toLowerCase() + ".yml");
        }
    }

    public FileConfiguration getConfig() {
        return FileManager.getSkillFile(skillType);
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public AbilityCycleType getCycleType() {
        try {
            return AbilityCycleType.valueOf(getConfig().getString("abilityCycle"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.ERROR, "abilityCycle in " + skillType.toString().toLowerCase() + ".yml is invalid");
        }
        return null;
    }

    public ChatColor getExpDropColor() {
        try {
            return ChatColor.valueOf(getConfig().getString("expDropColor"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.WARN, "Invalid configuration value expDropColor at " + getSkillType().name().toLowerCase() + ".yml");
        }
        return ChatColor.WHITE;
    }

    public void registerEvents(Listener... listeners) {
        RPGPlus.getInstance().registerEvents(listeners);
    }


}
