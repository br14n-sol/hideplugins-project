package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.CooldownManager;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
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
        FileConfiguration groups = plugin.getGroups();
        Player player = event.getPlayer();
        if(!config.getBoolean("locked-commands")) {
            return false;
        }

        event.setCancelled(true);

        if(player.isOp() || player.hasPermission(Objects.requireNonNull(config.getString("bypass-permission")))){
            event.setCancelled(false);
            return false;
        }

        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(Utils.colors(config.getString("cooldown.message")).replaceAll("%time%", String.valueOf(CooldownManager.getCooldown(player))));
        } else {
            String command = event.getMessage().split(" ").length > 0 ? event.getMessage().split(" ")[0] : event.getMessage();
            String pageNumber = event.getMessage().split(" ").length > 1 ? event.getMessage().split(" ")[1] : event.getMessage();

            List<String> commandList = groupManager.getCommands(player, false);
            String playerGroup = groupManager.getPlayerGroup(player);
            if(commandList.contains(command)){
                if(command.equalsIgnoreCase("/help") && groups.contains("groups."+playerGroup+".options.custom-help") && groups.getBoolean("groups."+playerGroup+".options.custom-help.enable")
                        && groups.contains("groups."+playerGroup+".options.custom-help.worlds."+player.getWorld().getName())){
                    event.setCancelled(true);
                    List<String> pages = new ArrayList<>(Objects.requireNonNull(groups.getConfigurationSection("groups."+playerGroup+".options.custom-help.worlds."+player.getWorld().getName()+".pages")).getKeys(false));
                    if(!(pageNumber.equals(command))){
                        if(pages.contains(pageNumber)){
                            for(String pag : groups.getStringList("groups."+playerGroup+".options.custom-help.worlds."+player.getWorld().getName()+".pages."+pageNumber))
                                player.sendMessage(Utils.colors(pag));
                        } else {
                            if(config.getBoolean("prefix.enable")){
                                player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+ Objects.requireNonNull(plugin.getMessages().getString("help-page-not-exist")).replace("[PAGE]", pageNumber)));
                            } else {
                                player.sendMessage(Utils.colors(Objects.requireNonNull(plugin.getMessages().getString("help-page-not-exist")).replace("[PAGE]", pageNumber)));
                            }
                        }
                    } else {
                        if(pages.contains("1")){
                            for(String pag : groups.getStringList("groups."+playerGroup+".options.custom-help.worlds."+player.getWorld().getName()+".pages.1"))
                                player.sendMessage(Utils.colors(pag));
                        } else {
                            if(config.getBoolean("prefix.enable")){
                                player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+ Objects.requireNonNull(plugin.getMessages().getString("help-page-not-exist")).replace("[PAGE]", "1")));
                            } else {
                                player.sendMessage(Utils.colors(Objects.requireNonNull(plugin.getMessages().getString("help-page-not-exist")).replace("[PAGE]", "1")));
                            }
                        }
                    }
                    return false;
                }
                event.setCancelled(false);
                return false;
            } else {
                if(config.getBoolean("cooldown.enable")){
                    CooldownManager.setCooldown(player, config.getInt("cooldown.time"));
                }
                if(config.getBoolean("player-command-history") && Utils.checkCommand(command)){
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
                if(config.getBoolean("warning-message.title.enable")){
                    player.sendTitle(Utils.colors("warning-message.title.top"), Utils.colors("warning-message.title.bottom"), 10, 70, 20);
                }
                if(config.getBoolean("warning-message.message.enable")){
                    for(String line : config.getStringList("warning-message.message.list")){
                        if(config.getBoolean("prefix.enable")){
                            player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+line));
                        } else {
                            player.sendMessage(Utils.colors(line));
                        }
                    }
                }
                if(config.getBoolean("potion-effect.enable")){
                    PotionEffectType effect = PotionEffectType.getByName(Objects.requireNonNull(config.getString("potion-effect.effect")));
                    assert effect != null;
                    player.addPotionEffect(new PotionEffect(effect, config.getInt("potion-effect.time")*20, config.getInt("potion-effect.amplifier"), false, false, false));
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
        return false;
    }
}
