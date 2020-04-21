package com.vmv.rpgplus.skill.farming;

import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class AutoReplanter extends Ability {

    public AutoReplanter(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
    }

    @EventHandler
    public void replantEvent(BlockPlaceEvent e) {

    }

}

