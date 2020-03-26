package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
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
    private HashMap<UUID, String> selectGroup = new HashMap<>();
    private HashMap<UUID, String> selectWorld = new HashMap<>();
    private HashMap<UUID, String> selectGlobal = new HashMap<>();
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public ChiefCommand(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
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
                for(String line : messages.getStringList("help-pages.1"))
                    player.sendMessage(Utils.colors(line));
            }
            /*
             /hproject reload
             */
            else if(args[0].equalsIgnoreCase("reload")) {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("reload-config")));
                plugin.reloadGroups();
                plugin.reloadMessages();
                plugin.reloadPlayers();
                plugin.reloadConfig();
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(selectGroup.containsKey(player.getUniqueId())) {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-finish").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    selectGroup.remove(player.getUniqueId());
                    selectWorld.remove(player.getUniqueId());
                    selectGlobal.remove(player.getUniqueId());
                } else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
            }
        }
        else if(args.length == 2){
            /*
             /hproject help <page>
             */
            if(args[0].equalsIgnoreCase("help")){
                List<String> pages = new ArrayList<>(messages.getConfigurationSection("help-pages").getKeys(false));
                if(pages.contains(args[1])){
                    for(String pag : messages.getStringList("help-pages."+args[1]))
                        player.sendMessage(Utils.colors(pag));
                } else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("page-does-not-exist")));
                }
            }
            /*
             /hproject select <global>
             */
            else if(args[0].equalsIgnoreCase("select")) {
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!selectGlobal.containsKey(player.getUniqueId())) {
                            if(groups.get("groups." + selectGroup.get(player.getUniqueId()) + ".global", true) != null) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix + Utils.colors(messages.getString("world-deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                selectGlobal.put(player.getUniqueId(), "global");
                                player.sendMessage(plugin.prefix + Utils.colors(messages.getString("global-selected")));
                            } else {
                                player.sendMessage(plugin.prefix + Utils.colors(messages.getString("global-not-exist").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                            }
                        } else {
                            player.sendMessage(plugin.prefix + Utils.colors(messages.getString("global-already-selected")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix + Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
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
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aAdd Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aAdd Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+messages.getString("already-have-book"));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(selectWorld.containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
                                    }
                                } else {
                                    if(selectWorld.containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aAdd Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
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
                                player.sendMessage(plugin.prefix+messages.getString("have-no-space-in-hotbar"));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                /*
                 /hproject add global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!groups.contains("groups."+selectGroup.get(player.getUniqueId())+".global", true)) {
                            selectGlobal.put(player.getUniqueId(), "global");
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("global-added").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", "");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", "");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("global-already-exist")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            /*
             /hproject remove <commands, tabs>
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("commands") || args[1].equalsIgnoreCase("tabs")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())) {
                            if(Utils.getItem("§aRemove Commands §8- §7HidePlugins Project", player) != null ||
                                    Utils.getItem("§aRemove Tabs §8- §7HidePlugins Project", player) != null){
                                player.sendMessage(plugin.prefix+messages.getString("already-have-book"));
                                return false;
                            }

                            int slot = Utils.slotFree(player);
                            if(slot != -1){
                                if(args[1].equalsIgnoreCase("commands")){
                                    if(selectWorld.containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Commands §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
                                    }
                                } else {
                                    if(selectWorld.containsKey(player.getUniqueId())){
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectWorld.get(player.getUniqueId())));
                                    } else {
                                        player.getInventory().setItem(slot, Utils.createBook("§aRemove Tabs §8- §7HidePlugins Project", "§eGroup: §r"+selectGroup.get(player.getUniqueId()), "§eWorld: §r"+selectGlobal.get(player.getUniqueId())));
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
                                player.sendMessage(plugin.prefix+messages.getString("have-no-space-in-hotbar"));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
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
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))){
                                if(groups.contains("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", true)){
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    if(!list.contains(args[2])){
                                        list.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-added").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    } else {
                                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-already-exist").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[INHERIT]", args[2])));
                                    }
                                } else {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-added").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-not-valid")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                /*
                 /hproject add world [worldname]
                 */
                if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("global-deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-added").replace("[WORLD]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-already-exist").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            /*
             /hproject remove inherit [inheritname]
             */
            else if(args[0].equalsIgnoreCase("remove")){
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))){
                                if(groupManager.getInherit(selectGroup.get(player.getUniqueId())).contains(args[2])) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit.removed").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-not-valid")));
                                }
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("inherit-not-valid")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
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
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("global-deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-selected").replace("[WORLD]", args[2])));
                            } else {
                                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-not-exist")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("world-server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-selected").replace("[GROUP]", args[2])));
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-end-first").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            /*
             /hproject set help [true, false]
             */
            else if(args[0].equalsIgnoreCase("set")) {
                if(args[1].equalsIgnoreCase("help")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("help-set").replace("[BOOLEAN]", args[2])));
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.custom-help.enable", Boolean.valueOf(args[2]));
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("help-set-not-valid")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-select-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            /*
             /hproject create group [groupname]
             */
            else if(args[0].equalsIgnoreCase("create")) {
                if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(!groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-created").replace("[GROUP]", args[2])));
                            groups.set("groups." + args[2] + ".options.inheritances", "");
                            groups.set("groups." + args[2] + ".options.custom-help.enable", true);
                            groups.createSection("groups."+args[2]+".options.custom-help.worlds");
                            groups.createSection("groups."+args[2]+".worlds");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-already-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+Utils.colors(messages.getString("group-end-first").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
            }
        }
        else {
            player.sendMessage(plugin.prefix+Utils.colors(messages.getString("command-does-not-exist")));
        }
        return false;
    }
}
