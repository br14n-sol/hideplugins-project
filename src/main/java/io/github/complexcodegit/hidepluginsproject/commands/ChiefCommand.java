package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.SelectorManager;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class ChiefCommand implements CommandExecutor {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    private SelectorManager selectorManager;
    public ChiefCommand(HidePluginsProject plugin, GroupManager groupManager, SelectorManager selectorManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
        this.selectorManager = selectorManager;
    }

    @SuppressWarnings({"NullableProblems", "ConstantConditions"})
    public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args) {
        FileConfiguration groups = plugin.getGroups();
        FileConfiguration messages = plugin.getMessages();
        List<String> groupList = groupManager.getGroups();
        List<String> serverWorlds = groupManager.getWorlds();
        if(!cmd.getName().equalsIgnoreCase("hproject") || !(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        if(!player.hasPermission("hidepluginsproject.hproject") && !groupManager.getCommands(player, false).contains("/hproject")) {
            return false;
        }

        if(args.length == 1){
            /*
             /hproject help
             */
            if(args[0].equalsIgnoreCase("help")){
                for(String line : messages.getStringList("helpPages.1"))
                    player.sendMessage(Utils.colors(line));
            }
            /*
             /hproject reload
             */
            else if(args[0].equalsIgnoreCase("reload")) {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("reloadConfig")));
                plugin.reloadGroups();
                plugin.reloadMessages();
                plugin.reloadPlayers();
                plugin.reloadConfig();
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(selectorManager.containsGroup(player)) {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupFinish").replace("[GROUP]", selectorManager.getGroup(player))));
                    selectorManager.removeGroup(player);
                    selectorManager.removeWorld(player);
                    selectorManager.removeGlobal(player);
                } else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
            }
        }
        else if(args.length == 2){
            /*
             /hproject help <page>
             */
            if(args[0].equalsIgnoreCase("help")){
                List<String> pages = new ArrayList<>(messages.getConfigurationSection("helpPages").getKeys(false));
                if(pages.contains(args[1])){
                    for(String pag : messages.getStringList("helpPages."+args[1]))
                        player.sendMessage(Utils.colors(pag));
                } else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("pageDoesNotExist")));
                }
            }
            /*
             /hproject select global
             */
            else if(args[0].equalsIgnoreCase("select") && args[1].equalsIgnoreCase("global")) {
                if(selectorManager.containsGroup(player)) {
                    if(!selectorManager.containsGlobal(player)) {
                        if(groups.get("groups." + selectorManager.getGroup(player) + ".global", true) != null) {
                            if(selectorManager.containsWorld(player)) {
                                player.sendMessage(plugin.prefix + Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", selectorManager.getWorld(player))));
                                selectorManager.removeWorld(player);
                            }
                            selectorManager.putGlobal(player, "global");
                            player.sendMessage(plugin.prefix + Utils.colors(messages.getString("globalSelected")));
                        } else {
                            player.sendMessage(plugin.prefix + Utils.colors(messages.getString("globalNotExist").replace("[GROUP]", selectorManager.getGroup(player))));
                        }
                    } else {
                        player.sendMessage(plugin.prefix + Utils.colors(messages.getString("globalAlreadySelected")));
                    }
                } else {
                    player.sendMessage(plugin.prefix + Utils.colors(messages.getString("groupSelectFirst")));
                }
            }
            /*
             /hproject add <global, commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add <commands, tabs>
                 */
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(selectorManager.containsGroup(player)) {
                        if(selectorManager.containsWorld(player) || selectorManager.containsGlobal(player)) {
                            if(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("alreadyHaveBook")));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(selectorManager.containsWorld(player)){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getWorld(player)));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getGlobal(player)));
                                    }
                                } else {
                                    if(selectorManager.containsWorld(player)){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getWorld(player)));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getGlobal(player)));
                                    }
                                }
                                if(player.getInventory().getHeldItemSlot() == slot){
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
                                        player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
                                    }, 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 3);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 5);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 7);
                                }
                            } else {
                                player.sendMessage(plugin.prefix+messages.getString("haveNoSpaceInHotbar"));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("selectWorldOrGlobal")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject add global
                 */
                else if(args[1].equalsIgnoreCase("global")) {
                    if(selectorManager.containsGroup(player)) {
                        if(!groups.contains("groups."+selectorManager.getGroup(player)+".global", true)) {
                            selectorManager.putGlobal(player, "global");
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("globalAdded").replace("[GROUP]", selectorManager.getGroup(player))));
                            groups.set("groups." + selectorManager.getGroup(player) + ".global.commands", "");
                            groups.set("groups." + selectorManager.getGroup(player) + ".global.tab", "");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("globalAlreadyExist")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject remove <commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(selectorManager.containsGroup(player)) {
                        if(selectorManager.containsWorld(player) || selectorManager.containsGlobal(player)) {
                            if(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("alreadyHaveBook")));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(selectorManager.containsWorld(player)){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getWorld(player)));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getGlobal(player)));
                                    }
                                } else {
                                    if(selectorManager.containsWorld(player)){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getWorld(player)));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectorManager.getGroup(player), "§eWorld: §r"+selectorManager.getGlobal(player)));
                                    }
                                }
                                if(player.getInventory().getHeldItemSlot() == slot){
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                        player.spawnParticle(Particle.CRIT_MAGIC, Utils.getHandLocation(player), 20);
                                        player.spawnParticle(Particle.CRIT, Utils.getHandLocation(player), 20);
                                    }, 4);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 3);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 5);
                                    Bukkit.getScheduler().runTaskLater(plugin, () -> player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 100F, 100F), 7);
                                }
                            } else {
                                player.sendMessage(plugin.prefix+messages.getString("haveNoSpaceInHotbar"));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("selectWorldOrGlobal")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
            }
        }
        else if(args.length == 3) {
            /*
             /hproject add <inherit, world> [inheritname, worldname]
             */
            if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add inherit [inheritname]
                 */
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectorManager.containsGroup(player)) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectorManager.getGroup(player))){
                                if(groups.contains("groups."+selectorManager.getGroup(player)+".options.inheritances", true)){
                                    List<String> list = new ArrayList<>();
                                    if(!groups.getString("groups." + selectorManager.getGroup(player) + ".options.inheritances").equals("")){
                                        String inherit = groups.getString("groups." + selectorManager.getGroup(player) + ".options.inheritances").replace(" ", "");
                                        list.addAll(Arrays.asList(inherit.split(",")));
                                    }
                                    if(!list.contains(args[2])){
                                        list.add(args[2]);
                                        groups.set("groups."+selectorManager.getGroup(player)+".options.inheritances", String.join(", ", list));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritAdded").replace("[INHERIT]", args[2]).replace("[GROUP]", selectorManager.getGroup(player))));
                                    } else {
                                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritAlreadyExist").replace("[GROUP]", selectorManager.getGroup(player)).replace("[INHERIT]", args[2])));
                                    }
                                } else {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritAdded").replace("[INHERIT]", args[2]).replace("[GROUP]", selectorManager.getGroup(player))));
                                    groups.set("groups."+selectorManager.getGroup(player)+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritNotValid")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject add world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(selectorManager.containsGroup(player)) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectorManager.getGroup(player));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(selectorManager.containsWorld(player)) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", selectorManager.getWorld(player))));
                                    selectorManager.removeWorld(player);
                                }
                                if(selectorManager.containsGlobal(player)) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("globalDeselected")));
                                    selectorManager.removeGlobal(player);
                                }
                                selectorManager.putWorld(player, args[2]);
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldAdded").replace("[WORLD]", args[2]).replace("[GROUP]", selectorManager.getGroup(player))));
                                groups.set("groups." + selectorManager.getGroup(player) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + selectorManager.getGroup(player) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldAlreadyExist").replace("[GROUP]", selectorManager.getGroup(player)).replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldServerNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject remove inherit [inheritname]
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectorManager.containsGroup(player)) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectorManager.getGroup(player))){
                                if(groupManager.getInherit(selectorManager.getGroup(player)).contains(args[2])) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritRemoved").replace("[INHERIT]", args[2]).replace("[GROUP]", selectorManager.getGroup(player))));
                                    String inherit = groups.getString("groups." + selectorManager.getGroup(player) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + selectorManager.getGroup(player) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritNotValid")));
                                }
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inheritNotValid")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject select <world, group> [worldname, groupname]
             */
            else if(args[0].equalsIgnoreCase("select")) {
                /*
                 /hproject select world [worldname]
                 */
                if(args[1].equalsIgnoreCase("world")) {
                    if(selectorManager.containsGroup(player)) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectorManager.getGroup(player));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(selectorManager.containsWorld(player)) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", selectorManager.getWorld(player))));
                                    selectorManager.removeWorld(player);
                                }
                                if(selectorManager.containsGlobal(player)) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("globalDeselected")));
                                    selectorManager.removeGlobal(player);
                                }
                                selectorManager.putWorld(player, args[2]);
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldSelected").replace("[WORLD]", args[2])));
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldNotExist").replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("worldServerNotExist").replace("[WORLD]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!selectorManager.containsGroup(player)) {
                        if(groupList.contains(args[2])) {
                            selectorManager.putGroup(player, args[2]);
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelected").replace("[GROUP]", args[2])));
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupEndFirst").replace("[GROUP]", selectorManager.getGroup(player))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject set <help, permission> <[true, false], [permission]>
             */
            else if(args[0].equalsIgnoreCase("set")) {
                /*
                 /hproject set help [true, false]
                 */
                if(args[1].equalsIgnoreCase("help")) {
                    if(selectorManager.containsGroup(player)) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("helpSet").replace("[BOOLEAN]", args[2])));
                            groups.set("groups." + selectorManager.getGroup(player) + ".options.custom-help.enable", Boolean.valueOf(args[2]));
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("setNotValid")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject set permission [permission]
                 */
                else if(args[1].equalsIgnoreCase("permission")){
                    if(selectorManager.containsGroup(player)){
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSetPermission").replace("[GROUP]", selectorManager.getGroup(player)).replace("[PERMISSION]", args[2])));
                        groups.set("groups." + selectorManager.getGroup(player) + ".options.permission", args[2]);
                        plugin.saveGroups();
                        plugin.reloadGroups();
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject create group [groupname]
             */
            else if(args[0].equalsIgnoreCase("create")) {
                if(args[1].equalsIgnoreCase("group")) {
                    if(!selectorManager.containsGroup(player)) {
                        if(!groupList.contains(args[2])) {
                            selectorManager.putGroup(player, args[2]);
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupCreated").replace("[GROUP]", args[2])));
                            groups.set("groups." + args[2] + ".options.inheritances", "");
                            groups.set("groups." + args[2] + ".options.permission", "hidepluginsproject.group."+args[2]);
                            groups.set("groups." + args[2] + ".options.custom-help.enable", true);
                            groups.createSection("groups."+args[2]+".options.custom-help.worlds");
                            groups.set("groups."+args[2]+".global.commands", "");
                            groups.set("groups."+args[2]+".global.tab", "");
                            groups.createSection("groups."+args[2]+".worlds");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupAlreadyExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("groupEndFirst").replace("[GROUP]", selectorManager.getGroup(player))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
            }
        }
        else {
            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("commandDoesNotExist")));
        }
        return false;
    }
}
