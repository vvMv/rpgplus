package com.vmv.rpgplus.skill;

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
        registerAbility(new MultiArrow("multi_arrow", ARCHERY),
                new ExplosiveArrow("explosive_arrow", ARCHERY),
                new TeleportArrow("teleport_arrow", ARCHERY),
                new SplitShot("split_shot", ARCHERY),
                new Dash("dash", STAMINA),
                new VeinMiner("vein_miner", MINING),
                new TreeFeller("tree_feller", WOODCUTTING),
                new Track("track", ATTACK),
                new OreLocator("ore_locator", MINING));
    }

    public void registerAbility(Listener... ab) {
        for (Listener a : ab) {
            RPGPlus.getInstance().registerEvents(a);
            abilities.add((Ability) a);
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

}
