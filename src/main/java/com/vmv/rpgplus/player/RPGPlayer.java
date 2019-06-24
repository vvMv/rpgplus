package com.vmv.rpgplus.player;

import com.vmv.core.database.Database;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.event.ExperienceModifyEvent;
import com.vmv.rpgplus.event.LevelModifyEvent;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

public class RPGPlayer {
    private HashMap<SkillType, Double> exp;
    private HashMap<SkillType, Ability> activeAbility;
    private UUID uuid;

    public RPGPlayer(UUID uuid, HashMap<SkillType, Double> exp) {
        this.uuid = uuid;
        this.exp = new HashMap<SkillType, Double>();
        this.activeAbility = new HashMap<SkillType, Ability>();

        if (this.exp == null) {
            for (SkillType s : SkillType.values()) {
                this.exp.put(s, 0.0);
            }
        } else {
            this.exp = exp;
        }
    }

    public Ability getActiveAbility(SkillType st) {
        return activeAbility.get(st);
    }

    public double getLevel(SkillType skill) {
        return SkillManager.getInstance().getLevel(exp.get(skill), skill);
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

    public boolean hasAbilityEnabled(Ability a) {
        //TODO user ability menu
        return true;
    }

    public boolean hasAbilityRequirements(Ability a) {
        if (getLevel(a.getSkillType()) >= a.getRequiredLevel()) {
            return true;
        } else {
            return false;
        }
    }

    public void setXP(SkillType skill, double xp) {
        if (xp < 0) xp = 0;
        xp = MathUtils.round(xp, 2);
        Bukkit.getPluginManager().callEvent(new ExperienceModifyEvent(this, skill, (xp - MathUtils.round(exp.get(skill), 2))));
        if ((int) getLevel(skill) < (int) SkillManager.getInstance().getLevel(xp, skill)) Bukkit.getPluginManager().callEvent(new LevelModifyEvent(this, skill, (int) getLevel(skill), (int) SkillManager.getInstance().getLevel(xp, skill)));
        exp.put(skill, xp);
        Database.getInstance().updateData("player_experience", skill.toString().toLowerCase(), xp, "uuid", "=", uuid.toString());
    }

}
