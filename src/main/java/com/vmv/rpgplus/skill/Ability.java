package com.vmv.rpgplus.skill;

import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.core.minecraft.misc.BarTimer;
import com.vmv.core.minecraft.misc.Cooldowns;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.SkillManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public abstract class Ability {

    protected boolean enabled, passive = false;
    protected String description = "default description";
    protected double cooldown, duration;
    protected int requiredLevel;
    private String name;
    private SkillType skillType;
    private FileConfiguration skillConfig;
    private HashMap<Player, Double> active = new HashMap<Player, Double>();

    public Ability(String name, SkillType st) {
        this.skillType = st;
        this.name = name;
        this.skillConfig = SkillManager.getInstance().getSkill(st).getConfig();
        this.requiredLevel = getAbilityConfigSection().getInt("level");
        this.cooldown = getAbilityConfigSection().getInt("cooldown");
        this.duration = getAbilityConfigSection().getInt("duration");
        this.enabled = getAbilityConfigSection().getBoolean("enabled");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RPGPlus.getInstance(), () -> updateActive(), 0, 2);
    }

    private void updateActive() {
        ArrayList<Player> toRemove = new ArrayList<>();
        active.forEach((player, time) -> {
            if (time < 0) {
                toRemove.add(player);
            } else {
                active.put(player, time - 0.1);
            }
        });
        toRemove.forEach(r -> active.remove(r));
    }

    public ConfigurationSection getAbilityConfigSection() {
        return skillConfig.getConfigurationSection("ability." + getName() + ".");
    }

    public String getName() {
        return name;
    }

    public double getCooldown() {
        return cooldown;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPassive() {
        return passive;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public SkillType getSkillType() {
        return skillType;
    }

    public boolean isHoldingAbilityItem(Player player) {
        return Arrays.stream(SkillManager.getInstance().getSkill(getSkillType()).getMaterials().toArray()).anyMatch(m -> m == player.getInventory().getItemInMainHand().getType());
    }

    public boolean isActive(Player p) {
        return active.containsKey(p);
    }

    public void setActive(Player p, double duration) {
        active.put(p, duration);
        //if bar timer enabled
        new BarTimer(p, duration, getName());
    }

    public boolean isSelected(LivingEntity entity) {
        if (!(entity instanceof Player)) return false;
        return RPGPlayerManager.getInstance().getPlayer((Player) entity).getActiveAbility(getSkillType()) == this ? true : false;
    }

    public boolean onCooldown(Player p) {
        if (!Cooldowns.tryCooldown(p, getName(), getAbilityConfigSection().getLong("cooldown") * 1000)) {
            ChatUtil.sendActionMessage(p, "&3" + getName() + "&7 on cooldown &6" + MathUtils.round((Double.valueOf(Cooldowns.getCooldown(p, getName()))/1000), 1) + "s");
            return true;
        }
        return false;
    }

    public boolean checkReady(LivingEntity entity) {
        if (!(entity instanceof Player)) return false;
        return (isSelected(entity) && !isActive((Player) entity) && !onCooldown((Player) entity)) ? true : false;
    }

    public double getDuration() {
        return duration;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
