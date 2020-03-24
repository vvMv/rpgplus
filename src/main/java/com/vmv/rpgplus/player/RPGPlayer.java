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
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class RPGPlayer {
    private HashMap<SkillType, Double> exp = new HashMap<SkillType, Double>();
    private HashMap<SkillType, Ability> activeAbility;
    private HashMap<PlayerSetting, String> settings;
    private HashMap<String, Double> pointAllocations;
    private UUID uuid;

    public RPGPlayer(UUID uuid, HashMap<SkillType, Double> exp, HashMap<PlayerSetting, String> settings, HashMap<String, Double> pointAllocations) {
        this.uuid = uuid;
        this.exp = exp;
        this.settings = settings;
        this.pointAllocations = pointAllocations;
        //AbilityManager.getAbilities().forEach(ability -> ability.getAttributes().forEach(attribute -> setPointAllocation(ability, attribute, 0, false))); //set all ability and attribute values

        this.activeAbility = new HashMap<SkillType, Ability>();
    }

    public HashMap<PlayerSetting, String> getSettings() {
        return settings;
    }

    public String getSettingValue(PlayerSetting setting) {
        return settings.get(setting);
    }

    public void setSettingValue(PlayerSetting setting , String value) {
        settings.put(setting, value);

        String s = getUuid().toString() + ":" + setting.name();
        for(String data : DatabaseManager.getInstance().getSettingDataToSave()) {
            if (s.equalsIgnoreCase(data)) {
                return;
            }
        }
        DatabaseManager.getInstance().getSettingDataToSave().add(s);
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

    public double getAttributeValue(Ability ability, AbilityAttribute attribute) {
        double base = attribute.getBaseValue(ability);
        double playersAllocation = getPointAllocation(ability, attribute);
        double attributeIncrease = attribute.getValuePerPoint(ability);
        return base + (playersAllocation * attributeIncrease);
    }

    public double getAbilityPoints(Skill skill) {
        double truePoints = skill.getPointsPerLevel() * getLevel(skill.getSkillType());
        double spentPoints = 0;
        for (Ability ability : AbilityManager.getAbilities(skill.getSkillType()) ) {
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

    public double getOverallPoints(Skill skill) {
        return skill.getPointsPerLevel() * getLevel(skill.getSkillType());
    }

    public boolean attemptSetPointAllocation(Ability ability, AbilityAttribute attribute, int points,  boolean forceUnsafe) {
        Player p = Bukkit.getPlayer(getUuid());
        if (!forceUnsafe) {
            if (points > attribute.getValueMaxPoint(ability) || points < 0) {
                return false;
            }
            if (!(getAbilityPoints(SkillManager.getInstance().getSkill(ability.getSkillType())) > points - getPointAllocation(ability, attribute))) {
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

    public void toggleScoreboard() {
        if (RPGPlus.getInstance().getConfig().getBoolean("scoreboard.enabled")) {
            Player p = Bukkit.getPlayer(this.uuid);
            if (Netherboard.instance().getBoard(p) == null) {
                BPlayerBoard board = Netherboard.instance().createBoard(p, ChatColor.translateAlternateColorCodes('&', FileManager.getLang().getString("scoreboard_title")));

                int index = 0;
                for (Skill s : SkillManager.getInstance().getSkills()) {
                    int num = (int) RPGPlayerManager.getInstance().getPlayer(p).getLevel(s.getSkillType());
                    board.set(StringUtils.rightPad(s.getSkillColor() + WordUtils.capitalizeFully(s.getSkillType().toString()), 2) + " » " + ChatColor.RESET + num, index++);
                }
            } else {
                Netherboard.instance().getBoard(p).delete();
            }
        }
    }

    public boolean getSettingBoolean(PlayerSetting setting) {

        int value = Double.valueOf(getSettingValue(setting)).intValue();
        return value == 1 ? true : false;
    }

    public void toggleSetting(PlayerSetting setting) {
        setSettingValue(setting, getSettingBoolean(setting) ? "0" : "1");
    }

    public boolean hasAbilityEnabled(Ability a) {
        if (a == null) return false;
        if (PlayerSetting.valueOf(a.getName().toUpperCase()) == null) {
            InformationHandler.printMessage(InformationType.ERROR, "Missing ability setting " + a.getName().toUpperCase() + " please report this issue");
        }
        return getSettingBoolean(PlayerSetting.valueOf(a.getName().toUpperCase()));
    }

    public void toggleAbilityEnabled(Ability a) {
        if (!hasAbilityLevelRequirement(a)) return;
        setActiveAbility(a.getSkillType(), null);
        if (hasAbilityEnabled(a)) {
            setSettingValue(PlayerSetting.valueOf(a.getName().toUpperCase()), "0");
        } else {
            setSettingValue(PlayerSetting.valueOf(a.getName().toUpperCase()), "1");
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

    public void setXP(SkillType skill, double xp) {
        if (xp < 0) xp = 0;
        xp = MathUtils.round(xp, 2);
        Bukkit.getPluginManager().callEvent(new ExperienceModifyEvent(this, skill, (xp - MathUtils.round(exp.get(skill), 2))));
        if ((int) getLevel(skill) < (int) SkillManager.getInstance().getLevel(xp, skill)) Bukkit.getPluginManager().callEvent(new LevelModifyEvent(this, skill, (int) getLevel(skill), (int) SkillManager.getInstance().getLevel(xp, skill)));
        exp.put(skill, xp);

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
        try {
            if (getAbilityPoints() > 0) {

                List<String> reminder = FileManager.getLang().getStringList("points_reminder");
                StringBuilder sb = new StringBuilder();

                for (Skill skill : SkillManager.getInstance().getSkills()) {
                    if (getAbilityPoints(skill) < 1) continue;
                    sb.append(WordUtils.capitalizeFully(skill.getSkillType().name()) + " " + (int) getAbilityPoints(skill) + ", ");
                }

                sb.deleteCharAt(sb.length() - 2);

                for (String s : reminder) {
                    ChatUtil.sendCenteredChatMessage(Bukkit.getPlayer(getUuid()), s.replaceAll("%a", sb.toString()));
                }
            }
        } catch (Exception e) {}
    }

}
