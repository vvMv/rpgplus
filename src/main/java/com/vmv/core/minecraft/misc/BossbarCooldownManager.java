package com.vmv.core.minecraft.misc;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class BossbarCooldownManager extends BukkitRunnable {

    public static ArrayList<BossbarCooldown> barCooldowns = new ArrayList<BossbarCooldown>();

    public BossbarCooldownManager(Plugin plugin) {
        this.runTaskTimer(plugin,0, 2);
    }

    @Override
    public void run() {
        ArrayList<BossbarCooldown> toRemove = new ArrayList<BossbarCooldown>();
        for (BossbarCooldown barCooldown : barCooldowns) {
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

    public static void addBarCooldown(BossbarCooldown barCooldown, Player p) {
        ArrayList<BossbarCooldown> toRemove = new ArrayList<BossbarCooldown>();
        for (BossbarCooldown cooldown : barCooldowns) {
            if (cooldown.getBossBar().getPlayers().contains(p)) {
                cooldown.getBossBar().removeAll();
                barCooldown.setDurationLeft(cooldown.getDurationLeft());
                toRemove.add(cooldown);
            }
        }
        toRemove.forEach(bossbarCooldown -> barCooldowns.remove(bossbarCooldown));
        barCooldowns.add(barCooldown);
    }
}
