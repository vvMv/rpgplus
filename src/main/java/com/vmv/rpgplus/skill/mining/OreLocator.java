package com.vmv.rpgplus.skill.mining;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OreLocator extends Ability implements Listener {

    public static List<Material> blocks = new ArrayList<>();
    public static List<Location> highlighted = new ArrayList<>();

    public OreLocator(String name, SkillType st) {
        super(name, st);
    }

    @EventHandler
    public void onLocatorMine(BlockBreakEvent e) {

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) {
            setActive(e.getPlayer(), getDuration());
            locate(e.getPlayer());
        }

        try { getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(Material.valueOf(b))); } catch (IllegalArgumentException ex) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.ore_locator.blocks", ex.getMessage(), "This error is coming from mining.yml" ); }


    }

    @EventHandler
    public void onLocateMove(PlayerMoveEvent e) {
        if (getActive().containsKey(e.getPlayer())) {
            locate(e.getPlayer());
        }
    }

    public void locate(Player player) {

        for(Block block : getBlocks(player.getLocation().getBlock(), 15)) {

            Location l = block.getLocation().add(0.5, 0.5, 0.5);

            if (highlighted.contains(l)) continue;

            if (blocks.contains(block.getType())) {
                Shulker sh = (Shulker) player.getLocation().getWorld().spawnEntity(l, EntityType.SHULKER);
                sh.setGlowing(true);
                sh.setAI(false);
                sh.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1));
                sh.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 100));
                sh.setCollidable(false);
                sh.setGravity(false);
                sh.setInvulnerable(true);
                sh.setSilent(true);

//                Slime sl = (Slime)  player.getLocation().getWorld().spawnEntity(l, EntityType.SLIME);
//                sl.setGlowing(true);
//                sl.setSize(1);
//                sl.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10, 1));
//                sl.setGravity(false);
//                sl.setSize(1);
//                sl.setInvulnerable(true);
//                sl.setSilent(true);
//                sl.setWander(false);
//                sl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10, 100));

                highlighted.add(l);
            }
        }


    }

    public List<Block> getBlocks(Block start, int radius){
        if (radius < 0) {
            return new ArrayList<Block>(0);
        }
        int iterations = (radius * 2) + 1;
        List<Block> blocks = new ArrayList<Block>(iterations * iterations * iterations);
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(start.getRelative(x, y, z));
                }
            }
        }
        return blocks;
    }

}
//TODO figure out if this is possible without lag