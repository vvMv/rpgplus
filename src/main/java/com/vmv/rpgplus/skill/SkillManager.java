package com.vmv.rpgplus.skill;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.archery.Archery;
import com.vmv.rpgplus.skill.attack.Attack;
import com.vmv.rpgplus.skill.farming.Farming;
import com.vmv.rpgplus.skill.fishing.Fishing;
import com.vmv.rpgplus.skill.mining.Mining;
import com.vmv.rpgplus.skill.stamina.Stamina;
import com.vmv.rpgplus.skill.woodcutting.Woodcutting;

import java.util.ArrayList;
import java.util.List;

public class SkillManager {

    public static List<Skill> skills = new ArrayList();
    private static SkillManager instance;

    public SkillManager() {
        instance = this;
        skills.add(new Archery(SkillType.ARCHERY));
        skills.add(new Attack(SkillType.ATTACK));
        skills.add(new Fishing(SkillType.FISHING));
        skills.add(new Farming(SkillType.FARMING));
        skills.add(new Mining(SkillType.MINING));
        skills.add(new Stamina(SkillType.STAMINA));
        skills.add(new Woodcutting(SkillType.WOODCUTTING));
    }

    public static SkillManager getInstance() {
        return instance;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public Skill getSkill(SkillType s) {
        for (Skill skill : skills) {
            if (skill.getSkillType() == s) return skill;
        }
        return null;
    }

    public double getExperience(double level) {
        double modifier = RPGPlus.getInstance().getConfig().getDouble("General.ExperienceGrowth");
        return MathUtils.round(((level * 50) * (level * modifier) / 3), 2);
    }

    public double getLevel(double exp) {
        int maxLevel = RPGPlus.getInstance().getConfig().getInt("General.MaxLevel");
        for (int level = 0; level <= maxLevel; level++) {
            if (getExperience(level) > exp) {
                double decimal = MathUtils.round((exp - getExperience(level - 1)) / (getExperience(level) - getExperience(level - 1)), 2);
                return (level - 1) + decimal;
            }
        }
        return maxLevel;
    }
}
