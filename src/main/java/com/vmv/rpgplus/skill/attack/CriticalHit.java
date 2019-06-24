package com.vmv.rpgplus.skill.attack;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.Listener;

public class CriticalHit extends Ability implements Listener {
    public CriticalHit(String name, SkillType st) {
        super(name, st);
        this.passive = true;
        this.description = "Gives chance of a critical hit";
    }
}
