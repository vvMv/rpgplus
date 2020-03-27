package com.vmv.rpgplus.skill;

import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityManager implements Listener {

    private List<Ability> abilities = new ArrayList();
    private static AbilityManager instance;

    public AbilityManager() {
        instance = this;
        RPGPlus.getInstance().registerEvents(this, new AbilityCycle());
        for (Skill skill : SkillManager.getInstance().getSkills()) {
            skill.registerAbilities();
        }
    }

    public void registerAbility(Listener... ab) {
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

    public List<Ability> getAbilities() {
        return abilities;
    }

    public List<Ability> getAbilities(SkillType st) {
        return abilities.stream().filter(a -> a.getSkillType() == st).collect(Collectors.toList());
    }

    public Ability getAbility(String name) {
        for (Ability ability : abilities) {
            if (ability.getName().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
    }

}
