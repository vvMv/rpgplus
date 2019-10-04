package com.vmv.rpgplus.skill.stamina;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.Listener;

public class Stealth extends Ability implements Listener {

    public Stealth(String name, SkillType st) {
        super(name, st);
    }

}
