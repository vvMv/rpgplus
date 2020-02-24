package com.vmv.rpgplus.event;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.core.math.MathUtils;

import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class ExperienceModifyEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final RPGPlayer rp;
    private final SkillType skill;
    private double exp;

    private static ArrayList<ArmorStand> animationStands = new ArrayList<>();

    public ExperienceModifyEvent(RPGPlayer rp, SkillType skill, double exp) {
        super(Bukkit.getPlayer(rp.getUuid()));
        this.rp = rp;
        this.skill = skill;
        this.exp = MathUtils.round(exp, 2);
        spawnExperienceAnimation(1);
    }

    public double getExp() {
        return this.exp;
    }

    public SkillType getSkill() {
        return this.skill;
    }

    public RPGPlayer getRPGPlayer() {
        return this.rp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private void spawnExperienceAnimation(double duration) {
        if (!RPGPlus.getInstance().getConfig().getBoolean("general.experience_animation")) return;
        if (player == null || player.getGameMode() == GameMode.SPECTATOR || player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;
        ArmorStand as = player.getWorld().spawn(getVariateLocation(player.getLocation()), ArmorStand.class);
        as.setVisible(false);
        as.setSmall(true);
        as.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (duration*20), 1));
        as.setCustomNameVisible(true);
        as.setCustomName(SkillManager.getInstance().getSkill(getSkill()).getSkillColor() + "+" + getExp() + "xp");

        animationStands.add(as);

        Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
            public void run() {
                animationStands.remove(as);
                as.remove();
            }
        }, (long) (duration * 20));
    }

    private Location getVariateLocation(Location l) {
        Random r = new Random();
        l.setYaw(r.nextInt() > 50 ? l.getYaw() - MathUtils.getRandom(25, 15) : l.getYaw() - -MathUtils.getRandom(25, 15));
        l.setPitch(l.getPitch() + MathUtils.getRandom(-6, -12));
        l.add(l.getDirection().multiply(5));
        return l;
    }

    public static ArrayList<ArmorStand> getAnimationStands() {
        return animationStands;
    }
}