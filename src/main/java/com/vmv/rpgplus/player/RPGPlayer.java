package com.vmv.rpgplus.player;

import com.vmv.core.config.FileManager;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.database.DatabaseManager;
import com.vmv.rpgplus.database.PlayerSetting;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.event.PointModifyEvent;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.*;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RPGPlayer {
    private HashMap<SkillType, Double> exp = new HashMap<SkillType, Double>();
    private HashMap<SkillType, Ability> activeAbility;
    private HashMap<PlayerSetting, Boolean> settings;
    private HashMap<String, Double> pointAllocations;
    private UUID uuid;

    public RPGPlayer(UUID uuid, HashMap<SkillType, Double> exp, HashMap<PlayerSetting, Boolean> settings, HashMap<String, Double> pointAllocations) {
        this.uuid = uuid;
        this.exp = exp;
        this.settings = settings;
        this.pointAllocations = pointAllocations;
        this.activeAbility = new HashMap<SkillType, Ability>();
    }

    public HashMap<PlayerSetting, Boolean> getSettings() {
        return settings;
    }

    public Boolean getSettingValue(PlayerSetting setting) {
        for (Boolean value : settings.values()) {
            InformationHandler.printMessage(InformationType.DEBUG, setting.name() + " is " + settings.get(setting));
        }
        return settings.get(setting);
    }

    public void setSettingValue(PlayerSetting setting , Boolean value) {
        settings.put(setting, value);

        String s = getUuid().toString() + ":" + setting.name();
        for(String data : DatabaseManager.getInstance().getSettingDataToSave()) {
            if (s.equalsIgnoreCase(data)) {
                return;
            }
        }
        DatabaseManager.getInstance().getSettingDataToSave().add(s);
    }

    public boolean getSettingBoolean(PlayerSetting setting) {

        return Boolean.valueOf(getSettingValue(setting));
//        int value = Double.valueOf(getSettingValue(setting)).intValue();
//        return value == 1 ? true : false;
    }

    public void toggleSetting(PlayerSetting setting) {
        setSettingValue(setting, getSettingBoolean(setting) ? false : true);
    }

    public Ability getActiveAbility(SkillType st) {
        return activeAbility.get(st);
    }

    public double getLevel(SkillType skill) {
        return SkillManager.getInstance().getLevel(exp.get(skill), skill);
    }

    public int getTotalLevel() {
        int c = 0;
        for (Skill skill : SkillManager.getInstance().getSkills()) c += (int) getLevel(skill.getSkillType());
        return c;
    }

    public int getTotalExperience() {
        int c = 0;
        for (Skill skill : SkillManager.getInstance().getSkills()) c += (int) getExperience(skill.getSkillType());
        return c;
    }

    public double getExperience(SkillType skill) {
        return exp.get(skill);
    }

    public void setActiveAbility(SkillType st, Ability a) {
        activeAbility.put(st, a);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addXP(SkillType skill, double xp) {
        setXP(skill, exp.get(skill) + xp);
    }

    public void removeXP(SkillType skill, double xp) {
        setXP(skill, exp.get(skill) - xp);
    }

    public void resetXP(SkillType skill) {
        setXP(skill, 0);
    }

    /**
     * @param ability test
     * @param attribute from enum list
     * @return May be null if the ability doesn't have specified attribute
     */
    public double getAttributeValue(Ability ability, AbilityAttribute attribute) {
        double base = attribute.getBaseValue(ability);
        double playersAllocation = getPointAllocation(ability, attribute);
        double attributeIncrease = attribute.getValuePerPoint(ability);
        return base + (playersAllocation * attributeIncrease);
    }

    public double getAbilityPoints(Skill skill) {
        double truePoints = skill.getPointsPerLevel() * getLevel(skill.getSkillType());
        double spentPoints = 0;
        for (Ability ability : AbilityManager.getInstance().getAbilities(skill.getSkillType()) ) {
            for (AbilityAttribute attribute : ability.getAttributes()) {
                spentPoints += getPointAllocation(ability, attribute);
            }
        }
        return truePoints - spentPoints;
    }

    public double getAbilityPoints() {
        int count = 0;
        for (Skill skill : SkillManager.getInstance().getSkills()) {
            count += getAbilityPoints(skill);
        }
        return count;
    }

    public double getPointAllocation(Ability ability, AbilityAttribute attribute) {
        String key = ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
        if (pointAllocations.containsKey(key)) {
            return pointAllocations.get(key);
        }
        return 0;
    }

    public double getOverallPoints() {
        int c = 0;
        for (Skill skill : SkillManager.getInstance().getSkills()) c += (int) getOverallPoints(skill);
        return c;
    }

    public double getOverallPoints(Skill skill) {
        return skill.getPointsPerLevel() * getLevel(skill.getSkillType());
    }

    public boolean attemptSetPointAllocation(Ability ability, AbilityAttribute attribute, int points,  boolean forceUnsafe) {
        if (!forceUnsafe) {
            if (points > attribute.getValueMaxPoint(ability) || points < 0) { //check when setting with command
                return false;
            }
            if (getAbilityPoints(SkillManager.getInstance().getSkill(ability.getSkillType())) < points - getPointAllocation(ability, attribute)) {
                return false;
            }
        }
        setPointAllocation(ability, attribute, points, true);
        return true;
    }

    private void setPointAllocation(Ability ability, AbilityAttribute attribute, double value, boolean callEvent) {
        pointAllocations.put(ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase(), value);
        if (callEvent) { Bukkit.getPluginManager().callEvent(new PointModifyEvent(this, ability)); }

        //Checks if the point data already queued to save
        String s = getUuid().toString() + ":" + ability.getName().toLowerCase() + ":" + attribute.name().toLowerCase();
        for(String data : DatabaseManager.getInstance().getPointDataToSave()) {
            if (s.equalsIgnoreCase(data)) {
                return;
            }
        }
        DatabaseManager.getInstance().getPointDataToSave().add(s);

    }

    public boolean hasAbilityEnabled(Ability a) {
        if (a == null) return false;
        if (PlayerSetting.valueOf(a.getName().toUpperCase()) == null) {
            InformationHandler.printMessage(InformationType.ERROR, "Missing ability setting " + a.getName().toUpperCase() + " please report this issue");
        }
        return getSettingBoolean(PlayerSetting.valueOf(a.getName().toUpperCase()));
    }

    public void toggleAbilityEnabled(Ability a) {
        toggleAbilityEnabled(a, null);
    }

    public void toggleAbilityEnabled(Ability a, Boolean setTo) {
        if (!hasAbilityLevelRequirement(a)) return;
        setActiveAbility(a.getSkillType(), null);
        if (setTo != null) {
            setSettingValue(PlayerSetting.valueOf(a.getName().toUpperCase()), setTo.booleanValue() ? true : false);
            return;
        }
        if (hasAbilityEnabled(a)) {
            setSettingValue(PlayerSetting.valueOf(a.getName().toUpperCase()), false);
        } else {
            setSettingValue(PlayerSetting.valueOf(a.getName().toUpperCase()), true);
        }

        //Checks if the setting and player are already queued to save
        String s = getUuid().toString() + ":" + a.getName();
        for(String data : DatabaseManager.getInstance().getSettingDataToSave()) {
            if (s.equalsIgnoreCase(data)) {
                return;
            }
        }

        DatabaseManager.getInstance().getSettingDataToSave().add(s);
    }

    public boolean hasAbilityLevelRequirement(Ability a) {
        if (a == null) return false;
        return getLevel(a.getSkillType()) >= a.getRequiredLevel() ? true : false;
    }

    public void setXP(SkillType skill, double amount) {

        if (Bukkit.getPlayer(getUuid()) != null) {
            Player p = Bukkit.getPlayer(getUuid());
            if (RPGPlus.getInstance().getConfig().getStringList("general.experience_blacklist_world").contains(p.getWorld().getName())) return;
            if (RPGPlus.getInstance().getConfig().getStringList("general.experience_blacklist_gamemode").stream().anyMatch(p.getGameMode().toString()::equalsIgnoreCase)) return;
        }

        double xp = amount;
        if (xp < 0) xp = 0;
        xp = MathUtils.round(xp, 2);
        double currentxp = MathUtils.round(exp.get(skill), 2);
        if ((int) getLevel(skill) < (int) SkillManager.getInstance().getLevel(xp, skill)) Bukkit.getPluginManager().callEvent(new LevelModifyEvent(this, skill, (int) getLevel(skill), (int) SkillManager.getInstance().getLevel(xp, skill)));
        exp.put(skill, xp);

        Bukkit.getPluginManager().callEvent(new ExperienceModifyEvent(this, skill, (xp - currentxp)));

        //Checks if the exp skill type and player are already queued to save
        String s = getUuid().toString() + ":" + skill.toString();
        for(String data : DatabaseManager.getInstance().getExpDataToSave()) {
            if (s.equalsIgnoreCase(data)) {
                return;
            }
        }
        DatabaseManager.getInstance().getExpDataToSave().add(s);


    }

    public void sendAbilityPointReminder() {
        if (getAbilityPoints() > 0 && getSettingBoolean(PlayerSetting.REMINDER_MESSAGES)) {

            List<String> reminder = FileManager.getLang().getStringList("points_reminder");
            StringBuilder sb = new StringBuilder();

            for (Skill skill : SkillManager.getInstance().getSkills()) {

                int spent = 0; //counting how many spent on skill to prevent reminder being sent if no points can be spent
                int max = 0;

                for (Ability ab : AbilityManager.getInstance().getAbilities(skill.getSkillType())) {
                    for (AbilityAttribute at : ab.getAttributes()) {
                        spent += getPointAllocation(ab, at);
                        max += at.getValueMaxPoint(ab);
                    }
                }

                if (spent == max) continue;
                if (getAbilityPoints(skill) < 1) continue;
                sb.append(WordUtils.capitalizeFully(skill.getFormattedName()) + " " + (int) getAbilityPoints(skill) + ", ");
            }

            if (sb.length() >= 2) sb.deleteCharAt(sb.length() - 2);

            for (String s : reminder) {
                ChatUtil.sendCenteredChatMessage(Bukkit.getPlayer(getUuid()), s.replaceAll("%a", sb.toString()));
            }
        }
    }

}
