package com.vmv.rpgplus.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.entity.Player;

@CommandAlias("rpg")
public class Commands extends BaseCommand {

    @Default
    public void onDefault(Player p, int level) {
        RPGPlayerManager.getInstance().getPlayer(p.getUniqueId()).setXP(SkillType.ARCHERY, SkillManager.getInstance().getExperience(level));
        //RPGPlayerManager.getInstance().getPlayers().forEach(rp -> p.sendMessage(rp.getUuid().toString()));
        //SkillManager.getSkills().forEach(s -> p.sendMessage(s.getSkillType().toString() + s.getMaxLevel()));
    }

    @Subcommand("stats")
    public void displayStats(Player p) {
        for (SkillType st : SkillType.values()) {
            p.sendMessage(st.toString() + " " + RPGPlayerManager.getInstance().getPlayer(p).getLevel(st));
        }
    }

}
