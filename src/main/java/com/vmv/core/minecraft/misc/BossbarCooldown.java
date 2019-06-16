package com.vmv.core.minecraft.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossbarCooldown {

    private double duration, durationLeft;
    private BossBar bossBar;

    public BossbarCooldown(Player p, double duration, String title) {
        this.duration = duration * 20;
        this.durationLeft = duration * 20;
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', title), BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(p);
        BossbarCooldownManager.addBarCooldown(this, p);
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public double getDuration() {
        return duration;
    }

    public double getDurationLeft() {
        return durationLeft;
    }

    public void setDurationLeft(double durationLeft) {
        this.durationLeft = durationLeft;
    }
}
