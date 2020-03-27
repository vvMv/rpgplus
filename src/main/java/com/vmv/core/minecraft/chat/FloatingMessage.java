package com.vmv.core.minecraft.chat;

import com.vmv.core.math.MathUtils;
import com.vmv.rpgplus.main.RPGPlus;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Random;

public class FloatingMessage {

    private static ArrayList<ArmorStand> floatingMessage = new ArrayList<>();

    public static void send(Player player, String message, double seconds) {

        if (player == null || player.getGameMode() == GameMode.SPECTATOR || player.hasPotionEffect(PotionEffectType.INVISIBILITY)) return;

        ArmorStand as = player.getWorld().spawn(getVariateLocation(player.getLocation()), ArmorStand.class);
        as.setVisible(false);
        as.setSmall(true);
        as.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, (int) (seconds*20), 1));
        as.setCustomNameVisible(true);
        as.setCustomName(message);
        as.setRemoveWhenFarAway(true);

        floatingMessage.add(as);

        Bukkit.getScheduler().runTaskLater(RPGPlus.getInstance(), new Runnable() {
            public void run() {
                floatingMessage.remove(as);
                as.remove();
            }
        }, (long) (seconds * 20));

    }

    private static Location getVariateLocation(Location l) {
        Random r = new Random();
        l.setYaw(r.nextInt() > 50 ? l.getYaw() - MathUtils.getRandom(25, 15) : l.getYaw() - -MathUtils.getRandom(25, 15));
        l.setPitch(l.getPitch() + MathUtils.getRandom(-6, -12));
        l.add(l.getDirection().multiply(5));
        return l;
    }

    public ArrayList<ArmorStand> getFloatingMessages() {
        return floatingMessage;
    }
}
