package io.github.complexcodegit.hidepluginsproject.events;

import java.util.ArrayList;
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

        if(player.isOp() || player.hasPermission("hidepluginsproject.bypass")){
            event.setCancelled(false);
            return false;
        }

        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(plugin.colors(plugin.getConfig().getString("cooldown.message")).replaceAll("%timeRemaining%", String.valueOf(CooldownManager.getCooldown(player))));
        } else {
            String command = event.getMessage().split(" ").length > 0 ? event.getMessage().split(" ")[0] : event.getMessage();
            String pageNumber = event.getMessage().split(" ").length > 1 ? event.getMessage().split(" ")[1] : event.getMessage();

            List<String> commandsList = GroupManager.getCommandsList(player, plugin);
            String group = GroupManager.getPlayerGroup(player, plugin);
            if(plugin.getConfig().getBoolean("player-command-history")){
                if(command.equalsIgnoreCase("/hprojectinternal")){
                    if(commandsList.contains("/hproject")){
                        event.setCancelled(false);
                        return false;
                    }
                }
            }
            if(commandsList.contains(command)) {
                if(command.equalsIgnoreCase("/help")){
                    if(commandsList.contains("/help")){
                        if(plugin.getGroups().getBoolean("groups."+group+".custom-help.enable")){
                            event.setCancelled(true);
                            List<String> page;
                            List<String> pages = new ArrayList<>();
                            for(String s : plugin.getGroups().getConfigurationSection("groups."+group+".custom-help.pages").getKeys(false)){
                                if(!pages.contains(s)){
                                    pages.add(s);
                                }
                            }
                            if(!(pageNumber.equals(command))){
                                if(pages.contains(pageNumber)){
                                    page = plugin.getGroups().getStringList("groups."+group+".custom-help.pages."+pageNumber);
                                    for(int i=0; i < page.size(); i++){
                                        player.sendMessage(plugin.colors(page.get(i)).replaceAll("%pages%", String.valueOf(pages.size())));
                                    }
                                } else {
                                    player.sendMessage(plugin.colors("&cPage &b"+pageNumber+" &cdoes not exist."));
                                }
                            } else {
                                page = plugin.getGroups().getStringList("groups."+group+".custom-help.pages.1");
                                for(int i=0; i < page.size(); i++){
                                    player.sendMessage(plugin.colors(page.get(i)).replaceAll("%pages%", String.valueOf(pages.size())));
                                }
                            }
                            return false;
                        }
                    }
                }
                event.setCancelled(false);
                return false;
            } else {
                if(plugin.getConfig().getBoolean("warning-message.enable")){
                    if(plugin.getConfig().getString("warning-message.type").equals("screen-title")){
                        title.send(player, plugin.colors(plugin.getConfig().getString("warning-message.screen-title.title")), plugin.colors(plugin.getConfig().getString("warning-message.screen-title.subtitle")), 0, 0, 0);
                    } else if(plugin.getConfig().getString("warning-message.type").equals("chat-message")){
                        List<String> messages = plugin.getConfig().getStringList("warning-message.chat-message");
                        for(int i=0; i < messages.size(); i++){
                            player.sendMessage(plugin.colors(messages.get(i)));
                        }
                    }
                }
                if(plugin.getConfig().getBoolean("potion-effect.enable")){
                    String name = plugin.getConfig().getString("potion-effect.effect");
                    PotionEffectType effect;
                    effect = PotionEffectType.getByName(name);
                    if(plugin.serverVersion.equalsIgnoreCase("v1_12_R1")){
                        player.addPotionEffect(new PotionEffect(effect, (plugin.getConfig().getInt("potion-effect.time")*20), plugin.getConfig().getInt("potion-effect.amplifier"), false, false));
                    } else {
                        player.addPotionEffect(new PotionEffect(effect, (plugin.getConfig().getInt("potion-effect.time")*20), plugin.getConfig().getInt("potion-effect.amplifier"), false, false, false));
                    }
                }
                if(plugin.getConfig().getBoolean("particles.enable")){
                    player.spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.particle")), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), 200);
                }

                SoundManager.checkSoundPlayer(plugin, player);
                if(plugin.getConfig().getBoolean("cooldown.enable")){
                    CooldownManager.setCooldown(player, plugin.getConfig().getInt("cooldown.time"));
                }
                players.set("players." + player.getName() + ".reports", players.getInt("players." + player.getName() + ".reports")+1);
                players.set("players." + player.getName() + ".last-command", command);
                if(plugin.getConfig().getBoolean("player-command-history")){
                    List<String> listCommands = players.getStringList("players." + player.getName() + ".command-history");
                    listCommands.add(command);
                    players.set("players." + player.getName() + ".command-history", listCommands);
                }
                plugin.savePlayers();
            }
        }
        return false;
    }
}