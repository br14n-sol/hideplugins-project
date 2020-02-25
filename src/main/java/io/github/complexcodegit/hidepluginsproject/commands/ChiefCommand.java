package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.InteractText;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ChiefCommand implements CommandExecutor {
    private HidePluginsProject plugin;

    public ChiefCommand(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(!command.getName().equalsIgnoreCase("hproject")){
            return false;
        }

        if(!(sender instanceof Player)){
            if(args.length == 0) {
                plugin.console.sendMessage(plugin.colors(plugin.prefix + "&cUse &f/hproject help"));
            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("help")){
                    plugin.console.sendMessage(plugin.colors(plugin.prefix + "&b/hproject reload &f| Reload the configuration file."));
                } else if(args[0].equalsIgnoreCase("reload")) {
                    plugin.console.sendMessage(plugin.colors(plugin.prefix + "&bAll files &awere reloaded successfully."));
                    plugin.reloadConfig();
                    plugin.reloadGroups();
                    plugin.reloadPlayers();
                } else {
                    plugin.console.sendMessage(plugin.colors(plugin.prefix + "&cThe command does not exist, try using &f/hproject help"));
                }
            } else {
                plugin.console.sendMessage(plugin.colors(plugin.prefix + "&cThe command does not exist, try using &f/hproject help"));
            }
            return false;
        }

        Player player = (Player)sender;
        List<String> hprojectGroup = GroupManager.getCommandsList(player, plugin);
        if(!player.hasPermission("hidepluginsproject.hproject") && !hprojectGroup.contains("/hproject")){
            return false;
        }

        if(args.length == 0){
            InteractText.send("useCommand", player, plugin);
        } else if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                player.sendMessage(plugin.colors(plugin.prefix + "&f&lAvailable commands:"));
                player.sendMessage(plugin.colors("&b/hproject reload &e<filename> &f| Reload the configuration file you specify."));
                player.sendMessage(plugin.colors("&fFiles: &eallfiles&f, &egroupsfile&f, &eplayersfile&f, &econfigfile&f."));
                player.sendMessage("");
                player.sendMessage(plugin.colors("&b/hproject groups &f| Show the list of available groups."));
                player.sendMessage(plugin.colors("&b/hproject group &e<groupname> &f| Show detailed group information."));
                player.sendMessage(plugin.colors("&b/hproject addcmd &e<groupname> <command> &f| Used to add commands to a group."));
                player.sendMessage(plugin.colors("&b/hproject addtab &e<groupname> <command> &f| Used to add commands to the tab of a group."));
                player.sendMessage(plugin.colors("&b/hproject sethelp &e<groupname> <true|false> &f| Activate or deactivate, the custom help in the indicated group."));
                player.sendMessage(plugin.colors("&b/hproject player &e<playername> &f| Shows you the information of a player."));
            } else if(args[0].equalsIgnoreCase("groups")){
                player.sendMessage("");
                player.sendMessage(plugin.colors(plugin.prefix + "&f&lGroups List:"));

                InteractText.send("listGroups", player, plugin);
            } else if(args[0].equalsIgnoreCase("reload")){
                player.sendMessage(plugin.colors(plugin.prefix + "&bAll files &awere reloaded successfully."));
                plugin.reloadConfig();
                plugin.reloadGroups();
                plugin.reloadPlayers();
            } else {
                InteractText.send("commandExistUse", player, plugin);
            }
        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("group")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                if(groups.contains(arg)){
                    if(args[1].equalsIgnoreCase(arg)){
                        player.sendMessage("");
                        player.sendMessage(plugin.colors(plugin.prefix + "&b&l"+ arg + " &f&lgroup information."));
                        if(arg.equalsIgnoreCase("default")){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lPermission: &bYou don't need permission."));
                        } else {
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lPermission: &b" + GroupManager.getGroupPermission(arg)));
                        }
                        player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lOnline members: &b" + GroupManager.getMembersGroup(arg , plugin)));
                        player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lCommands List:"));

                        List<String> list = GroupManager.getGroupCommandsList(arg, plugin);
                        String result = String.join("&f, &b", list);

                        player.sendMessage(plugin.colors("&b" + result));
                    }
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup not exist."));
                }
            } else if(args[0].equalsIgnoreCase("player")){
                String arg = args[1];
                List<String> historyPlayers = new ArrayList<>();
                for(String users : plugin.getPlayers().getConfigurationSection("players").getKeys(false)){
                    if(!historyPlayers.contains(users)){
                        historyPlayers.add(users);
                    }
                }
                if(historyPlayers.contains(arg)){
                    if(args[1].equalsIgnoreCase(arg)){
                        player.sendMessage("");
                        player.sendMessage(plugin.colors(plugin.prefix + "&b&l"+ arg + " &f&lplayer information."));
                        if(player.isOp()){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lGroup: &bAn operator does not need a group."));
                        } else {
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lGroup: &b" + GroupManager.getPlayerGroup(Bukkit.getPlayer(arg) , plugin)));
                        }
                        player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lReports: &b" + plugin.getPlayers().getString("players." + arg + ".reports")));
                        if(plugin.getPlayers().getString("players." + arg + ".last-command") == null){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lLast command used: &bNo registered commands."));
                        } else {
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &f&lLast command used: &b" + plugin.getPlayers().getString("players." + arg + ".last-command")));
                            if(plugin.getConfig().getBoolean("player-command-history")){
                                InteractText.sendUser("playerHistory", arg, player, plugin);
                            }
                        }
                    }
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&b&l" + arg + " &cplayer was not found."));
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(args[1].equalsIgnoreCase("playersfile")) {
                    player.sendMessage(plugin.colors(plugin.prefix + "&aThe &bPlayers &afile was reloaded correctly."));
                    plugin.reloadPlayers();
                } else if(args[1].equalsIgnoreCase("groupsfile")) {
                    player.sendMessage(plugin.colors(plugin.prefix + "&aThe &bGroups &afile was reloaded correctly."));
                    plugin.reloadGroups();
                } else if(args[1].equalsIgnoreCase("configfile")) {
                    player.sendMessage(plugin.colors(plugin.prefix + "&aThe &bConfig &afile was reloaded correctly."));
                    plugin.reloadConfig();
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b&l" + args[1] + " &cfile does not exist, please try again."));
                }
            } else {
                InteractText.send("commandExistUse", player, plugin);
            }
        } else if(args.length == 3){
            if(args[0].equalsIgnoreCase("addcmd")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                String cmd = args[2];
                if(groups.contains(arg)){
                    List<String> groupCmds = GroupManager.getGroupCommandsList(arg, plugin);
                    if(!groupCmds.contains(cmd)){
                        List<String> blacklist = new ArrayList<>();
                        for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
                            if(plugins != null){
                                if(!blacklist.contains(plugins.getName())){
                                    blacklist.add(plugins.getName());
                                }
                            }
                        }
                        blacklist.add("Aliases");
                        blacklist.add("Bukkit");
                        blacklist.add("Minecraft");
                        blacklist.add("hprojectinternal");
                        blacklist.add("hpp");
                        blacklist.add("hpj");

                        List<String> cmds = new ArrayList<>();
                        for(HelpTopic cmdLabel : plugin.getServer().getHelpMap().getHelpTopics()){
                            if(!cmds.contains(cmdLabel.getName().replaceAll("/", ""))){
                                cmds.add(cmdLabel.getName().replaceAll("/", ""));
                            }
                        }

                        for(int i=0; i < blacklist.size(); i++){
                            if(cmds.contains(blacklist.get(i))){
                                cmds.remove(blacklist.get(i));
                            }
                        }

                        List<String> groupCommands = GroupManager.getGroupCommandsList(arg, plugin);
                        for(int i=0; i < groupCommands.size(); i++){
                            if(cmds.contains(groupCommands.get(i))){
                                cmds.remove(groupCommands.get(i));
                            }
                        }

                        if(cmds.contains(cmd)){
                            groupCmds.add(cmd);
                            plugin.getGroups().set("groups." + arg + ".commands", groupCmds);
                            plugin.saveGroups();
                            player.sendMessage(plugin.colors(plugin.prefix + "&aThe &b" + cmd + " &acommand was successfully added to the &b" + arg + " &agroup."));
                        } else {
                            player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + cmd + " &ccommand does not exist, please verify that it is well written."));
                        }
                    } else {
                        player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup already has the &b" + cmd + " &ccommand."));
                    }
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup not exist."));
                }
            } else if(args[0].equalsIgnoreCase("sethelp")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                String swich = args[2];
                if(groups.contains(arg)){
                    if(swich.equalsIgnoreCase("true")){
                        plugin.getGroups().set("groups." + arg + ".custom-help.enable", true);
                        plugin.saveGroups();
                        player.sendMessage(plugin.colors(plugin.prefix + "&aThe custom help was &benabled &afor the &b" + arg + " &agroup correctly."));
                    } else if(swich.equalsIgnoreCase("false")){
                        plugin.getGroups().set("groups." + arg + ".custom-help.enable", false);
                        plugin.saveGroups();
                        player.sendMessage(plugin.colors(plugin.prefix + "&aThe custom help was &bdisabled &afor the &b" + arg + " &agroup correctly."));
                    } else {
                        player.sendMessage(plugin.colors(plugin.prefix + "&b" + swich + " &cis not valid please use (true or false)."));
                    }
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup not exist."));
                }
            } else if(args[0].equalsIgnoreCase("addtab")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                String cmd = args[2];
                if(groups.contains(arg)){
                    List<String> groupTabs = GroupManager.getTabList(arg, plugin);
                    if(!groupTabs.contains(cmd)){
                        List<String> blacklist = new ArrayList<>();
                        for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
                            if(plugins != null){
                                if(!blacklist.contains(plugins.getName())){
                                    blacklist.add(plugins.getName());
                                }
                            }
                        }
                        blacklist.add("Aliases");
                        blacklist.add("Bukkit");
                        blacklist.add("Minecraft");
                        blacklist.add("hprojectinternal");
                        blacklist.add("hpp");
                        blacklist.add("hpj");

                        List<String> cmds = new ArrayList<>();
                        for(HelpTopic cmdLabel : plugin.getServer().getHelpMap().getHelpTopics()){
                            if(!cmds.contains(cmdLabel.getName().replaceAll("/", ""))){
                                cmds.add(cmdLabel.getName().replaceAll("/", ""));
                            }
                        }

                        for(int i=0; i < blacklist.size(); i++){
                            if(cmds.contains(blacklist.get(i))){
                                cmds.remove(blacklist.get(i));
                            }
                        }

                        List<String> groupCommands = GroupManager.getTabList(arg, plugin);
                        for(int i=0; i < groupCommands.size(); i++){
                            if(cmds.contains(groupCommands.get(i))){
                                cmds.remove(groupCommands.get(i));
                            }
                        }

                        if(cmds.contains(cmd)){
                            groupTabs.add(cmd);
                            plugin.getGroups().set("groups." + arg + ".tab-completes", groupTabs);
                            plugin.saveGroups();
                            player.sendMessage(plugin.colors(plugin.prefix + "&aThe &b" + cmd + " &atab was successfully added to the &b" + arg + " &agroup."));
                        } else {
                            player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + cmd + " &ccommand does not exist, please verify that it is well written."));
                        }
                    } else {
                        player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup already has the &b" + cmd + " &ctab."));
                    }
                } else {
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup not exist."));
                }
            } else {
                InteractText.send("commandExistUse", player, plugin);
            }
        } else {
            InteractText.send("commandExistUse", player, plugin);
        }
        return false;
    }
}
