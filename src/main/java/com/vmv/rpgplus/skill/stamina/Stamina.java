package com.vmv.rpgplus.skill.stamina;

import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class Stamina extends Skill {
    public Stamina(SkillType skillType) {
        super(skillType);
    }

    @EventHandler
    public void onSprintToggle(PlayerToggleSprintEvent e) {

    }

}
