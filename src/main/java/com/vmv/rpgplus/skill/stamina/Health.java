package com.vmv.rpgplus.skill.stamina;

import com.vmv.rpgplus.event.AbilityToggleEvent;
import com.vmv.rpgplus.event.PointModifyEvent;
import com.vmv.rpgplus.main.RPGPlus;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Ability;
import com.vmv.rpgplus.skill.AbilityAttribute;
import com.vmv.rpgplus.skill.AbilityManager;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Health extends Ability implements Listener {

    private int defaultHearts;

    public Health(String name, SkillType st, AbilityAttribute... attributes) {
        super(name, st, attributes);
        this.passive = true;
        this.defaultHearts = getAbilityConfigSection().getInt("hearts");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void updateHealthEvent(PlayerJoinEvent e) {
        Bukkit.getScheduler().runTaskAsynchronously(RPGPlus.getInstance(),() -> updateHearts(e.getPlayer()));
        //updateHearts(e.getPlayer());
    }

    @EventHandler
    public void healthAbilityEvent(PointModifyEvent e) {
        if (e.getPlayer() == null) return;
        if (e.getAbility() == AbilityManager.getInstance().getAbility("health")) updateHearts(e.getPlayer());
    }

    @EventHandler
    public void healthToggled(AbilityToggleEvent e) {
        if (e.getAbility() == this) updateHearts(e.getPlayer());
    }

    private void updateHearts(Player player) {
        if (!player.isOnline()) return;
        RPGPlayer rp = RPGPlayerManager.getInstance().getPlayer(player);

        if (rp == null) {
            Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    updateHearts(player);
                }
            }, 20L);
            return;
        }

        double amount = RPGPlayerManager.getInstance().getPlayer(player).hasAbilityEnabled(this) ? defaultHearts + (rp.getPointAllocation(this, AbilityAttribute.INCREASE_HEARTS) * AbilityAttribute.INCREASE_HEARTS.getValuePerPoint(this)) : defaultHearts;
        player.setHealthScale(amount);
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(amount);


    }
}
