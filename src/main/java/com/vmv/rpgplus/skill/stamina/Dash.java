package com.vmv.rpgplus.skill.stamina;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class Dash extends Ability implements Listener {
    public Dash(String name, SkillType st) {
        super(name, st);
    }

    @EventHandler
    public void ShiftToggle(PlayerToggleSprintEvent e) {


    }

}
