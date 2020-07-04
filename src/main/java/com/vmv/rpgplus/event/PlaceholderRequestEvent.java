package com.vmv.rpgplus.event;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.*;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceholderRequestEvent extends PlaceholderExpansion {

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String id) {

        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(p);

        //returns the level in specified skill format %rpgplus_level_{skill}%
        //old method also available format %rpgplus_{skill}%
        for (SkillType s : SkillType.values()) {
            if (id.equalsIgnoreCase(s.toString()) || id.equalsIgnoreCase("level_" + s.toString())) {
                return String.valueOf(rp.getLevel(s));
            }
        }

        //returns the total experience in specified skill format %rpgplus_experience_{skill}%
        for (SkillType s : SkillType.values()) {
            if (id.equalsIgnoreCase("experience_" + s.toString())) {
                return String.valueOf(rp.getExperience(s));
            }
        }

        //returns the total level of all skills combined format %rpgplus_level_total%
        //old method also available format %rpgplus_total%
        if (id.equalsIgnoreCase("total") || id.equalsIgnoreCase("level_total")) {
            return String.valueOf(rp.getTotalLevel());
        }

        //returns the total experience of all skills combined format %rpgplus_experience_total%
        if (id.equalsIgnoreCase("experience_total")) {
            return String.valueOf(rp.getTotalExperience());
        }

        //returns the amount of points allocated to specific ability attribute format %rpgplus_points_{ability}_{attribute}%
        for (Ability ability : AbilityManager.getInstance().getAbilities()) {
            for (AbilityAttribute attribute : ability.getAttributes()) {
                if (id.equalsIgnoreCase("points_" + ability.getName() + "_" + attribute.name())) {
                    return String.valueOf(rp.getPointAllocation(ability, attribute));
                }
            }
        }

        //returns the amount of available points in specified skill format %rpgplus_points_available_{skill}%
        for (SkillType s : SkillType.values()) {
            if (id.equalsIgnoreCase("points_available_" + s)) {
                return String.valueOf(rp.getAbilityPoints(SkillManager.getInstance().getSkill(s)));
            }
        }

        //returns the amount of total points in specified skill format %rpgplus_points_total_{skill}%
        for (SkillType s : SkillType.values()) {
            if (id.equalsIgnoreCase("points_total_" + s)) {
                return String.valueOf(rp.getOverallPoints(SkillManager.getInstance().getSkill(s)));
            }
        }

        //returns a players total ability points available to spend format %rpgplus_points_available%
        if (id.equalsIgnoreCase("points_available")) {
            return String.valueOf(rp.getAbilityPoints());
        }

        //returns a players overall points including points spent format %rpgplus_points_total%
        if (id.equalsIgnoreCase("points_total")) {
            return String.valueOf(rp.getOverallPoints());
        }

        //returns the value of an attribute format %rpgplus_attribute_value_{ability}_{attribute}%
        for (Ability ability : AbilityManager.getInstance().getAbilities()) {
            for (AbilityAttribute attribute : ability.getAttributes()) {
                if (id.equalsIgnoreCase("attribute_value_" + ability.getName() + "_" + attribute.name())) {
                    return String.valueOf(rp.getAttributeValue(ability, attribute));
                }
            }
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
        return RPGPlus.getInstance().getDescription().getVersion();
    }
}
