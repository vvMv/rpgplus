package com.vmv.rpgplus.dependency;

import me.konsolas.aac.api.AACAPI;
import me.konsolas.aac.api.AACExemption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class AACHook implements AntiCheatHook {

    private final AACAPI api;
    private final AACExemption exemption = new AACExemption("The player is using RPGPlus");

    public AACHook() {
        this.api = Bukkit.getServicesManager().load(AACAPI.class);

        if (api == null) {
            throw new IllegalStateException("Tried to initialize " + getClass().getName() + " but couldn't find AACAPI");
        }
    }

    @Override
    public String getPluginName() {
        return "AAC5";
    }

    @Override
    public void exempt(Player player) {
        this.api.addExemption(player, exemption);
    }

    @Override
    public void unexempt(Player player) {
        this.api.removeExemption(player, exemption);
    }
}
