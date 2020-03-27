package com.vmv.core.minecraft.misc;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BarTimer {

    private double duration;
    private double durationRemaining;
    private BossBar bossBar;
    private String title;
    private Player player;

    public BarTimer(Player p, double duration, String title) {
        this.duration = duration * 20;
        this.durationRemaining = duration * 20;
        this.title = title;
        this.player = p;
        bossBar = Bukkit.createBossBar(ChatColor.translateAlternateColorCodes('&', WordUtils.capitalizeFully(title).replace("_", " ")), BarColor.BLUE, BarStyle.SOLID);
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
        return durationRemaining;
    }

    public void setDurationLeft(double durationLeft) {
        this.durationRemaining = durationLeft;
    }

    public String getTitle() {
        return title;
    }

    public Player getPlayer() {
        return player;
    }
}
