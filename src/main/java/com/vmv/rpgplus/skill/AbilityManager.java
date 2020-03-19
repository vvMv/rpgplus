package com.vmv.rpgplus.skill;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.archery.ExplosiveArrow;
import com.vmv.rpgplus.skill.archery.MultiArrow;
import com.vmv.rpgplus.skill.archery.SplitShot;
import com.vmv.rpgplus.skill.archery.TeleportArrow;
import com.vmv.rpgplus.skill.attack.Track;
import com.vmv.rpgplus.skill.mining.OreLocator;
import com.vmv.rpgplus.skill.mining.VeinMiner;
import com.vmv.rpgplus.skill.stamina.Dash;
import com.vmv.rpgplus.skill.woodcutting.TreeFeller;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.vmv.rpgplus.skill.SkillType.*;

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
            if (((Ability) a).getSkillConfig().getBoolean("enabled")) {
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
