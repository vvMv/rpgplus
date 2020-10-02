package com.vmv.rpgplus.skill.excavation;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Excavate extends Ability implements Listener {

    public List<Material> blocks = new ArrayList<>();
    public List<Block> checkedBlocks = new ArrayList<Block>();
    public Map<Player, BlockFace> blockFaceMap = new HashMap<>();

    public Excavate(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        try {
            getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(XMaterial.valueOf(b).parseMaterial()));
        } catch (IllegalArgumentException e) {
            InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.excavate.blocks", e.getMessage(), "This error is coming from excavation.yml" );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreakEvent(BlockBreakEvent e) {
        BlockFace blockFace = blockFaceMap.get(e.getPlayer());
        if (e.isCancelled()) return;
        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) setActive(e.getPlayer(), getDuration(e.getPlayer()));
        if (!isActive(e.getPlayer())) return;
        if (!blocks.contains(e.getBlock().getType())) return;

        Location l = e.getBlock().getLocation();
        if (blockFace == BlockFace.UP || blockFace == BlockFace.DOWN) excavateBlocks(l.getBlockX() - 1, l.getBlockX() + 1, l.getBlockY(), l.getBlockY(), l.getBlockZ() - 1, l.getBlockZ() + 1, e);
        if (blockFace == BlockFace.EAST || blockFace == BlockFace.WEST) excavateBlocks(l.getBlockX(), l.getBlockX(), l.getBlockY() - 1, l.getBlockY() + 1, l.getBlockZ() - 1, l.getBlockZ() + 1, e);
        if (blockFace == BlockFace.NORTH || blockFace == BlockFace.SOUTH) excavateBlocks(l.getBlockX() - 1, l.getBlockX() + 1, l.getBlockY() - 1, l.getBlockY() + 1, l.getBlockZ(), l.getBlockZ(), e);

    }

    public void excavateBlocks(int x1, int x2, int y1, int y2, int z1, int z2, BlockBreakEvent e) {
        for (int x = x1; x <= x2 ; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    if (!blocks.contains(e.getPlayer().getWorld().getBlockAt(x, y, z).getType())) continue;
                    e.getPlayer().getWorld().getBlockAt(x, y, z).breakNaturally(e.getPlayer().getInventory().getItemInMainHand());
                }
            }
        }
    }

    @EventHandler
    public void blockFaceClicked(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        blockFaceMap.put(e.getPlayer(), e.getBlockFace());
    }

}
