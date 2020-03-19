package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.LanguageManager;
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
    private LanguageManager languageManager;
    public ChiefCommand(HidePluginsProject plugin, GroupManager groupManager, LanguageManager languageManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
        this.languageManager = languageManager;
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
             /hproject help <page>
             */
            if(args[0].equalsIgnoreCase("help")){
                if(!selectGroup.containsKey(player.getUniqueId())){
                    if(args.length == 1){
                        List<String> lines = languageManager.getList("commands.help.no-group-selected");
                        for(String line : lines)
                            player.sendMessage(plugin.colors(line));
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                    }
                } else {
                    if(args.length == 1){
                        List<String> lines = languageManager.getList("commands.help.group-selected.1");
                        for(String line : lines)
                            player.sendMessage(plugin.colors(line));
                    } else if(args.length == 2){
                        if(args[1].equalsIgnoreCase("1")){
                            List<String> lines1 = languageManager.getList("commands.help.group-selected.1");
                            for(String line : lines1)
                                player.sendMessage(plugin.colors(line));
                        } else if(args[1].equalsIgnoreCase("2")){
                            List<String> lines2 = languageManager.getList("commands.help.group-selected.2");
                            for(String line : lines2)
                                player.sendMessage(plugin.colors(line));
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.page-not-exist")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                    }
                }
            }
            /*
             /hproject reload
             */
            else if(args[0].equalsIgnoreCase("reload")) {
                if(args.length == 1){
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.reload")));
                    plugin.reloadGroups();
                    plugin.reloadLanguages();
                    plugin.reloadPlayers();
                    plugin.reloadConfig();
                } else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject finish
             */
            else if(args[0].equalsIgnoreCase("finish")) {
                if(args.length == 1){
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.finish").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                        selectGroup.remove(player.getUniqueId());
                        selectWorld.remove(player.getUniqueId());
                        selectGlobal.remove(player.getUniqueId());
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                } else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
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
                    if(args.length == 2){
                        if(selectGroup.containsKey(player.getUniqueId())) {
                            if(!selectGlobal.containsKey(player.getUniqueId())) {
                                if(groups.get("groups." + selectGroup.get(player.getUniqueId()) + ".global", true) != null) {
                                    if(selectWorld.containsKey(player.getUniqueId())) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                        selectWorld.remove(player.getUniqueId());
                                    }
                                    selectGlobal.put(player.getUniqueId(), "global");
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.selected")));
                                } else {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.not-exist").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.already-selected")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
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
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.selected").replace("[WORLD]", args[2])));
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.not-exist")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                /*
                 /hproject select group [groupname]
                 */
                else if(args[1].equalsIgnoreCase("group")) {
                    if(!selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            selectGroup.put(player.getUniqueId(), args[2]);
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.selected").replace("[GROUP]", args[2])));
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.first-ends-group").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject remove <global, group, inherit, world, command, tab> [groupname, inheritname, worldname, command]
             */
            else if(args[0].equalsIgnoreCase("remove")) {
                /*
                 /hproject remove inherit [inheritname}
                 */
                if(args[1].equalsIgnoreCase("inherit")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(groupList.contains(args[2])) {
                            if(!args[2].equals(selectGroup.get(player.getUniqueId()))) {
                                if(groupManager.getInherit(selectGroup.get(player.getUniqueId())).contains(args[2])) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.remove").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    String inherit = groups.getString("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances").replace(" ", "");
                                    List<String> list = new ArrayList<>(Arrays.asList(inherit.split(",")));
                                    list.remove(args[2]);
                                    groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.inheritances", String.join(", ", list));
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                } else {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.not-valid")));
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.you-can-not-inherit")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
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
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.remove-from-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.not-defined-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.remove-from-global").replace("[CMD]", args[2])));
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.not-defined-global").replace("[CMD]", args[2])));
                                    }
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.not-exist").replace("[CMD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
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
                                    List<String> commands = groupManager.getWorldTab(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.remove-from-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("tab.command.not-defined-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalTab(selectGroup.get(player.getUniqueId()));
                                    if(commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.remove-from-global").replace("[CMD]", args[2])));
                                        commands.remove(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.not-defined-global").replace("[CMD]", args[2])));
                                    }
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.not-exist").replace("[CMD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject set <help> [true, false]
             */
            else if(args[0].equalsIgnoreCase("set")) {
                /*
                 /hproject set help [true, false]
                 */
                if(args[1].equalsIgnoreCase("help")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        if(args[2].equals("true") || args[2].equals("false")) {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.custom-help.set").replace("[BOOLEAN]", args[2])));
                            groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".options.custom-help.enable", Boolean.valueOf(args[2]));
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.custom-help.not-valid")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
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
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.set").replace("[GROUP]", args[2])));
                            groups.set("groups." + args[2] + ".options.inheritances", "");
                            groups.set("groups." + args[2] + ".options.custom-help.enable", true);
                            groups.createSection("groups."+args[2]+".options.custom-help.worlds");
                            groups.createSection("groups."+args[2]+".worlds");
                            plugin.saveGroups();
                            plugin.reloadGroups();
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.already-set").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.first-ends-group").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            /*
             /hproject add <inherit, command, tab, global, world> [inheritname, command, worldname]
             */
            else if(args[0].equalsIgnoreCase("add")){
                /*
                 /hproject add global
                 */
                if(args[1].equalsIgnoreCase("global")) {
                    if(args.length == 2){
                        if(selectGroup.containsKey(player.getUniqueId())) {
                            if(!groups.contains("groups."+selectGroup.get(player.getUniqueId())+".global", true)) {
                                selectGlobal.put(player.getUniqueId(), "global");
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.set").replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".global.tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.already-set")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                    }
                }
                /*
                 /hproject add world [worldname]
                 */
                else if(args[1].equalsIgnoreCase("world")) {
                    if(selectGroup.containsKey(player.getUniqueId())) {
                        List<String> groupWorlds = groupManager.getGroupWorlds(selectGroup.get(player.getUniqueId()));
                        if(serverWorlds.contains(args[2])) {
                            if(!groupWorlds.contains(args[2])) {
                                if(selectWorld.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.deselected").replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    selectWorld.remove(player.getUniqueId());
                                }
                                if(selectGlobal.containsKey(player.getUniqueId())) {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.global.deselected")));
                                    selectGlobal.remove(player.getUniqueId());
                                }
                                selectWorld.put(player.getUniqueId(), args[2]);
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.set").replace("[WORLD]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".commands", "");
                                groups.set("groups." + selectGroup.get(player.getUniqueId()) + ".worlds." + args[2] + ".tab", "");
                                plugin.saveGroups();
                                plugin.reloadGroups();
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.already-set").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[WORLD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.world.server-not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
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
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.add").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.already-add").replace("[GROUP]", selectGroup.get(player.getUniqueId())).replace("[INHERIT]", args[2])));
                                    }
                                } else {
                                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.add").replace("[INHERIT]", args[2]).replace("[GROUP]", selectGroup.get(player.getUniqueId()))));
                                    groups.set("groups."+selectGroup.get(player.getUniqueId())+".options.inheritances", args[2]);
                                    plugin.saveGroups();
                                    plugin.reloadGroups();
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.inherit.you-can-not-inherit")));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.group.not-exist").replace("[GROUP]", args[2])));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
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
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.add-from-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.already-defined-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalCommands(selectGroup.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.add-from-global").replace("[CMD]", args[2])));
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.commands", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.already-defined-global").replace("[CMD]", args[2])));
                                    }
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.not-exist").replace("[CMD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
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
                                    List<String> commands = groupManager.getWorldTab(selectGroup.get(player.getUniqueId()), selectWorld.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.add-from-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".worlds."+selectWorld.get(player.getUniqueId())+".tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.already-defined-world").replace("[CMD]", args[2]).replace("[WORLD]", selectWorld.get(player.getUniqueId()))));
                                    }
                                } else {
                                    List<String> commands = groupManager.getGlobalTab(selectGroup.get(player.getUniqueId()));
                                    if(!commands.contains(args[2])) {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.add-from-global").replace("[CMD]", args[2])));
                                        commands.add(args[2]);
                                        groups.set("groups."+selectGroup.get(player.getUniqueId())+".global.tab", String.join(", ", commands));
                                        plugin.saveGroups();
                                        plugin.reloadGroups();
                                    } else {
                                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.already-defined-global").replace("[CMD]", args[2])));
                                    }
                                }
                            } else {
                                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.not-exist").replace("[CMD]", args[2])));
                            }
                        } else {
                            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.tab.select-world-or-global")));
                        }
                    } else {
                        player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.select-group-first")));
                    }
                }
                else {
                    player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
                }
            }
            else {
                player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
            }
        } else {
            player.sendMessage(plugin.prefix+plugin.colors(languageManager.getInternal("commands.command-not-exist")));
        }
        return false;
    }
}
