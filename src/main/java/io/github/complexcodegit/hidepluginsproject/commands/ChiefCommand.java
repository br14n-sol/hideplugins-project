package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.LanguageManager;
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
                    plugin.reloadLanguages();
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
            player.sendMessage(LanguageManager.internalTranslate("command-use", plugin));
        } else if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                player.sendMessage(LanguageManager.internalTranslate("commands.help.title", plugin));
                List<String> lines = LanguageManager.internalTranslateList("commands.help.lines", plugin);
                for(String line : lines){
                    player.sendMessage(plugin.colors(line));
                }
            } else if(args[0].equalsIgnoreCase("groups")){
                List<String> groups = GroupManager.getGroups(plugin);
                player.sendMessage(LanguageManager.internalTranslate("commands.groups.title", plugin));
                for(String group : groups) {
                    player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.groups.format-base", plugin).replaceAll("%group%", group));
                }
                player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.groups.more-information", plugin));
            } else if(args[0].equalsIgnoreCase("reload")){
                player.sendMessage(LanguageManager.internalTranslate("commands.reload.allfiles", plugin));
                plugin.reloadConfig();
                plugin.reloadGroups();
                plugin.reloadPlayers();
                plugin.reloadLanguages();
            } else {
                player.sendMessage(LanguageManager.internalTranslate("command-not-exist", plugin));
            }
        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("group")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                if(groups.contains(arg)){
                    if(args[1].equalsIgnoreCase(arg)){
                        player.sendMessage(LanguageManager.internalTranslate("commands.group.title", plugin).replaceAll("%group%", arg));
                        if(arg.equalsIgnoreCase("default")){
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.group.default", plugin));
                        } else {
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.group.not-default", plugin).replaceAll("%permission%", GroupManager.getGroupPermission(arg)));
                        }
                        player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.group.online-members", plugin).replaceAll("%online%", String.valueOf(GroupManager.getMembersGroup(arg , plugin))));

                        if(!plugin.getGroups().getStringList("groups."+arg+".inheritance").isEmpty()){
                            List<String> inheritances = GroupManager.getInheritances(arg, plugin);
                            String results = String.join("&f, &e", inheritances);
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.group.inheritances", plugin)+plugin.colors(" &e" + results));
                        }

                        player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.group.commands-list", plugin));
                        List<String> list = GroupManager.getGroupCommandsList(arg, plugin);
                        String result = String.join("&f, &b", list);
                        player.sendMessage(plugin.colors("&b" + result));
                    }
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("group-not-exist", plugin).replaceAll("%group%", arg));
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
                        player.sendMessage(LanguageManager.internalTranslate("commands.player.title", plugin).replaceAll("%player%", arg));
                        if(player.isOp()){
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.player.op", plugin));
                        } else {
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.player.not-op", plugin).replaceAll("%group%", GroupManager.getPlayerGroup(Bukkit.getPlayer(arg) , plugin)));
                        }
                        player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.player.reports", plugin).replaceAll("%reports%", String.valueOf(plugin.getPlayers().getInt("players." + arg + ".reports"))));
                        if(plugin.getPlayers().getString("players." + arg + ".last-command").equals("none")){
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.player.no-last-command", plugin));
                        } else {
                            player.sendMessage(LanguageManager.internalTranslateNoPrefix("commands.player.last-command", plugin).replaceAll("%command%", plugin.getPlayers().getString("players." + arg + ".last-command")));
                            if(plugin.getConfig().getBoolean("player-command-history")){
                                InteractText.sendUser("playerHistory", arg, player, plugin);
                            }
                        }
                    }
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("commands.player.player-not-exist", plugin).replaceAll("%player%", arg));
                }
            } else if(args[0].equalsIgnoreCase("reload")) {
                if(args[1].equalsIgnoreCase("playersfile")) {
                    plugin.reloadPlayers();
                } else if(args[1].equalsIgnoreCase("groupsfile")) {
                    plugin.reloadGroups();
                } else if(args[1].equalsIgnoreCase("configfile")) {
                    plugin.reloadConfig();
                } else if(args[1].equalsIgnoreCase("langsfile")) {
                    plugin.reloadLanguages();
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("commands.reload.file-not-exist", plugin).replaceAll("%file%", args[1]));
                    return false;
                }
                player.sendMessage(LanguageManager.internalTranslate("commands.reload.format-base", plugin).replaceAll("%file%", args[1]));
            } else {
                player.sendMessage(LanguageManager.internalTranslate("command-not-exist", plugin));
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

                        for(String s : blacklist){
                            cmds.remove(s);
                        }

                        List<String> groupCommands = GroupManager.getGroupCommandsList(arg, plugin);
                        for(String s : groupCommands){
                            cmds.remove(s);
                        }

                        if(cmds.contains(cmd)){
                            groupCmds.add(cmd);
                            plugin.getGroups().set("groups." + arg + ".commands", groupCmds);
                            plugin.saveGroups();
                            player.sendMessage(LanguageManager.internalTranslate("commands.addcmd.command-add", plugin).replaceAll("%command%", cmd).replaceAll("%group%", arg));
                        } else {
                            player.sendMessage(LanguageManager.internalTranslate("commands.addcmd.command-not-exist", plugin).replaceAll("%command%", cmd));
                        }
                    } else {
                        player.sendMessage(LanguageManager.internalTranslate("commands.addcmd.registered-command", plugin).replaceAll("%group%", arg).replaceAll("%command%", cmd));
                    }
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("group-not-exist", plugin).replaceAll("%group%", arg));
                }
            } else if(args[0].equalsIgnoreCase("sethelp")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                String swich = args[2];
                if(groups.contains(arg)){
                    if(swich.equalsIgnoreCase("true")){
                        plugin.getGroups().set("groups." + arg + ".custom-help.enable", true);
                        plugin.saveGroups();
                        player.sendMessage(LanguageManager.internalTranslate("commands.sethelp.enable-help", plugin).replaceAll("%group%", arg));
                    } else if(swich.equalsIgnoreCase("false")){
                        plugin.getGroups().set("groups." + arg + ".custom-help.enable", false);
                        plugin.saveGroups();
                        player.sendMessage(LanguageManager.internalTranslate("commands.sethelp.disable-help", plugin).replaceAll("%group%", arg));
                    } else {
                        player.sendMessage(LanguageManager.internalTranslate("commands.sethelp.invalid-option", plugin).replaceAll("%option%", swich));
                    }
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("group-not-exist", plugin).replaceAll("%group%", arg));
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

                        for(String s : blacklist){
                            cmds.remove(s);
                        }

                        List<String> groupCommands = GroupManager.getTabList(arg, plugin);
                        for(String s : groupCommands){
                            cmds.remove(s);
                        }

                        if(cmds.contains(cmd)){
                            groupTabs.add(cmd);
                            plugin.getGroups().set("groups." + arg + ".tab-completes", groupTabs);
                            plugin.saveGroups();
                            player.sendMessage(LanguageManager.internalTranslate("commands.addtab.command-add", plugin).replaceAll("%command%", cmd).replaceAll("%group%", arg));
                        } else {
                            player.sendMessage(LanguageManager.internalTranslate("commands.addtab.command-not-exist", plugin).replaceAll("%command%", cmd));
                        }
                    } else {
                        player.sendMessage(LanguageManager.internalTranslate("commands.addtab.registered-command", plugin).replaceAll("%group%", arg).replaceAll("%command%", cmd));
                    }
                } else {
                    player.sendMessage(LanguageManager.internalTranslate("group-not-exist", plugin).replaceAll("%group%", arg));
                }
            } else {
                player.sendMessage(LanguageManager.internalTranslate("command-not-exist", plugin));
            }
        } else {
            player.sendMessage(LanguageManager.internalTranslate("command-not-exist", plugin));
        }
        return false;
    }
}
