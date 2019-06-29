package com.vmv.core.minecraft.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BarTimer {

    private double duration, durationLeft;
    private BossBar bossBar;
    private String title;
    private Player player;

    public BarTimer(Player p, double duration, String title) {
        this.duration = duration * 20;
        this.durationLeft = duration * 20;
        this.title = title;
        this.player = p;
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', title), BarColor.BLUE, BarStyle.SOLID);
        bossBar.addPlayer(p);
        BarTimerManager.addBarCooldown(this, p, title);
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

    public String getTitle() {
        return title;
    }

    public Player getPlayer() {
        return player;
    }
}
