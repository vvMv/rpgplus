package com.vmv.rpgplus.skill;

import com.vmv.core.config.FileManager;
import com.vmv.core.math.MathUtils;
import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.core.minecraft.misc.BarTimer;
import com.vmv.core.minecraft.misc.Cooldowns;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayerManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Ability {

    protected boolean enabled;
    protected boolean passive = false;
    protected String description = "default description";
    protected double cooldown;
    protected double duration;
    protected int requiredLevel;
    protected List<AbilityAttribute> attributes = new ArrayList<>();

    private String name;
    private SkillType skillType;
    private FileConfiguration skillConfig;
    private HashMap<Player, Double> active = new HashMap<Player, Double>();

    public Ability(String name, SkillType st, AbilityAttribute... attributes) {
        this.skillType = st;
        this.name = name;
        this.attributes.addAll(Arrays.asList(attributes));
        this.skillConfig = SkillManager.getInstance().getSkill(st).getConfig();
        reload();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(RPGPlus.getInstance(), () -> updateActive(), 0, 2);
    }

    public void reload() {
        this.requiredLevel = getAbilityConfigSection().getInt("level");
        this.cooldown = getAbilityConfigSection().getInt("cooldown");
        this.duration = getAbilityConfigSection().getInt("duration");
        this.enabled = getAbilityConfigSection().getBoolean("enabled");
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
        return skillConfig.getConfigurationSection("ability." + this.name + ".");
    }

    public String getName() {
        return name;
    }

    public String getFormattedName() {
        try {
            return FileManager.getLang().getString("ability." + getName().toLowerCase());
        } catch (Exception ignore) {
            return WordUtils.capitalizeFully(getName().replaceAll("_", " "));
        }
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

    public HashMap<Player, Double> getActive() {
        return active;
    }

    public List<AbilityAttribute> getAttributes() {
        return attributes;
    }

    public boolean isHoldingAbilityItem(Player player) {
        return Arrays.stream(SkillManager.getInstance().getSkill(this.skillType).getMaterials().toArray()).anyMatch(m -> m == player.getInventory().getItemInMainHand().getType());
    }

    public boolean isActive(Player p) {
        return active.containsKey(p);
    }

    public void setActive(Player p, double duration) {
        active.put(p, duration);
        //if bar timer enabled
        ChatUtil.sendActionMessage(p, "&f" + WordUtils.capitalizeFully(this.name).replace("_", " ") + " &aactivated!", RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.ability_active"));
        new BarTimer(p, duration, this.name);
    }

    public boolean isSelected(LivingEntity entity) {
        if (!(entity instanceof Player)) return false;
        return RPGPlayerManager.getInstance().getPlayer((Player) entity).getActiveAbility(this.skillType) == this ? true : false;
    }

    public boolean onCooldown(Player p) {
        double pointReduction = RPGPlayerManager.getInstance().getPlayer(p).getPointAllocation(this, AbilityAttribute.DECREASE_COOLDOWN) * AbilityAttribute.DECREASE_COOLDOWN.getValuePerPoint(this);
        if (!Cooldowns.tryCooldown(p, this.name, (getAbilityConfigSection().getLong("cooldown") + (long) pointReduction) * 1000)) {
            ChatUtil.sendActionMessage(p, "&3" + WordUtils.capitalizeFully(this.name).replace("_", " ") + "&7 on cooldown &6" + MathUtils.round((Double.valueOf(Cooldowns.getCooldown(p, this.name))/1000), 1) + "s", RPGPlus.getInstance().getConfig().getInt("actionbar.priority.priorities.ability_cooldown"));
            return true;
        }
        return false;
    }

    public boolean checkReady(LivingEntity entity) {
        if (!(entity instanceof Player)) return false;
        return (isSelected(entity) && !isActive((Player) entity) && !onCooldown((Player) entity)) ? true : false;
    }

    public FileConfiguration getSkillConfig() {
        return skillConfig;
    }

    public double getDuration(Player p) {
        return (duration + (AbilityAttribute.INCREASE_DURATION.getValuePerPoint(this) * RPGPlayerManager.getInstance().getPlayer(p).getPointAllocation(this, AbilityAttribute.INCREASE_DURATION)));
    }

    public boolean isEnabled() {
        return enabled;
    }
}
