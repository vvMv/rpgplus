package com.vmv.rpgplus.skill;

import com.vmv.core.minecraft.chat.ChatUtil;
import com.vmv.rpgplus.event.AbilityCycleEvent;
import com.vmv.rpgplus.player.RPGPlayer;
import com.vmv.rpgplus.player.RPGPlayerManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AbilityCycle implements Listener {

    @EventHandler
    public void checkClickCycle(PlayerInteractEvent e) {

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            for (Skill s : SkillManager.getInstance().getSkills()) {
                if (s.getCycleType() != AbilityCycleType.LEFT_CLICK) return;
                if (Arrays.stream(s.getMaterials().toArray()).anyMatch(m -> m == e.getMaterial())) {
                    tryNextCycle(s.getSkillType(), RPGPlayerManager.getInstance().getPlayer(e.getPlayer()));
                }
            }
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            for (Skill s : SkillManager.getInstance().getSkills()) {
                if (s.getCycleType() != AbilityCycleType.RIGHT_CLICK) continue;
                if (Arrays.stream(s.getMaterials().toArray()).anyMatch(m -> m == e.getMaterial())) {
                    tryNextCycle(s.getSkillType(), RPGPlayerManager.getInstance().getPlayer(e.getPlayer()));
                }
            }
        }
    }

    public void tryNextCycle(SkillType st, RPGPlayer rp) {
        List<Ability> a = new ArrayList<>();
        a = AbilityManager.getAbilities().stream().filter(ability -> ability.getSkillType() == st && ability.isEnabled() && !ability.isPassive() && rp.hasAbilityRequirements(ability) && rp.hasAbilityEnabled(ability)).collect(Collectors.toList());
        a.add(null);
        if (a.size() == 0) return;
        if (a.indexOf(rp.getActiveAbility(st)) + 1 >= a.size()) {
            rp.setActiveAbility(st, a.get(0));
        } else {
            rp.setActiveAbility(st, a.get(a.indexOf(rp.getActiveAbility(st)) + 1));
        }
        Bukkit.getPluginManager().callEvent(new AbilityCycleEvent(rp, rp.getActiveAbility(st)));
    }

    @EventHandler
    public void onAbilityCycle(AbilityCycleEvent e) {
        if (e.getAbility() == null) {
            ChatUtil.sendActionMessage(e.getPlayer(), "&eDefault");
        } else {
            ChatUtil.sendActionMessage(e.getPlayer(), "&e" + WordUtils.capitalizeFully(e.getAbility().getName().replaceAll("_", " ")));

        }
    }

}
