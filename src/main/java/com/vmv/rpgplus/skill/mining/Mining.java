package com.vmv.rpgplus.skill.mining;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

import static com.vmv.rpgplus.skill.SkillType.MINING;

public class Mining extends Skill implements Listener {

    private ArrayList<String> blocks = new ArrayList<String>();

    public Mining(SkillType skillType) {
        super(skillType);
    }

    @Override
    protected void registerAbilities() {
        registerAbilities(new VeinMiner("vein_miner", MINING, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_DURATION),
                new OreLocator("ore_locator", MINING, AbilityAttribute.DECREASE_COOLDOWN, AbilityAttribute.INCREASE_DURATION, AbilityAttribute.INCREASE_RANGE));
    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {

        if (e.isCancelled()) return;
        if (!super.hasBuildPermission(e.getBlock().getLocation(), e.getPlayer()) || !super.hasMaterial(e.getPlayer())) return;

        if (!blocks.contains(e.getBlock().toString())) {
            for (String b : getConfig().getConfigurationSection("experience").getKeys(false)) {
                if (e.getBlock().getType() == XMaterial.valueOf(b).parseMaterial()) {
                    double xp = MathUtils.getRandom(getConfig().getDouble("experience." + b + ".max"), getConfig().getDouble("experience." + b + ".min"));
                    RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).addXP(MINING, xp);
                }
            }
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        blocks.add(e.getBlock().toString());
    }


}
