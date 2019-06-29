package com.vmv.core.minecraft.misc;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BarTimerManager extends BukkitRunnable {

    public static ArrayList<BarTimer> barCooldowns = new ArrayList<BarTimer>();

    public BarTimerManager(Plugin plugin) {
        this.runTaskTimer(plugin,0, 2);
    }

    @Override
    public void run() {
        ArrayList<BarTimer> toRemove = new ArrayList<BarTimer>();
        for (BarTimer barCooldown : barCooldowns) {
            if (barCooldown.getDurationLeft() <= 0) {
                barCooldown.getBossBar().removeAll();
                toRemove.add(barCooldown);
            } else {
                barCooldown.getBossBar().setProgress(barCooldown.getDurationLeft() / barCooldown.getDuration());
                barCooldown.setDurationLeft(barCooldown.getDurationLeft() - 2);
            }
        }
        toRemove.forEach(bossbarCooldown -> barCooldowns.remove(bossbarCooldown));
    }

    public static void addBarCooldown(BarTimer barCooldown, Player p, String title) {
        ArrayList<BarTimer> toRemove = new ArrayList<BarTimer>();
        for (BarTimer cooldown : barCooldowns) {
            if (cooldown.getBossBar().getPlayers().contains(p) && cooldown.getTitle().equalsIgnoreCase(title)) {
                cooldown.getBossBar().removeAll();
                barCooldown.setDurationLeft(cooldown.getDurationLeft());
                toRemove.add(cooldown);
            }
        }
        toRemove.forEach(bossbarCooldown -> barCooldowns.remove(bossbarCooldown));
        barCooldowns.add(barCooldown);
    }

    /**
     *
     * @param title is the identifier for the timer
     */
    public static boolean isBarTimerActive(Player p, String title) {
        for (BarTimer b : barCooldowns) {
            if (b.getTitle().equalsIgnoreCase(title) && b.getPlayer() == p) {
                return true;
            }
        }
        return false;

    }
}
