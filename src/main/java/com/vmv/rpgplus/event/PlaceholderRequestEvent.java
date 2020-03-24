package com.vmv.rpgplus.event;

import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.SkillType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderRequestEvent extends PlaceholderExpansion {

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {

        for (SkillType s : SkillType.values()) {
            if (id.equalsIgnoreCase(s.toString())) {
                return String.valueOf(RPGPlayerManager.getInstance().getPlayer(p).getLevel(s));
            }
        }

        if (id.equalsIgnoreCase("total")) {
            int runningTotal = 0;
            for (SkillType s : SkillType.values()) {
                runningTotal += Math.floor(RPGPlayerManager.getInstance().getPlayer(p).getLevel(s));
            }
            return String.valueOf(runningTotal);
        }


        return id;
    }

    @Override
    public String getIdentifier() {
        return "rpgplus";
    }

    @Override
    public String getAuthor() {
        return "v_M_v";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }
}
