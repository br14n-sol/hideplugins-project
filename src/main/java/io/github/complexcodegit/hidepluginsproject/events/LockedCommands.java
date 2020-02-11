package io.github.complexcodegit.hidepluginsproject.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.github.complexcodegit.hidepluginsproject.managers.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.CooldownManager;
import io.github.complexcodegit.hidepluginsproject.utils.Title;

public class LockedCommands implements Listener {
    private HidePluginsProject plugin;
    private Title title;

    public LockedCommands(HidePluginsProject plugin, Title title) {
        this.plugin = plugin;
        this.title = title;
    }

    @EventHandler
    public boolean onBlockedCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if(!plugin.getConfig().getBoolean("locked-commands")) {
            return false;
        }

        event.setCancelled(true);

        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(plugin.colors("&cYou can't use another command for &f" + CooldownManager.getCooldown(player) + " &cseconds."));
        } else {
            String permission = GroupManager.getPlayerGroupPermission(player, plugin);
            if(permission == null || permission.isEmpty()) {
                title.send(player, plugin.colors(plugin.getConfig().getString("warning-message.title")), plugin.colors(plugin.getConfig().getString("warning-message.subtitle")), 0, 0, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (6*20), 1, false, false, false));
                player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 200);

                SoundManager.playRandomSound(plugin, player);
                CooldownManager.setCooldown(player, 6);
            }

            String command = event.getMessage().split(" ").length > 0 ? event.getMessage().split(" ")[0] : event.getMessage();

            List<String> commandsList = GroupManager.getCommandsList(player, plugin);
            if(commandsList.contains(command)) {
                event.setCancelled(false);
                return false;
            } else {
                title.send(player, plugin.colors(plugin.getConfig().getString("warning-message.title")), plugin.colors(plugin.getConfig().getString("warning-message.subtitle")), 0, 0, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (6*20), 1, false, false, false));
                player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 200);

                SoundManager.playRandomSound(plugin, player);
                CooldownManager.setCooldown(player, 6);
            }
        }
        return false;
    }
}