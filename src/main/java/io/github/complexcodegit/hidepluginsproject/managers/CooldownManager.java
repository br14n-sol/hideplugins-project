package io.github.complexcodegit.hidepluginsproject.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CooldownManager {
    static HashMap<UUID, Double> cooldowns = new HashMap<>();

    public static void setCooldown(Player player, int seconds) {
        double delay = System.currentTimeMillis() + (seconds * 1000);
        cooldowns.put(player.getUniqueId(), delay);
    }

    public static int getCooldown(Player player) {
        return Math.toIntExact(Math.round((cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000));
    }

    public static boolean checkCooldown(Player player) {
        if(!cooldowns.containsKey(player.getUniqueId()) || cooldowns.get(player.getUniqueId()) <= System.currentTimeMillis()) {
            return true;
        }
        return false;
    }
}
