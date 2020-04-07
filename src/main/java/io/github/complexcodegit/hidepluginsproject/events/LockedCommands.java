package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import io.github.complexcodegit.hidepluginsproject.managers.CooldownManager;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LockedCommands implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public LockedCommands(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean locked(PlayerCommandPreprocessEvent event){
        FileConfiguration players = plugin.getPlayers();
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();
        if(!config.getBoolean("lockedCommands")) {
            return false;
        }

        event.setCancelled(true);

        if(player.isOp() || player.hasPermission(Objects.requireNonNull(config.getString("bypassPermission")))){
            event.setCancelled(false);
            return false;
        }

        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(Utils.colors(config.getString("cooldown.message")).replaceAll("%time%", String.valueOf(CooldownManager.getCooldown(player))));
        } else {
            String command = event.getMessage().split(" ").length > 0 ? event.getMessage().split(" ")[0] : event.getMessage();
            String pageNumber = event.getMessage().split(" ").length > 1 ? event.getMessage().split(" ")[1] : event.getMessage();

            List<String> commandList = groupManager.getCommands(player, false);
            GroupObject playerGroup = GroupManager.getPlayerGroup(player);
            if(commandList.contains(command)){
                if(command.equalsIgnoreCase("/help") && playerGroup.hasCustomHelp() && groupManager.getHelpPage(player, "1") != null){
                    event.setCancelled(true);
                    if(!(pageNumber.equals(command))){
                        if(groupManager.getHelpPage(player, pageNumber) != null){
                            for(String line : groupManager.getHelpPage(player, pageNumber)) {
                                player.sendMessage(Utils.colors(line).replace("[PAGE]", pageNumber).replace("[LASTPAGE]", String.valueOf(playerGroup.getWorld(player.getWorld().getName()).getPages().size())));
                            }
                        } else {
                            if(config.getBoolean("prefix.enable")){
                                player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+ Objects.requireNonNull(plugin.getMessages().getString("helpPageNotExist")).replace("[PAGE]", pageNumber)));
                            } else {
                                player.sendMessage(Utils.colors(Objects.requireNonNull(plugin.getMessages().getString("helpPageNotExist")).replace("[PAGE]", pageNumber)));
                            }
                        }
                    } else {
                        for(String pag : groupManager.getHelpPage(player, "1")) {
                            player.sendMessage(Utils.colors(pag).replace("[PAGE]", "1").replace("[LASTPAGE]", String.valueOf(playerGroup.getWorld(player.getWorld().getName()).getPages().size())));
                        }
                    }
                    return false;
                }
                event.setCancelled(false);
                return false;
            } else {
                if(Utils.checkCommand(command)){
                    if(config.getBoolean("cooldown.enable")){
                        CooldownManager.setCooldown(player, config.getInt("cooldown.time"));
                    }
                    if(config.getBoolean("playerCommandHistory")){
                        if(Objects.equals(players.getString("Players."+player.getName()+".command-history"), "")){
                            List<String> list = new ArrayList<>();
                            list.add(command);
                            String result = String.join("", list);
                            players.set("Players."+player.getName()+".command-history", result);
                        } else {
                            String history = Objects.requireNonNull(players.getString("Players."+player.getName()+".command-history")).replace(" ", "");
                            List<String> list = new ArrayList<>();
                            list.add(command);
                            list.addAll(Arrays.asList(history.split(",")));
                            String result = String.join(", ", list);
                            players.set("Players."+player.getName()+".command-history", result);
                        }
                    }
                    if(config.getBoolean("warningMessage.title.enable")){
                        player.sendTitle(Utils.colors(config.getString("warningMessage.title.top")), Utils.colors(config.getString("warningMessage.title.bottom")), 10, 70, 20);
                    }
                    if(config.getBoolean("warningMessage.message.enable")){
                        for(String line : config.getStringList("warningMessage.message.lines")){
                            if(config.getBoolean("prefix.enable")){
                                player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+line));
                            } else {
                                player.sendMessage(Utils.colors(line));
                            }
                        }
                    }
                    if(config.getBoolean("potionEffect.enable")){
                        PotionEffectType effect = PotionEffectType.getByName(Objects.requireNonNull(config.getString("potionEffect.effect")));
                        assert effect != null;
                        player.addPotionEffect(new PotionEffect(effect, config.getInt("potionEffect.time")*20, config.getInt("potionEffect.amplifier"), false, false, false));
                    }
                    if(config.getBoolean("particles.enable")){
                        player.spawnParticle(Particle.valueOf(config.getString("particles.particle")), player.getLocation(), config.getInt("particles.amount"));
                    }
                    if(config.getBoolean("sounds.enable")){
                        Utils.randomSound(player);
                    }
                    players.set("Players."+player.getName()+".last-command", command);
                    int reports = players.getInt("Players."+player.getName()+".reports");
                    players.set("Players."+player.getName()+".reports", reports+1);
                    plugin.savePlayers();
                }
            }
        }
        return false;
    }
}
