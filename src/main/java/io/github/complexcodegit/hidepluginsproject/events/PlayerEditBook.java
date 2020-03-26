package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerEditBook implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public PlayerEditBook(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler
    public void editCheck(PlayerEditBookEvent event){
        if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Commands §8- §7HidePlugins Project") ||
                event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project") ||
                event.getPreviousBookMeta().getDisplayName().equals("§aRemove Commands §8- §7HidePlugins Project") ||
                event.getPreviousBookMeta().getDisplayName().equals("§aRemove Tabs §8- §7HidePlugins Project")){
            List<String> content = event.getNewBookMeta().getPages();
            List<String> lore = event.getPreviousBookMeta().getLore();

            List<String> commands;

            List<String> contentCommands = new ArrayList<>();
            List<String> alreadyExist = new ArrayList<>();
            List<String> successfully = new ArrayList<>();
            List<String> notExist = new ArrayList<>();

            Player player = event.getPlayer();

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.getInventory().removeItem(Utils.getItem(event.getPreviousBookMeta().getDisplayName(), player)),1);

            player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
            player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F);

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 4);

            String[] nameParts = event.getPreviousBookMeta().getDisplayName().split(" ");
            String action = nameParts[0].substring(2); String option = nameParts[1];

            String group = lore.get(0).substring(11); String world = lore.get(1).substring(11);
            if(world.equals("global")){
                if(option.equalsIgnoreCase("Tabs")){
                    commands = groupManager.getGlobalTab(group);
                } else {
                    commands = groupManager.getGlobalCommands(group);
                }
            } else {
                if(option.equalsIgnoreCase("Tabs")){
                    commands = groupManager.getWorldTab(group, world);
                } else {
                    commands = groupManager.getWorldCommands(group, world);
                }
            }

            for(String cmds : content){
                contentCommands.addAll(Arrays.asList(cmds.replace(" ", "").split(",")));
            }
            for(String command : contentCommands) {
                if(Utils.checkCommand(command)) {
                    if(action.equalsIgnoreCase("Add")){
                        if(!commands.contains(command)) {
                            commands.add(command);
                            if(!successfully.contains(command)){
                                successfully.add(command);
                            }
                        } else {
                            if(!alreadyExist.contains(command)){
                                alreadyExist.add(command);
                            }
                        }
                    } else {
                        if(commands.contains(command)) {
                            commands.remove(command);
                            if(!successfully.contains(command)){
                                successfully.add(command);
                            }
                        } else {
                            if(!alreadyExist.contains(command)){
                                alreadyExist.add(command);
                            }
                        }
                    }
                } else {
                    if(!notExist.contains(command)) {
                        notExist.add(command);
                    }
                }
            }

            FileConfiguration groups = plugin.getGroups();
            FileConfiguration messages = plugin.getMessages();
            if(world.equals("global")){
                if(option.equalsIgnoreCase("Tabs")){
                    groups.set("groups."+group+".global.tab", String.join(", ", commands));
                } else {
                    groups.set("groups."+group+".global.commands", String.join(", ", commands));
                }
            } else {
                if(option.equalsIgnoreCase("Tabs")){
                    groups.set("groups."+group+".worlds."+world+".tabs", String.join(", ", commands));
                } else {
                    groups.set("groups."+group+".worlds."+world+".commands", String.join(", ", commands));
                }
            }
            plugin.saveGroups();
            plugin.reloadGroups();

            TextComponent size = new TextComponent();
            TextComponent message = new TextComponent();
            if(!notExist.isEmpty()){
                size.setText(plugin.prefix+"§7"+notExist.size()+" ");
                if(notExist.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", notExist)).create()));
                    message.setText(Utils.colors(messages.getString("commands-not-valid")));
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(notExist.get(0)).create()));
                    message.setText(Utils.colors(messages.getString("command-not-valid")));
                }
                player.spigot().sendMessage(size, message);
            }
            if(!alreadyExist.isEmpty()){
                size.setText(plugin.prefix+"§7"+alreadyExist.size()+" ");
                if(alreadyExist.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", alreadyExist)).create()));
                    if(action.equalsIgnoreCase("Add")){
                        message.setText(Utils.colors(messages.getString("commands-already-exist")));
                    } else {
                        message.setText(Utils.colors(messages.getString("command-not-exist")));
                    }
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(alreadyExist.get(0)).create()));
                    if(action.equalsIgnoreCase("Add")){
                        message.setText(Utils.colors(messages.getString("command-already-exist")));
                    } else {
                        message.setText(Utils.colors(messages.getString("commands-not-exist")));
                    }
                }
                player.spigot().sendMessage(size, message);
            }
            if(!successfully.isEmpty()){
                size.setText(plugin.prefix+"§7"+successfully.size()+" ");
                if(successfully.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", successfully)).create()));
                    if(action.equalsIgnoreCase("Add")){
                        message.setText(Utils.colors(messages.getString("commands-added")));
                    } else {
                        message.setText(Utils.colors(messages.getString("commands-removed")));
                    }
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(successfully.get(0)).create()));
                    if(action.equalsIgnoreCase("Add")){
                        message.setText(Utils.colors(messages.getString("command-added")));
                    } else {
                        message.setText(Utils.colors(messages.getString("command-removed")));
                    }
                }
                player.spigot().sendMessage(size, message);
            }
        }
    }
}
