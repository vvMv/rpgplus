package com.vmv.rpgplus.skill.stamina;

import com.vmv.core.information.InformationHandler;
import com.vmv.core.information.InformationType;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class Dash extends Ability implements Listener {

    private HashMap<UUID, Integer> count = new HashMap<UUID, Integer>();
    private double speed;

    public Dash(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.speed = getAbilityConfigSection().getDouble("speed");
    }

    @EventHandler
    public void CrouchToggle(PlayerToggleSneakEvent e) {

        Player p = e.getPlayer();
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(p);
        double level = rp.getLevel(SkillType.STAMINA);

        if (p.isFlying() || isActive(e.getPlayer()) || getRequiredLevel() > level || !rp.hasAbilityEnabled(this)) return;
        if (count.containsKey(p.getUniqueId())) {
            count.put(p.getUniqueId(), count.get(p.getUniqueId()) + 1);
        } else {
            count.put(p.getUniqueId(), 1);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RPGPlus.getInstance(), new Runnable() {
                public void run() {
                    count.remove(p.getUniqueId()); //remove the count after half a second
                }
            }, (long) (10));

        }

        if (count.containsKey(p.getUniqueId())) {
            if (count.get(p.getUniqueId()) >= 3) {
                count.put(p.getUniqueId(), 0);
                if (onCooldown(p)) return;
                double finalDuration = (20 * getDuration(p));
                double finalSpeed = speed + (AbilityAttribute.INCREASE_SPEED.getValuePerPoint(this) * rp.getPointAllocation(this, AbilityAttribute.INCREASE_SPEED));
                setActive(e.getPlayer(), finalDuration / 20 );
                e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int) finalDuration, (int) finalSpeed));
            }
        }
    }
}
