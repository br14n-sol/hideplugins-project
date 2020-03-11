package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.CommandChecker;
import io.github.complexcodegit.hidepluginsproject.utils.Messages;
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
    private Messages message;
    public ChiefCommand(HidePluginsProject plugin, GroupManager groupManager, Messages messages){
        this.plugin = plugin;
        this.groupManager = groupManager;
        this.message = messages;
    }

    @SuppressWarnings("NullableProblems")
    public boolean onCommand(CommandSender sender, Command cmd, String aliases, String[] args) {
        FileConfiguration groups = plugin.getGroups();
        List<String> groupList = groupManager.getGroups();
        List<String> serverWorlds = groupManager.getWorlds();
        if(!cmd.getName().equalsIgnoreCase("hproject")) {
            return false;
        }

        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        List<String> hprojectGroup = groupManager.getCommands(player);
        if(!player.hasPermission("hidepjpremium.hproject") && !hprojectGroup.contains("/hproject")) {
            return false;
        }

        if(selectGroup.containsKey(player.getUniqueId())) {
            List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")){
                    player.sendMessage("");
                    player.sendMessage("§7Configuration files reloaded successfully.");
                    plugin.reloadGroups();
                    plugin.reloadLanguages();
                    plugin.reloadPlayers();
                    plugin.reloadConfig();
                } else if(args[0].equalsIgnoreCase("finish")) {
                    player.sendMessage("");
                    player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group has been finalized.");
                    selectGroup.remove(player.getUniqueId());
                    selectWorld.remove(player.getUniqueId());
                    selectGlobal.remove(player.getUniqueId());
                } else {
                    message.commandNotExist(player);
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("remove")){
                    if(args[1].equalsIgnoreCase("global")){
                        if(groups.contains("groups."+selectGroup.get(player.getUniqueId())+".global")) {
                            if(selectGlobal.containsKey(player.getUniqueId())){
                                message.selectGlobalMsg(player);
                                selectGlobal.remove(player.getUniqueId());
                            }
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu was successfully removed from the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                            groups.set("groups."+selectGroup.get(player.getUniqueId())+".global", null);
                            plugin.saveGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group does not own the §cglobal §7submenu.");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("select")) {
                    if(args[1].equalsIgnoreCase("global")) {
                        if(!selectGlobal.containsKey(player.getUniqueId())) {
                            if(selectWorld.containsKey(player.getUniqueId())) {
                                message.selectWorldMsg(player, selectWorld.get(player.getUniqueId()));
                                selectWorld.remove(player.getUniqueId());
                            }
                            selectGlobal.put(player.getUniqueId(), "global");
                            message.globalMessages(player, "select");
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7You have already selected the §eglobal §7submenu.");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    if(args[1].equalsIgnoreCase("global")) {
                        if(!groups.contains("groups." + selectGroup.get(player.getUniqueId()) + ".global")) {
                            selectGlobal.put(player.getUniqueId(), "global");
                            message.globalMessages(player, "create");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", "");
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", "");
                            plugin.saveGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §eglobal §7submenu already exists in the group, to select it use §e/hproject select global");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("create")) {
                    if(args[1].equalsIgnoreCase("group")) {
                        player.sendMessage("");
                        player.sendMessage("§7Before creating another group, first end with the §e" + selectGroup.get(player.getUniqueId()) + " §7group, using §e/hproject finish");
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    if(args[1].equalsIgnoreCase("world")) {
                        String world = args[2];
                        if(serverWorlds.contains(world)) {
                            if(!groupWorlds.contains(world)) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    message.selectWorldMsg(player, selectWorld.get(player.getUniqueId()));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    message.selectGlobalMsg(player);
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), world);
                                message.worldMessages(player, "set", world);
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".tab", "");
                                plugin.saveGroups();
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group already has the world §c" + world + "§7.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + world + " §7world does not exist on the server.");
                        }
                    } else if(args[1].equalsIgnoreCase("customhelp")) {
                        String value = args[2];
                        if(value.equals("true") || value.equals("false")) {
                            player.sendMessage("");
                            player.sendMessage("§7The custom help was configured correctly as: §e" + value);
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.custom-help.enable", value);
                            plugin.saveGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The option you entered is not valid.");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("select")) {
                    if(args[1].equalsIgnoreCase("world")) {
                        String world = args[2];
                        if(serverWorlds.contains(world)) {
                            if(groupWorlds.contains(world)) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    message.selectWorldMsg(player, selectWorld.get(player.getUniqueId()));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    message.selectGlobalMsg(player);
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), world);
                                message.worldMessages(player, "select", world);
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The group does not have the §c" + world + " §7world, create it using the command §e/hproject create world " + world);
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + world + " §7world does not exist on the server.");
                        }
                    } else if(args[1].equalsIgnoreCase("group")) {
                        player.sendMessage("");
                        player.sendMessage("§7Before selecting another group, first end with the §e" + selectGroup.get(player.getUniqueId()) + " §7group, using §e/hproject finish");
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("add")) {
                    if(args[1].equalsIgnoreCase("inherit")){
                        String group = args[2];
                        if(groupList.contains(group)){
                            if(!group.equals(selectGroup.get(player.getUniqueId()))){
                                if(groups.contains("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances")){
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e"+group+" §7group was successfully added to the inheritance of the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                    String inherit = Objects.requireNonNull(groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances")).replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.add(group);
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e"+group+" §7group was successfully added to the inheritance of the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", group);
                                    plugin.saveGroups();
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7You cannot inherit the same group you have selected.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + group + " §7group does not exist.");
                        }
                    } else if(selectWorld.containsKey(player.getUniqueId())) {
                        if(args[1].equalsIgnoreCase("command")) {
                            String world = selectWorld.get(player.getUniqueId());
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(!groupManager.getGroupWorldCommands(selectGroup.get(player.getUniqueId()), world).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7command was successfully added to the §e" + world + " §7world.");
                                    List<String> commands = groupManager.getGroupWorldCommands(selectGroup.get(player.getUniqueId()), world);
                                    commands.add(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".commands", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7command is already defined in the §c" + world + " §7world.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else if(args[1].equalsIgnoreCase("tab")) {
                            String world = selectWorld.get(player.getUniqueId());
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(!groupManager.getGroupWorldTab(selectGroup.get(player.getUniqueId()), world).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7tab was successfully added to the §e" + world + " §7world.");
                                    List<String> commands = groupManager.getGroupWorldTab(selectGroup.get(player.getUniqueId()), world);
                                    commands.add(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".tab", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7tab is already defined in the §c" + world + " §7world.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            message.commandNotExist(player);
                        }
                    } else if(selectGlobal.containsKey(player.getUniqueId())) {
                        if(args[1].equalsIgnoreCase("command")) {
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(!groupManager.getGroupGlobalCommands(selectGroup.get(player.getUniqueId())).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7command was successfully added to the §eglobal §submenu.");
                                    List<String> commands = groupManager.getGroupGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    commands.add(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7command is already defined in the §cglobal §7submenu.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else if(args[1].equalsIgnoreCase("tab")) {
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(!groupManager.getGroupGlobalTab(selectGroup.get(player.getUniqueId())).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7tab was successfully added to the §eglobal §7submenu.");
                                    List<String> commands = groupManager.getGroupGlobalTab(selectGroup.get(player.getUniqueId()));
                                    commands.add(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7tab is already defined in the §cglobal §7submenu.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            message.commandNotExist(player);
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a world or the global submenu to use these commands, to select one use §e/hproject select <world, global> [groupname]");
                    }
                } else if(args[0].equalsIgnoreCase("remove")) {
                    if(args[1].equalsIgnoreCase("inherit")){
                        String group = args[2];
                        if(groupList.contains(group)){
                            if(!group.equals(selectGroup.get(player.getUniqueId()))){
                                if(groupManager.getInherit(selectGroup.get(player.getUniqueId())).contains(group)){
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e"+group+" §7group is no longer inherited by the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                    String inherit = Objects.requireNonNull(groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances")).replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(group);
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
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
                            player.sendMessage("§7The §c" + group + " §7group does not exist.");
                        }
                    } else if(args[1].equalsIgnoreCase("world")){
                        String world = args[2];
                        if(serverWorlds.contains(world)) {
                            if(groupWorlds.contains(world)){
                                if(selectWorld.containsKey(player.getUniqueId())){
                                    message.selectWorldMsg(player, world);
                                    selectWorld.remove(player.getUniqueId());
                                }
                                player.sendMessage("");
                                player.sendMessage("§7The §e"+world+" §7world was successfully removed from the §e"+selectGroup.get(player.getUniqueId())+" §7group.");
                                groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+world, null);
                                plugin.saveGroups();
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §e" + selectGroup.get(player.getUniqueId()) + " §7group does not own the §c" + world + "§7world.");
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + world + " §7world does not exist on the server.");
                        }
                    } else if(selectWorld.containsKey(player.getUniqueId())) {
                        if(args[1].equalsIgnoreCase("command")) {
                            String world = selectWorld.get(player.getUniqueId());
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(groupManager.getGroupWorldCommands(selectGroup.get(player.getUniqueId()), world).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7command was successfully removed from the §e" + world + " §7world.");
                                    List<String> commands = groupManager.getGroupWorldCommands(selectGroup.get(player.getUniqueId()), world);
                                    commands.remove(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".commands", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7command is not defined in the §c" + world + " §7world.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else if(args[1].equalsIgnoreCase("tab")) {
                            String world = selectWorld.get(player.getUniqueId());
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(groupManager.getGroupWorldTab(selectGroup.get(player.getUniqueId()), world).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7tab was successfully removed from the §e" + world + " §7world.");
                                    List<String> commands = groupManager.getGroupWorldTab(selectGroup.get(player.getUniqueId()), world);
                                    commands.remove(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + world + ".tab", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7tab is not defined in the §c" + world + " §7world.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            message.commandNotExist(player);
                        }
                    } else if(selectGlobal.containsKey(player.getUniqueId())) {
                        if(args[1].equalsIgnoreCase("command")) {
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(groupManager.getGroupGlobalCommands(selectGroup.get(player.getUniqueId())).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7command was successfully removed from the §eglobal §7submenu.");
                                    List<String> commands = groupManager.getGroupGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    commands.remove(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7command is not defined in the §cglobal §7submenu.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else if(args[1].equalsIgnoreCase("tab")) {
                            String command = args[2];
                            if(CommandChecker.checkCommand(command)) {
                                if(groupManager.getGroupGlobalTab(selectGroup.get(player.getUniqueId())).contains(command)) {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §e" + command + " §7tab was successfully removed from the §eglobal §7submenu.");
                                    List<String> commands = groupManager.getGroupGlobalTab(selectGroup.get(player.getUniqueId()));
                                    commands.remove(command);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", String.join(", ", commands));
                                    plugin.saveGroups();
                                } else {
                                    player.sendMessage("");
                                    player.sendMessage("§7The §c" + command + " §7tab is not defined in the §cglobal §7submenu.");
                                }
                            } else {
                                player.sendMessage("");
                                player.sendMessage("§7The §c" + command + " §7command does not exist, please verify that it is spelled correctly.");
                            }
                        } else {
                            message.commandNotExist(player);
                        }
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§7You must select a world or the global submenu to use these commands, to select one use §e/hproject select <world, global> [groupname]");
                    }
                } else {
                    message.commandNotExist(player);
                }
            } else {
                message.commandNotExist(player);
            }
        } else {
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reload")){
                    player.sendMessage("");
                    player.sendMessage("§7Configuration files reloaded successfully.");
                    plugin.reloadGroups();
                    plugin.reloadLanguages();
                    plugin.reloadPlayers();
                    plugin.reloadConfig();
                } else {
                    message.commandNotExist(player);
                }
            } else if(args.length == 3) {
                if(args[0].equalsIgnoreCase("create")) {
                    if(args[1].equalsIgnoreCase("group")) {
                        String group = args[2];
                        if(!groupList.contains(group)) {
                            selectGroup.put(player.getUniqueId(), group);
                            message.groupMessages(player, "create", group);
                            groups.set("groups." + group + ".options.default", false);
                            groups.set("groups." + group + ".options.inheritances", "");
                            groups.set("groups." + group + ".options.custom-help.enable", true);
                            plugin.saveGroups();
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + group + " §7group already exists.");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("select")) {
                    if(args[1].equalsIgnoreCase("group")) {
                        String group = args[2];
                        if(groupList.contains(group)) {
                            selectGroup.put(player.getUniqueId(), group);
                            message.groupMessages(player, "select", group);
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§7The §c" + group + " §7group does not exist.");
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else if(args[0].equalsIgnoreCase("remove")){
                    if(args[1].equalsIgnoreCase("group")){
                        String group = args[2];
                        if(groupList.contains(group)){
                            player.sendMessage("");
                            player.sendMessage("§7The §e"+group+" §7group was successfully deleted.");
                            groups.set("groups."+group, null);
                            plugin.saveGroups();
                        }
                    } else {
                        message.commandNotExist(player);
                    }
                } else {
                    message.commandNotExist(player);
                }
            } else {
                message.commandNotExist(player);
            }
        }
        return false;
    }
}
