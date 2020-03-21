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

    @SuppressWarnings({"unused", "ConstantConditions"})
    @EventHandler
    public void editCheck(PlayerEditBookEvent event){
        Player player = event.getPlayer();
        if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Commands §8- §7HidePlugins Project") || event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project")){
            List<String> content = event.getNewBookMeta().getPages();
            List<String> lore = event.getPreviousBookMeta().getLore();

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.getInventory().removeItem(Utils.getItem(event.getPreviousBookMeta().getDisplayName(), player)),1);
            player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
            player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 2);
            Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 4);

            List<String> commands;
            if(lore.get(1).substring(11).equals("global")){
                if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project")){
                    commands = groupManager.getGlobalTab(lore.get(0).substring(11));
                } else {
                    commands = groupManager.getGlobalCommands(lore.get(0).substring(11));
                }
            } else {
                if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project")){
                    commands = groupManager.getWorldTab(lore.get(0).substring(11), lore.get(1).substring(11));
                } else {
                    commands = groupManager.getWorldCommands(lore.get(0).substring(11), lore.get(1).substring(11));
                }
            }

            List<String> contentCommands = new ArrayList<>();
            List<String> alreadyExist = new ArrayList<>();
            List<String> successfully = new ArrayList<>();
            List<String> notExist = new ArrayList<>();

            for(String cmds : content){
                contentCommands.addAll(Arrays.asList(cmds.replace(" ", "").split(",")));
            }
            for(String command : contentCommands) {
                if(Utils.checkCommand(command)) {
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
                    if(!notExist.contains(command)) {
                        notExist.add(command);
                    }
                }
            }

            if(lore.get(1).substring(11).equals("global")){
                if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project")){
                    plugin.getGroups().set("groups."+lore.get(0).substring(11)+".global.tab", String.join(", ", commands));
                } else {
                    plugin.getGroups().set("groups."+lore.get(0).substring(11)+".global.commands", String.join(", ", commands));
                }
            } else {
                if(event.getPreviousBookMeta().getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project")){
                    plugin.getGroups().set("groups."+lore.get(0).substring(11)+".worlds."+lore.get(1).substring(11)+".tabs", String.join(", ", commands));
                } else {
                    plugin.getGroups().set("groups."+lore.get(0).substring(11)+".worlds."+lore.get(1).substring(11)+".commands", String.join(", ", commands));
                }
            }
            plugin.saveGroups();
            plugin.reloadGroups();

            TextComponent size = new TextComponent();
            TextComponent message = new TextComponent();
            if(!notExist.isEmpty()){
                size.setText(plugin.prefix+"§7"+notExist.size());
                if(notExist.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", notExist)).create()));
                    message.setText(" §ccommands are not valid.");
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(notExist.get(0)).create()));
                    message.setText(" §ccommand is invalid.");
                }
                player.spigot().sendMessage(size, message);
            }
            if(!alreadyExist.isEmpty()){
                size.setText(plugin.prefix+"§7"+alreadyExist.size());
                if(alreadyExist.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", alreadyExist)).create()));
                    message.setText(" §ecommands were already configured.");
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(alreadyExist.get(0)).create()));
                    message.setText(" §ecommand was already configured.");
                }
                player.spigot().sendMessage(size, message);
            }
            if(!successfully.isEmpty()){
                size.setText(plugin.prefix+"§7"+successfully.size());
                if(successfully.size() > 1){
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", successfully)).create()));
                    message.setText(" §acommands added successfully.");
                } else {
                    size.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(successfully.get(0)).create()));
                    message.setText(" §acommand was successfully added.");
                }
                player.spigot().sendMessage(size, message);
            }
        }
    }
}
