package com.vmv.rpgplus.skill.mining;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VeinMiner extends Ability implements Listener {

    public List<Material> blocks = new ArrayList<>();
    public static List<Block> checkedBlocks;
    public int maxSize, delay;

    public VeinMiner(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        maxSize = getAbilityConfigSection().getInt("maxsize");
        delay = getAbilityConfigSection().getInt("delay");
        try {
            getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(Material.valueOf(b)));
        } catch (IllegalArgumentException e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.vein_miner.blocks", e.getMessage(), "This error is coming from mining.yml" );
        }
    }

    @EventHandler
    public void blockBreakEvent(BlockBreakEvent e) {

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) setActive(e.getPlayer(), getDuration(e.getPlayer()));
        if (!isActive(e.getPlayer())) return;

        if (!blocks.contains(e.getBlock().getType())) return;

        checkedBlocks = new ArrayList<Block>();
        HashSet<Block> vein = findBlocks(e.getBlock(), new HashSet<Block>());
        int delay = 5;
        for (Block block : vein) {
            Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    block.getLocation().getWorld().playSound(block.getLocation(), Sound.BLOCK_STONE_BREAK, 1.0F, 1.0F);
                    block.breakNaturally();
                }
            }, (long) delay);
            delay+= this.delay;
        }

    }

    public HashSet<Block> findBlocks(Block anchor, HashSet<Block> collected) { //Recursively find ores in vein
        if (collected.contains(anchor)) return collected;
        if (collected.size() > maxSize) return collected;
        collected.add(anchor);

        for (BlockFace face : BlockFace.values()) {
            if (anchor.getRelative(face).getType().equals(anchor.getType())) {
                findBlocks(anchor.getRelative(face), collected);
            }
        }

        checkedBlocks.addAll(collected);
        return collected;
    }

}
