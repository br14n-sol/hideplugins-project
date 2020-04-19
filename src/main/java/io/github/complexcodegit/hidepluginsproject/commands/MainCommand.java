package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.Utils;
import io.github.complexcodegit.hidepluginsproject.handlers.GroupsHandler;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.SelectorManager;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MainCommand implements CommandExecutor {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public MainCommand(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String aliase, String[] args) {
        FileConfiguration groups = plugin.getGroups();
        FileConfiguration messages = plugin.getMessages();
        List<String> groupList = groupManager.getGroups();
        List<String> serverWorlds = groupManager.getWorlds();
        if(!command.getName().equalsIgnoreCase("hproject") || !(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;
        if(!player.hasPermission("hidepluginsproject.hproject") && !groupManager.getCommands(player, false).contains("/hproject")) {
            return false;
        }
        if(args.length == 0){
            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
        } else if(args.length == 1){
            /*
             /hproject help
             */
            if(args[0].equalsIgnoreCase("help")){
                messages.getStringList("helpPages.1").forEach(line -> player.sendMessage(Utils.colors(line)));
            }
            /*
             /hproject reload
             */
            else if(args[0].equalsIgnoreCase("reload")) {
                if(!SelectorManager.getGroups().containsKey(player.getUniqueId())){
                    plugin.reloadGroups();
                    plugin.reloadMessages();
                    plugin.reloadPlayers();
                    plugin.reloadConfig();
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("reloadConfig")));
                    GroupsHandler.read(plugin);
                    GroupManager.updateCmds();
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("loadGroups").replace("[GROUPS]", String.valueOf(GroupsHandler.getLoadGroups().size()))));
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupEndFirst").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                }
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                    if(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player) != null){
                        player.getInventory().removeItem(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player));
                    }
                    if(Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player) != null){
                        player.getInventory().removeItem(Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player));
                    }
                    if(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player) != null){
                        player.getInventory().removeItem(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player));
                    }
                    if(Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player) != null){
                        player.getInventory().removeItem(Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player));
                    }
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupFinish").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                    GroupsHandler.read(plugin);
                    groupManager.updateCmdGroup(SelectorManager.getGroups().get(player.getUniqueId()));
                    SelectorManager.getGroups().remove(player.getUniqueId());
                    SelectorManager.getWorlds().remove(player.getUniqueId());
                    SelectorManager.getGlobal().remove(player.getUniqueId());
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                }
            } else {
                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
            }
        } else if(args.length == 2){
            /*
             /hproject help <page>
             */
            if(args[0].equalsIgnoreCase("help")){
                List<String> pages = new ArrayList<>(messages.getConfigurationSection("helpPages").getKeys(false));
                if(pages.contains(args[1])){
                    for(String pag : messages.getStringList("helpPages."+args[1]))
                        player.sendMessage(Utils.colors(pag));
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("pageDoesNotExist")));
                }
            }
            /*
             /hproject select global
             */
            else if(args[0].equalsIgnoreCase("select") && args[1].equalsIgnoreCase("global")) {
                if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                    if(!SelectorManager.getGlobal().containsKey(player.getUniqueId())) {
                        if(groups.get("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".global", true) != null) {
                            if(SelectorManager.getWorlds().containsKey(player.getUniqueId())) {
                                player.sendMessage(plugin.getPrefix() + Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", SelectorManager.getWorlds().get(player.getUniqueId()))));
                                SelectorManager.getWorlds().remove(player.getUniqueId());
                            }
                            SelectorManager.getGlobal().put(player.getUniqueId(), "global");
                            player.sendMessage(plugin.getPrefix() + Utils.colors(messages.getString("globalSelected")));
                        } else {
                            player.sendMessage(plugin.getPrefix() + Utils.colors(messages.getString("globalNotExist").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix() + Utils.colors(messages.getString("globalAlreadySelected")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix() + Utils.colors(messages.getString("groupSelectFirst")));
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
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(SelectorManager.getWorlds().containsKey(player.getUniqueId()) || SelectorManager.getGlobal().containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("alreadyHaveBook")));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(SelectorManager.getWorlds().containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getWorlds().get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getGlobal().get(player.getUniqueId())));
                                    }
                                } else {
                                    if(SelectorManager.getWorlds().containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getWorlds().get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getGlobal().get(player.getUniqueId())));
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
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("haveNoSpaceInHotbar")));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("selectWorldOrGlobal")));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject add global
                 */
                else if(args[1].equalsIgnoreCase("global")) {
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(!groups.contains("groups."+SelectorManager.getGroups().get(player.getUniqueId())+".global", true)) {
                            SelectorManager.getGlobal().put(player.getUniqueId(), "global");
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("globalAdded").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                            groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".global.commands", "");
                            groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".global.tab", "");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("globalAlreadyExist")));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject remove <commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(SelectorManager.getWorlds().containsKey(player.getUniqueId()) || SelectorManager.getGlobal().containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("alreadyHaveBook")));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(SelectorManager.getWorlds().containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getWorlds().get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getGlobal().get(player.getUniqueId())));
                                    }
                                } else {
                                    if(SelectorManager.getWorlds().containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getWorlds().get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+SelectorManager.getGroups().get(player.getUniqueId()), "§eWorld: §r"+SelectorManager.getGlobal().get(player.getUniqueId())));
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
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("haveNoSpaceInHotbar")));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("selectWorldOrGlobal")));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            } else {
                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
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
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(SelectorManager.getGroups().get(player.getUniqueId()))){
                                if(groups.contains("groups."+SelectorManager.getGroups().get(player.getUniqueId())+".options.inheritances", true)){
                                    List<String> list = new ArrayList<>();
                                    if(!groups.getString("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.inheritances").equals("")){
                                        String inherit = groups.getString("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                        list.addAll(Arrays.asList(inherit.split(",")));
                                    }
                                    if(!list.contains(args[2])){
                                        list.add(args[2]);
                                        groups.set("groups."+SelectorManager.getGroups().get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritAdded").replace("[INHERIT]", args[2]).replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                                    } else {
                                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritAlreadyExist").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId())).replace("[INHERIT]", args[2])));
                                    }
                                } else {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritAdded").replace("[INHERIT]", args[2]).replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                                    groups.set("groups."+SelectorManager.getGroups().get(player.getUniqueId())+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritNotValid")));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject add world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(SelectorManager.getGroups().get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(SelectorManager.getWorlds().containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", SelectorManager.getWorlds().get(player.getUniqueId()))));
                                    SelectorManager.getWorlds().remove(player.getUniqueId());
                                }
                                if(SelectorManager.getGlobal().containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("globalDeselected")));
                                    SelectorManager.getGlobal().remove(player.getUniqueId());
                                }
                                SelectorManager.getWorlds().put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldAdded").replace("[WORLD]", args[2]).replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                                groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldAlreadyExist").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId())).replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldServerNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject remove inherit [inheritname]
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("inherit")){
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(SelectorManager.getGroups().get(player.getUniqueId()))){
                                if(groupManager.getInherit(SelectorManager.getGroups().get(player.getUniqueId())).contains(args[2])) {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritRemoved").replace("[INHERIT]", args[2]).replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                                    String inherit = groups.getString("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritNotValid")));
                                }
                            } else {
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("inheritNotValid")));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
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
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(SelectorManager.getGroups().get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(SelectorManager.getWorlds().containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldDeselected").replace("[WORLD]", SelectorManager.getWorlds().get(player.getUniqueId()))));
                                    SelectorManager.getWorlds().remove(player.getUniqueId());
                                }
                                if(SelectorManager.getGlobal().containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("globalDeselected")));
                                    SelectorManager.getGlobal().remove(player.getUniqueId());
                                }
                                SelectorManager.getWorlds().put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldSelected").replace("[WORLD]", args[2])));
                            } else {
                                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldNotExist").replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("worldServerNotExist").replace("[WORLD]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            SelectorManager.getGroups().put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelected").replace("[GROUP]", args[2])));
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupNotExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupEndFirst").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
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
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("helpSet").replace("[BOOLEAN]", args[2])));
                            groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.custom-help.enable", Boolean.valueOf(args[2]));
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("setNotValid")));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                }
                /*
                 /hproject set permission [permission]
                 */
                else if(args[1].equalsIgnoreCase("permission")){
                    if(SelectorManager.getGroups().containsKey(player.getUniqueId())){
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSetPermission").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId())).replace("[PERMISSION]", args[2])));
                        groups.set("groups." + SelectorManager.getGroups().get(player.getUniqueId()) + ".options.permission", args[2]);
                        plugin.saveGroups();
                        plugin.reloadGroups();
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupSelectFirst")));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            }
            /*
             /hproject create group [groupname]
             */
            else if(args[0].equalsIgnoreCase("create")) {
                if(args[1].equalsIgnoreCase("group")) {
                    if(!SelectorManager.getGroups().containsKey(player.getUniqueId())) {
                        if(!groupList.contains(args[2])) {
                            SelectorManager.getGroups().put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupCreated").replace("[GROUP]", args[2])));
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
                            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupAlreadyExist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("groupEndFirst").replace("[GROUP]", SelectorManager.getGroups().get(player.getUniqueId()))));
                    }
                } else {
                    player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
                }
            } else {
                player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
            }
        } else {
            player.sendMessage(plugin.getPrefix()+Utils.colors(messages.getString("commandDoesNotExist")));
        }
        return false;
    }
}
