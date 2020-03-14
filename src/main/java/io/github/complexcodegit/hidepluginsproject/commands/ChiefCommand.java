package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.CommandChecker;
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
        List<String> groupList = groupManager.getGroups();
        List<String> serverWorlds = groupManager.getWorlds();
        if(!cmd.getName().equalsIgnoreCase("hproject") || !(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        List<String> hprojectGroup = groupManager.getCommands(player);
        if(!player.hasPermission("hidepluginsproject.hproject") && !hprojectGroup.contains("/hproject")) {
            return false;
        }

        if(args.length > 0 && args.length < 4) {
            /*
             /hproject reload
             */
            if(args[0].equalsIgnoreCase("reload")) {
                player.sendMessage("");
                player.sendMessage("§7Configuration files reloaded successfully.");
                plugin.reloadGroups();
                plugin.reloadLanguages();
                plugin.reloadPlayers();
                plugin.reloadConfig();
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(selectGroup.containsKey(player.getUniqueId())) {
                    player.sendMessage("");
                    player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group has been finalized.");
                    selectGroup.remove(player.getUniqueId());
                    selectWorld.remove(player.getUniqueId());
                    selectGlobal.remove(player.getUniqueId());
                } else {
                    player.sendMessage("");
                    player.sendMessage("§7There is no group to finish, select one first.");
                }
            }
            /*
             /hproject select <global, world, group> [worldname, groupname]
             */
            else if(args[0].equalsIgnoreCase("select")) {
                /*
                 /hproject select global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!selectGlobal.containsKey(player.getUniqueId())) {
                            if(selectWorld.containsKey(player.getUniqueId())) {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + selectWorld.get(player.getUniqueId()) + " §7world is no longer selected.");
                                selectWorld.remove(player.getUniqueId());
                            }
                            selectGlobal.put(player.getUniqueId(), "global");
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu was selected correctly.");
                            player.sendMessage("");
                            player.sendMessage("§7Available commands:");
                            player.sendMessage("§e/hproject add command [command] §7- To add a command to the global.");
                            player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the global.");
                            player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the global.");
                            player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the global.");
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You already have the global submenu selected!");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject select world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + selectWorld.get(player.getUniqueId()) + " §7world is no longer selected.");
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if (selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §cglobal §7submenu is no longer selected.");
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + args[2] + " §7world was selected successfully.");
                                player.sendMessage("");
                                player.sendMessage("§7Available commands:");
                                player.sendMessage("§e/hproject add command [command] §7- To add a command to the world.");
                                player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the world.");
                                player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the world.");
                                player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the world.");
                                player.sendMessage("§e/hproject select world [worldname] §7- To select another world.");
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The group does not have the §c" + args[2] + " §7world.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7world does not exist on the server.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage("");
                            player.sendMessage("§7The §e" + args[2] + " §7group has been selected.");
                            player.sendMessage("");
                            player.sendMessage("§7Available commands:");
                            player.sendMessage("§e/hproject set world [worldname] §7- To add a world to the group.");
                            player.sendMessage("§e/hproject select world [worldname] §7- To select a world to the group.");
                            player.sendMessage("§e/hproject finish §7- To finish editing the group.");
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7group does not exist.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7Before selecting another group, first end with the §e" + selectGroup.get(player.getUniqueId()) + " §7group.");
                    }
                }
                else {
                    player.sendMessage("");
                    player.sendMessage("§7The command you entered does not exist.");
                }
            }
            /*
             /hproject remove <global, group, inherit, world, command, tab> [groupname, inheritname, worldname, command]
             */
            else if(args[0].equalsIgnoreCase("remove")) {
                /*
                 /hproject remove global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groups.contains("groups." + selectGroup.get(player.getUniqueId()) + ".global", true)) {
                            if(selectGlobal.containsKey(player.getUniqueId())) {
                                player.sendMessage("");
                                player.sendMessage("§7The §cglobal §7submenu is no longer selected.");
                                selectGlobal.remove(player.getUniqueId());
                            }
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu was successfully removed from the §e" + selectGroup.get(player.getUniqueId()) + " §7group.");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global", null);
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group does not own the §cglobal §7submenu.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject remove group [groupname}
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(groupList.contains(args[2])) {
                        if(args[2].equals(selectGroup.get(player.getUniqueId()))) {
                            selectGlobal.remove(player.getUniqueId());
                            selectWorld.remove(player.getUniqueId());
                            selectGroup.remove(player.getUniqueId());
                        }
                        player.sendMessage("");
                        player.sendMessage("§7The §e" + args[2] + " §7group was successfully deleted.");
                        groups.set("groups." + args[2], null);
                        plugin.saveGroups();
                        plugin.reloadGroups();
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7The §c" + args[2] + " §7group does not exist, check that it is well written.");
                    }
                }
                /*
                 /hproject remove inherit [inheritname}
                 */
                else if(args[1].equalsIgnoreCase("inherit")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))) {
                                if(groupManager.getInherit(selectGroup.get(player.getUniqueId())).contains(args[2])) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + args[2] + " §7group is no longer inherited by the §e" + selectGroup.get(player.getUniqueId()) + " §7group.");
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The inheritance you entered is not valid.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7You cannot inherit the same group you have selected.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7group does not exist.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject remove world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + selectWorld.get(player.getUniqueId()) + " §7world is no longer selected.");
                                    selectWorld.remove(player.getUniqueId());
                                }
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + args[2] + " §7world was successfully removed from the §e" + selectGroup.get(player.getUniqueId()) + " §7group.");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2], null);
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group does not own the §c" + args[2] + "§7world.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7world does not exist on the server.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject remove command [command]
                 */
                else if(args[1].equalsIgnoreCase("command")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())){
                            if(CommandChecker.checkCommand(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    List<String> commands = groupManager.getWorldCommands(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7command was successfully removed from the §e"+selectWorld.get(player.getUniqueId())+" §7world.");
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7command is not defined in the §c"+selectWorld.get(player.getUniqueId())+" §7world.");
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7command was successfully removed from the §eglobal §7submenu.");
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7command is not defined in the §cglobal §7submenu.");
                                    }
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c"+args[2]+" §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You must select a world or the global submenu first.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject remove tab [command]
                 */
                else if(args[1].equalsIgnoreCase("tab")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())){
                            if(CommandChecker.checkCommand(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    List<String> commands = groupManager.getWorldCommands(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7tab was successfully removed from the §e"+selectWorld.get(player.getUniqueId())+" §7world.");
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7tab is not defined in the §c"+selectWorld.get(player.getUniqueId())+" §7world.");
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7tab was successfully removed from the §eglobal §7submenu.");
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7tab is not defined in the §cglobal §7submenu.");
                                    }
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c"+args[2]+" §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You must select a world or the global submenu first.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                else {
                    player.sendMessage("");
                    player.sendMessage("§7The command you entered does not exist.");
                }
            }
            /*
             /hproject set <global, world, customhelp> [worldname, [true, false]]
             */
            else if(args[0].equalsIgnoreCase("set")) {
                /*
                 /hproject set global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(!groups.contains("groups."+selectGroup.get(player.getUniqueId())+".global", true)) {
                            selectGlobal.put(player.getUniqueId(), "global");
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu was created correctly.");
                            player.sendMessage("");
                            player.sendMessage("§7Available commands:");
                            player.sendMessage("§e/hproject add command [command] §7- To add a command to the global.");
                            player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the global.");
                            player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the global.");
                            player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the global.");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", "");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", "");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu already exists in the group, to select it use §e/hproject select global");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject set world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + selectWorld.get(player.getUniqueId()) + " §7world is no longer selected.");
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §cglobal §7submenu is no longer selected.");
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + args[2] + " §7world was set successfully.");
                                player.sendMessage("");
                                player.sendMessage("§7Available commands:");
                                player.sendMessage("§e/hproject add command [command] §7- To add a command to the world.");
                                player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the world.");
                                player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the world.");
                                player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the world.");
                                player.sendMessage("§e/hproject select world [worldname] §7- To select another world.");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group already has the world §c" + args[2] + "§7.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7world does not exist on the server.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject set customhelp [true, false]
                 */
                else if(args[1].equalsIgnoreCase("customhelp")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage("");
                            player.sendMessage("§7The custom help was configured correctly as: §e" + args[2]);
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.custom-help.enable", args[2]);
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The option you entered is not valid.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                else {
                    player.sendMessage("");
                    player.sendMessage("§7The command you entered does not exist.");
                }
            }
            /*
             /hproject create <group> [groupname]
             */
            else if(args[0].equalsIgnoreCase("create")) {
                /*
                 /hproject create group [groupname]
                 */
                if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(!groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage("");
                            player.sendMessage("§7The §e" + args[2] + " §7group was created successfully.");
                            player.sendMessage("");
                            player.sendMessage("§7Available commands:");
                            player.sendMessage("§e/hproject set world [worldname] §7- To add a world to the group.");
                            player.sendMessage("§e/hproject select world [worldname] §7- To select a world to the group.");
                            player.sendMessage("§e/hproject finish §7- To finish editing the group.");
                            groups.set("groups." + args[2] + ".options.default", false);
                            groups.set("groups." + args[2] + ".options.inheritances", "");
                            groups.set("groups." + args[2] + ".options.custom-help.enable", true);
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + args[2] + " §7group already exists.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7Before creating another group, first end with the §e" + selectGroup.get(player.getUniqueId()) + " §7group.");
                    }
                }
                else {
                    player.sendMessage("");
                    player.sendMessage("§7The command you entered does not exist.");
                }
            }
            /*
             /hproject add <inherit, command, tab> [inheritname, command]
             */
            else if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add inherit [inheritname]
                 */
                if(args[1].equalsIgnoreCase("inherit")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])){
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))){
                                if(groups.contains("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", true)){
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e"+args[2]+" §7group was successfully added to the inheritance of the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.add(args[2]);
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e"+args[2]+" §7group was successfully added to the inheritance of the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7You cannot inherit the same group you have selected.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c"+args[2]+" §7group does not exist.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject add command [command]
                 */
                else if(args[1].equalsIgnoreCase("command")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())){
                            if(CommandChecker.checkCommand(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    List<String> commands = groupManager.getWorldCommands(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7command was successfully added to the §e"+selectWorld.get(player.getUniqueId())+" §7world.");
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+"§7command is already defined in the §c"+selectWorld.get(player.getUniqueId())+" §7world.");
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e" + args[2] + " §7command was successfully added to the §eglobal §submenu.");
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7command is already defined in the §cglobal §7submenu.");
                                    }
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c"+args[2]+" §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You must select a world or the global submenu first.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                /*
                 /hproject add tab [command]
                 */
                else if(args[1].equalsIgnoreCase("tab")){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(selectWorld.containsKey(player.getUniqueId()) || selectGlobal.containsKey(player.getUniqueId())){
                            if(CommandChecker.checkCommand(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    List<String> commands = groupManager.getWorldCommands(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7tab was successfully added to the §e"+selectWorld.get(player.getUniqueId())+" §7world.");
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7tab is already defined in the §c"+selectWorld.get(player.getUniqueId())+" §7world.");
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §e"+args[2]+" §7tab was successfully added to the §eglobal §7submenu.");
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage("");
                                        player.sendMessage("§7The §c"+args[2]+" §7tab is already defined in the §cglobal §7submenu.");
                                    }
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c"+args[2]+" §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You must select a world or the global submenu first.");
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a group first.");
                    }
                }
                else {
                    player.sendMessage("");
                    player.sendMessage("§7The command you entered does not exist.");
                }
            }
            else {
                player.sendMessage("");
                player.sendMessage("§7The command you entered does not exist.");
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§7The command you entered does not exist.");
        }
        return false;
    }
}
