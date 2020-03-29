package com.vmv.rpgplus.skill.mining;

import com.cryptomorin.xseries.XMaterial;
import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OreLocator extends Ability implements Listener {

    public static List<Material> blocks = new ArrayList<>();
    public static List<Block> checkedBlocks;
    public static List<Location> highlighted = new ArrayList<>();
    public static List<MagmaCube> magmas = new ArrayList<>();
    private static Scoreboard scoreboard;

    public OreLocator(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        try { getAbilityConfigSection().getStringList("blocks").forEach(b -> blocks.add(XMaterial.valueOf(b).parseMaterial())); } catch (IllegalArgumentException ex) { InformationHandler.printMessage(InformationType.ERROR, "Invalid value at ability.ore_locator.blocks", ex.getMessage(), "This error is coming from mining.yml" ); }
        if (!(Bukkit.getVersion().contains("1.8") || Bukkit.getVersion().contains("1.9") || Bukkit.getVersion().contains("1.10") || Bukkit.getVersion().contains("1.11"))) {
            registerColorTeams();
        }
    }

    private void registerColorTeams() {
        if (!getAbilityConfigSection().getBoolean("color")) return;
        this.scoreboard = RPGPlus.getInstance().getServer().getScoreboardManager().getMainScoreboard();
        for (OreColour ore : OreColour.values()) {
            try {
                scoreboard.registerNewTeam("rpg_" + ore.toString().toLowerCase());
                scoreboard.getTeam("rpg_" + ore.toString().toLowerCase()).setColor(ore.getColor());
            } catch(Exception ignore) {
                //Out of date
            }
        }
    }

    public static void unregisterColorTeams() {
        for (OreColour ore : OreColour.values()) {
            scoreboard.getTeam("rpg_" + ore.toString().toLowerCase()).unregister();
        }
    }

    @EventHandler
    public void onLocatorMine(BlockBreakEvent e) {

        if (!isHoldingAbilityItem(e.getPlayer())) return;
        if (checkReady(e.getPlayer())) {
            setActive(e.getPlayer(), getDuration(e.getPlayer()));
            locate(e.getPlayer());
        }
    }

    @EventHandler
    public void mineBlock(BlockBreakEvent e) {
        for (MagmaCube magma : magmas) {
            if (magma.getLocation().getBlock().getLocation().equals(e.getBlock().getLocation())) {
                magmas.remove(magma);
                highlighted.remove(e.getBlock().getLocation());
                magma.remove();
                return;
            }
        }
    }

    public void locate(Player player) {

        int range = (int) RPGPlayerManager.getInstance().getPlayer(player).getAttributeValue(this, AbilityAttribute.INCREASE_RANGE);
        ArrayList<HashSet<Block>> veins = getOreVeins(player.getLocation().getBlock(), range);

        for (HashSet<Block> vein : veins) {

            List<Block> list = new ArrayList<Block>(vein);
            Block block = list.get(0);

            Location l = block.getLocation().add(0.5, 0.3, 0.5);

            if (highlighted.contains(l)) continue;

            MagmaCube sh = (MagmaCube) player.getLocation().getWorld().spawnEntity(l, EntityType.MAGMA_CUBE);

            if (getAbilityConfigSection().getBoolean("color")) {
                for (OreColour oc : OreColour.values()) {
                    if (oc.toString().equalsIgnoreCase(block.getType().toString())) {
                        scoreboard.getTeam("rpg_" + oc.toString().toLowerCase()).addEntry(sh.getUniqueId().toString());
                    }
                }
            }

            sh.setSize(1);
            sh.setGlowing(true);
            sh.setAI(false);
            sh.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1000, 10));
            sh.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000, 100));
            sh.setCollidable(false);
            sh.setGravity(false);
            sh.setInvulnerable(true);
            sh.setHealth(1);
            sh.setSilent(true);
            magmas.add(sh);
            highlighted.add(l);

            Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    sh.remove();
                    magmas.remove(sh);
                    highlighted.remove(l);
                }
            }, (long) (getDuration(player) * 20));


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
        magmas.forEach(magma -> magma.remove());
        magmas.clear();
        highlighted.clear();
    }
}
