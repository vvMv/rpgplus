package com.vmv.rpgplus.skill;

import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityManager implements Listener {

    public static List<Ability> abilities = new ArrayList();
    private static AbilityManager instance;

    public AbilityManager() {
        instance = this;
        RPGPlus.getInstance().registerEvents(this, new AbilityCycle());
        for (Skill skill : SkillManager.getInstance().getSkills()) {
            skill.registerAbilities();
        }

        //abilities.removeIf(ability -> !ability.getSkillConfig().getBoolean("enabled"));
    }

    public static void registerAbility(Listener... ab) {
        for (Listener a : ab) {
            if (((Ability) a).isEnabled()) {
                RPGPlus.getInstance().registerEvents(a);
                abilities.add((Ability) a);
            }
        }
    }

    public static AbilityManager getInstance() {
return instance;
    }

    public static List<Ability> getAbilities() {
        return abilities;
    }

    public static List<Ability> getAbilities(SkillType st) {
        return abilities.stream().filter(a -> a.getSkillType() == st).collect(Collectors.toList());
    }

    public static Ability getAbility(String name) {
        for (Ability ability : abilities) {
            if (ability.getName().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
    }

}
