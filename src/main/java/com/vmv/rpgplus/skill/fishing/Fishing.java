package com.vmv.rpgplus.skill.fishing;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.player.RPGPlayerManager;
import com.vmv.rpgplus.skill.Skill;
import com.vmv.rpgplus.skill.SkillType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.HashMap;

public class Fishing extends Skill implements Listener {

    public Fishing(SkillType skillType) {
        super(skillType);
    }

    @Override
    protected void registerAbilities() {

    }

    @Override
    protected void registerEvents() {
        registerEvents(this);
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {

        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {

            if (getConfig().getStringList("exclude").contains(e.getCaught().getName().toUpperCase())) {
                return;
            }

            double xp = MathUtils.getRandom(getConfig().getDouble("experience.OTHER.max"), getConfig().getDouble("experience.OTHER.min"));

            for (String fish : getConfig().getConfigurationSection("experience").getKeys(false)) {
                if (e.getCaught().getName().replaceAll(" ", "_").equalsIgnoreCase(fish)) {
                    xp = MathUtils.getRandom(getConfig().getDouble("experience." + fish.toUpperCase() + ".max"), getConfig().getDouble("experience." + fish + ".min"));
                    break;
                }
            }


            HashMap<Player, Integer> allCanes = new HashMap<>();

            for (String configPlayer : getConfig().getConfigurationSection("Canes").getKeys(false)) {
                Player p = Bukkit.getPlayer(configPlayer);
                int amount = getConfig().getInt("Canes." + configPlayer);
                allCanes.put(p, amount);
            }



            RPGPlayerManager.getInstance().getPlayer(e.getPlayer()).addXP(SkillType.FISHING, xp);
        }
    }

}
