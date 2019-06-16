package com.vmv.rpgplus.skill;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public abstract class Skill {

    private SkillType skillType;
    private ChatColor expDropColor;
    private int maxLevel = 100;

    public Skill(SkillType skillType) {
        this.skillType = skillType;
        this.expDropColor = getExpDropColor();
        maxLevel = RPGPlus.getInstance().getConfig().getInt("maxLevel");
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

    public ChatColor getExpDropColor() {
        try {
            return expDropColor = ChatColor.valueOf(getConfig().getString("expDropColor"));
        } catch (Exception e) {
            InformationHandler.printMessage(InformationType.WARN, "Invalid configuration value expDropColor at " + getSkillType().name().toLowerCase() + ".yml");
            //new InformationHandler(RPGPlus.getInstance(), InformationType.WARN, "Invalid configuration value expDropColor at " + getSkillType().name().toLowerCase() + ".yml");
        }
        return ChatColor.WHITE;
    }

    public void registerEvents(Listener... listeners) {
        RPGPlus.getInstance().registerEvents(listeners);
    }


}
