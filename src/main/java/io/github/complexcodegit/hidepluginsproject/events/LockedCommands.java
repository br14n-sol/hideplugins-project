package io.github.complexcodegit.hidepluginsproject.events;

import java.util.List;

import io.github.complexcodegit.hidepluginsproject.managers.SoundManager;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
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
        FileConfiguration players = plugin.getPlayers();
        Player player = event.getPlayer();

        if(!plugin.getConfig().getBoolean("locked-commands")) {
            return false;
        }

        event.setCancelled(true);

        if(player.isOp()){
            event.setCancelled(false);
            return false;
        }

        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(plugin.colors(plugin.getConfig().getString("cooldown.message")).replaceAll("%timeRemaining%", String.valueOf(CooldownManager.getCooldown(player))));
        } else {
            String command = event.getMessage().split(" ").length > 0 ? event.getMessage().split(" ")[0] : event.getMessage();

            List<String> commandsList = GroupManager.getCommandsList(player, plugin);
            if(command.equalsIgnoreCase("/hprojectinternal")){
                if(commandsList.contains("/hproject")){
                    event.setCancelled(false);
                    return false;
                }
            }
            if(commandsList.contains(command)) {
                if(command.equalsIgnoreCase("/help")){
                    if(commandsList.contains("/help")){
                        if(plugin.getGroups().getBoolean("groups."+GroupManager.getPlayerGroup(player, plugin)+".custom-help.enable")){
                            event.setCancelled(true);
                            List<String> helpLines = plugin.getGroups().getStringList("groups."+GroupManager.getPlayerGroup(player, plugin)+".custom-help.list");
                            for(int i=0; i < helpLines.size(); i++){
                                player.sendMessage(plugin.colors(helpLines.get(i)));
                            }
                            return false;
                        }
                    }
                }

                event.setCancelled(false);
                return false;
            } else {
                title.send(player, plugin.colors(plugin.getConfig().getString("warning-message.title")), plugin.colors(plugin.getConfig().getString("warning-message.subtitle")), 0, 0, 0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (6*20), 1, false, false, false));
                player.spawnParticle(Particle.SMOKE_LARGE, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 200);

                SoundManager.checkSoundPlayer(plugin, player);
                CooldownManager.setCooldown(player, plugin.getConfig().getInt("cooldown.time"));

                players.set("players." + player.getName() + ".reports", players.getInt("players." + player.getName() + ".reports")+1);
                players.set("players." + player.getName() + ".last-command", command);

                List<String> listCommands = players.getStringList("players." + player.getName() + ".command-history");
                listCommands.add(command);

                players.set("players." + player.getName() + ".command-history", listCommands);

                plugin.savePlayers();
            }
        }
        return false;
    }
}