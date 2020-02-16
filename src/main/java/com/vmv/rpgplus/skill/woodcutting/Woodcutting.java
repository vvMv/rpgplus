package com.vmv.rpgplus.skill.woodcutting;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.DependencyManager;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.Arrays;

import static com.vmv.rpgplus.skill.SkillType.WOODCUTTING;

public class Woodcutting extends Skill implements Listener {

    ArrayList<String> logs = new ArrayList<String>();

    public Woodcutting(SkillType skillType) {
        super(skillType);
    }

    @Override
    protected void registerAbilities() {
        registerAbilities(new TreeFeller("tree_feller", WOODCUTTING));
    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {

        if (!super.hasBuildPermission(e.getBlock().getLocation(), e.getPlayer()) || !super.hasMaterial(e.getPlayer())) return;

        if (!logs.contains(e.getBlock().toString())) {
            for (String b : getConfig().getConfigurationSection("experience").getKeys(false)) {
                if (e.getBlock().getType() == Material.valueOf(b)) {
                    double xp = MathUtils.getRandom(getConfig().getDouble("experience." + b + ".max"), getConfig().getDouble("experience." + b + ".min"));
                    RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).addXP(WOODCUTTING, xp);
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        logs.add(e.getBlock().toString());
    }


}
