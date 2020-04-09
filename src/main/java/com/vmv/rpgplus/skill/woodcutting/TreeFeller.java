package com.vmv.rpgplus.skill.woodcutting;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityUtils;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TreeFeller extends Ability implements Listener {

    public static List<Material> leaves = new ArrayList<>();
    public static List<Material> logs = new ArrayList<>();

    public List<Block> checkedBlocks = new ArrayList<Block>();

    private int maxsize;
    private int delay;

    public TreeFeller(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        delay = getAbilityConfigSection().getInt("delay");
        maxsize = getAbilityConfigSection().getInt("maxsize");
        try { getAbilityConfigSection().getStringList("logs").forEach(b -> logs.add(XMaterial.valueOf(b).parseMaterial())); } catch (IllegalArgumentException e) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.tree_feller.logs", e.getMessage(), "This error is coming from woodcutting.yml" ); }
        try { getAbilityConfigSection().getStringList("leaves").forEach(b -> leaves.add(XMaterial.valueOf(b).parseMaterial())); } catch (IllegalArgumentException e) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.tree_feller.leaves", e.getMessage(), "This error is coming from woodcutting.yml" ); }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChop(BlockBreakEvent e) {

        if (e.isCancelled()) return;
        if (!logs.contains(e.getBlock().getType())) return;
        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) setActive(e.getPlayer(), getDuration(e.getPlayer()));
        if (!isActive(e.getPlayer())) return;
        if (checkedBlocks.contains(e.getBlock())) return;

        int time = delay;
        HashSet<Block> logVein = AbilityUtils.collectBlocks(e.getBlock(), new HashSet<Block>(), maxsize);

        checkedBlocks.addAll(logVein);
        for (Block block : logVein) {
            if (time + delay > getActive().get(e.getPlayer()) * 20) return;
            Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), () -> {
                Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(block, e.getPlayer()));
            }, time += this.delay);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        if (!checkedBlocks.contains(e.getBlock())) return;
        if (e.isCancelled()) return;
        checkedBlocks.remove(e.getBlock());
        block.getLocation().getWorld().playSound(block.getLocation(), XSound.BLOCK_STONE_BREAK.parseSound(), 1.0F, 1.0F);
        Location center = block.getLocation().add(0.5, 0.5, 0.5);
        for (ItemStack stack : block.getDrops(e.getPlayer().getInventory().getItemInMainHand())) {
            block.getWorld().dropItem(center, stack);
        }

        block.setType(XMaterial.AIR.parseMaterial());
    }

}


