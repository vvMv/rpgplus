package com.vmv.rpgplus.skill.mining;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class OreLocator extends Ability implements Listener {

    public static List<Material> blocks = new ArrayList<>();
    public static List<Block> checkedBlocks;
    public static List<Location> highlighted = new ArrayList<>();
    public static List<Slime> slimes = new ArrayList<>();

    public OreLocator(String name, SkillType st) {
        super(name, st);
        try { getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(Material.valueOf(b))); } catch (IllegalArgumentException ex) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.ore_locator.blocks", ex.getMessage(), "This error is coming from mining.yml" ); }
    }

    @EventHandler
    public void onLocatorMine(BlockBreakEvent e) {

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) {
            setActive(e.getPlayer(), getDuration());
            locate(e.getPlayer());
        }
    }

    @EventHandler
    public void mineBlock(BlockBreakEvent e) {
        for (Slime slime : slimes) {
            if (slime.getLocation().getBlock().getLocation().equals(e.getBlock().getLocation())) {
                slimes.remove(slime);
                highlighted.remove(e.getBlock().getLocation());
                slime.remove();
                return;
            }
        }
    }

    public void locate(Player player) {

        ArrayList<HashSet<Block>> veins = getOreVeins(player.getLocation().getBlock(), 15);

        for (HashSet<Block> vein : veins) {

            List<Block> list = new ArrayList<Block>(vein);
            Block block = list.get(0);

            Location l = block.getLocation().add(0.5, 0.3, 0.5);

            if (highlighted.contains(l)) continue;

            Slime sh = (Slime) player.getLocation().getWorld().spawnEntity(l, EntityType.SLIME);
            //sh.setWander(false);
            sh.setSize(1);
            sh.setGlowing(true);
            sh.setAI(false);
            sh.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 10));
            sh.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 100));
            sh.setCollidable(false);
            sh.setGravity(false);
            sh.setInvulnerable(true);
            sh.setHealth(1);
            sh.setLootTable(null);
            sh.setSilent(true);
            slimes.add(sh);
            highlighted.add(l);

            Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sh.remove();
                    slimes.remove(sh);
                    highlighted.remove(l);
                }
            }, (long) (getDuration() * 20));


        }
    }

    public ArrayList<HashSet<Block>> getOreVeins(Block start, int radius){

        checkedBlocks = new ArrayList<>();
        if (radius < 0) {
            return new ArrayList<HashSet<Block>>(0);
        }

        int iterations = (radius * 2) + 1;
        List<Block> allblocks = new ArrayList<Block>(iterations * iterations * iterations);

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y+= MathUtils.getRandom(2,1)) {
                for (int z = -radius; z <= radius; z+= MathUtils.getRandom(2,1)) { //TODO Looping may be optimised
                    allblocks.add(start.getRelative(x, y, z));
                }
            }
        }

        ArrayList<HashSet<Block>> veins = new ArrayList<HashSet<Block>>();

        for (Block block: allblocks) {
            if (checkedBlocks.contains(block)) continue;
            if (blocks.contains(block.getType())) {
                HashSet<Block> vein = collectBlocks(block, new HashSet<Block>());
                veins.add(vein);
            }
        }
        return veins;
    }

    public HashSet<Block> collectBlocks(Block anchor, HashSet<Block> collected) { //Recursively find ores in vein
        if (collected.contains(anchor)) return collected;
        collected.add(anchor);

        for (BlockFace face : BlockFace.values()) {
            if (anchor.getRelative(face).getType().equals(anchor.getType())) {
                collectBlocks(anchor.getRelative(face), collected);
            }
        }
        checkedBlocks.addAll(collected);
        return collected;
    }

    public static void killAllSlimes() {
        slimes.forEach(slime -> slime.remove());
        slimes.clear();
        highlighted.clear();
    }
}
