package com.vmv.rpgplus.skill.woodcutting;

import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TreeCutter extends BukkitRunnable {

    private Player player;
    private Block startBlock;
    private List<String> comparisonBlockArray = new ArrayList<>();
    private List<String> comparisonBlockArrayLeaves = new ArrayList<>();
    private List<Block> blocks = new ArrayList<>();
    private List<Block> leaves = new ArrayList<>();
    private int maxsize;
    private double delay;

    public TreeCutter(Player player, Block startBlock, double delay, int maxsize) {
        this.delay = delay;
        this.player = player;
        this.startBlock = startBlock;
        this.maxsize = maxsize;
    }

    @Override
    public void run() {
        blocks.add(startBlock);
        runBlockLoop(startBlock, startBlock.getX(), startBlock.getZ(), 0);

        int c = 0;
        for (Block b : blocks) {
            Bukkit.getServer().getScheduler().runTaskLater(RPGPlus.getInstance(), () -> {
                Location center = b.getLocation().add(0.5, 0.5, 0.5);
                for (ItemStack stack : b.getDrops()) {
                    b.getWorld().dropItem(center, stack);
                    b.getWorld().playSound(center, Sound.BLOCK_WOOD_STEP, 1.0f, 1.0f);
                    //b.getWorld().playEffect(center, Effect.STEP_SOUND, startBlock.getType());
                    b.setType(Material.AIR);
                }
            }, (long) (20 * c * delay));

            c++;

        }

        this.cancel();
    }

    public void runBlockLoop(Block b1, final int x1, final int z1, int size) {
        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0)
                        continue;

                    if (size >= maxsize) break;
                    Block b2 = b1.getRelative(x, y, z);
                    String s = b2.getX() + ":" + b2.getY() + ":" + b2.getZ();

                    int searchSquareSize = 25;
                    if (b2.getX() > x1 + searchSquareSize || b2.getX() < x1 - searchSquareSize || b2.getZ() > z1 + searchSquareSize || b2.getZ() < z1 - searchSquareSize)
                        break;
                    if (Arrays.stream(TreeFeller.leaves.toArray()).anyMatch(l -> l == b2.getType()) && !comparisonBlockArrayLeaves.contains(s))
                        comparisonBlockArrayLeaves.add(s);
                        leaves.add(b2);
                    if (!TreeFeller.logs.contains(b2.getType()))
                        continue;
                    if (!comparisonBlockArray.contains(s)) {
                        comparisonBlockArray.add(s);
                        blocks.add(b2);
                        this.runBlockLoop(b2, x1, z1, size++);
                    }
                }
            }
        }
    }
}
