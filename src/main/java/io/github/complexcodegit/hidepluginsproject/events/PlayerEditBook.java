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
        Player player = event.getPlayer();
        String bookName = event.getPreviousBookMeta().getDisplayName();
        if(bookName.equals("§aAdd Commands §8- §7HidePlugins Project") || bookName.equals("§aAdd Tabs §8- §7HidePlugins Project")) {
            List<String> content = event.getNewBookMeta().getPages();
            List<String> lore = event.getPreviousBookMeta().getLore();

            List<String> commands;

            List<String> contentCommands = new ArrayList<>();
            List<String> alreadyExist = new ArrayList<>();
            List<String> successfully = new ArrayList<>();
            List<String> notExist = new ArrayList<>();

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.getInventory().removeItem(Utils.getItem(bookName, player)),1);

            player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
            player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F);

            String[] nameParts = bookName.split(" ");
            String option = nameParts[1];
            assert lore != null;
            String group = lore.get(0).substring(11);
            String world = lore.get(1).substring(11);
            for(String cmds : content){
                contentCommands.addAll(Arrays.asList(cmds.replace(" ", "").split(",")));
            }
            if(world.equals("global")){
                if(option.equals("Tabs")){
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
            if(!successfully.isEmpty()){
                if(world.equals("global")){
                    if(option.equals("Tabs")){
                        plugin.getGroups().set("groups."+group+".global.tab", String.join(", ", commands));
                    } else {
                        plugin.getGroups().set("groups."+group+".global.commands", String.join(", ", commands));
                    }
                } else {
                    if(option.equals("Tabs")){
                        plugin.getGroups().set("groups."+group+".worlds."+world+".tab", String.join(", ", commands));
                    } else {
                        plugin.getGroups().set("groups."+group+".worlds."+world+".commands", String.join(", ", commands));
                    }
                }
                plugin.saveGroups();
                plugin.reloadGroups();
            }
            FileConfiguration messages = plugin.getMessages();
            if(!notExist.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, notExist);
                message.setText(Utils.colors(messages.getString("commandsNotValid")));
                player.spigot().sendMessage(size, message);
            }
            if(!alreadyExist.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, alreadyExist);
                message.setText(Utils.colors(messages.getString("commandsAlreadyExist")));
                player.spigot().sendMessage(size, message);
            }
            if(!successfully.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, successfully);
                message.setText(Utils.colors(messages.getString("commandsAdded")));
                player.spigot().sendMessage(size, message);
            }
        } else if(bookName.equals("§aRemove Commands §8- §7HidePlugins Project") || bookName.equals("§aRemove Tabs §8- §7HidePlugins Project")) {
            List<String> content = event.getNewBookMeta().getPages();
            List<String> lore = event.getPreviousBookMeta().getLore();

            List<String> commands;

            List<String> contentCommands = new ArrayList<>();
            List<String> alreadyExist = new ArrayList<>();
            List<String> successfully = new ArrayList<>();
            List<String> notExist = new ArrayList<>();

            Bukkit.getScheduler().runTaskLater(plugin, () -> player.getInventory().removeItem(Utils.getItem(bookName, player)),1);

            player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
            player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F);

            String[] nameParts = bookName.split(" ");
            String option = nameParts[1];
            assert lore != null;
            String group = lore.get(0).substring(11);
            String world = lore.get(1).substring(11);
            for(String cmds : content){
                contentCommands.addAll(Arrays.asList(cmds.replace(" ", "").split(",")));
            }
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
            for(String command : contentCommands) {
                if(Utils.checkCommand(command)) {
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
                } else {
                    if(!notExist.contains(command)) {
                        notExist.add(command);
                    }
                }
            }
            if(!successfully.isEmpty()){
                if(world.equals("global")){
                    if(option.equals("Tabs")){
                        plugin.getGroups().set("groups."+group+".global.tab", String.join(", ", commands));
                    } else {
                        plugin.getGroups().set("groups."+group+".global.commands", String.join(", ", commands));
                    }
                } else {
                    if(option.equals("Tabs")){
                        plugin.getGroups().set("groups."+group+".worlds."+world+".tab", String.join(", ", commands));
                    } else {
                        plugin.getGroups().set("groups."+group+".worlds."+world+".commands", String.join(", ", commands));
                    }
                }
                plugin.saveGroups();
                plugin.reloadGroups();
            }
            FileConfiguration messages = plugin.getMessages();
            if(!notExist.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, notExist);
                message.setText(Utils.colors(messages.getString("commandsNotValid")));
                player.spigot().sendMessage(size, message);
            }
            if(!alreadyExist.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, alreadyExist);
                message.setText(Utils.colors(messages.getString("commandsNotExist")));
                player.spigot().sendMessage(size, message);
            }
            if(!successfully.isEmpty()){
                TextComponent size = new TextComponent();
                TextComponent message = new TextComponent();
                set(size, successfully);
                message.setText(Utils.colors(messages.getString("commandsRemoved")));
                player.spigot().sendMessage(size, message);
            }
        }
    }

    public void set(TextComponent component, List<String> list){
        component.setText(plugin.prefix+"§7"+list.size()+" ");
        if(list.size() > 1){
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(String.join(", ", list)).create()));
        } else {
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(list.get(0)).create()));
        }
    }
}
