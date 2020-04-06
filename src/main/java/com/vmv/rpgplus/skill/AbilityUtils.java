package com.vmv.rpgplus.skill;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;

public class AbilityUtils {

    public static HashSet<Block> collectBlocks(Block anchor, HashSet<Block> collected, int maxSize) { //Recursively find blocks
        if (collected.contains(anchor)) return collected;
        if (collected.size() > maxSize) return collected;
        collected.add(anchor);

        for (BlockFace face : BlockFace.values()) {
            if (anchor.getRelative(face).getType().equals(anchor.getType())) {
                collectBlocks(anchor.getRelative(face), collected, maxSize);
            }
        }

        return collected;
    }

}
