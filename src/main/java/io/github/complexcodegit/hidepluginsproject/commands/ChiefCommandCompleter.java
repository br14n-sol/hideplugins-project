package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChiefCommandCompleter implements TabCompleter {
    private HidePluginsProject plugin;

    public ChiefCommandCompleter(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String string, String[] args) {
        Player player = (Player)sender;
        List<String> hprojectGroup = GroupManager.getCommandsList(player, plugin);
        if(player.hasPermission("hidepluginsproject.hproject") || hprojectGroup.contains("/hproject")) {
            if(args.length == 1){
                ArrayList<String> commands = new ArrayList<>();
                ArrayList<String> cmds = new ArrayList<>();
                commands.add("help"); commands.add("groups"); commands.add("group"); commands.add("reload");
                commands.add("player"); commands.add("addcmd"); commands.add("addtab"); commands.add("sethelp");
                if(!args[0].equals("")){
                    for(int i=0; i < commands.size(); i++){
                        if(commands.get(i).toLowerCase().startsWith(args[0].toLowerCase())){
                            cmds.add(commands.get(i));
                        }
                    }
                } else {
                    cmds.addAll(commands);
                }
                Collections.sort(cmds);
                return cmds;
            } else if(args.length == 2){
                List<String> groups = GroupManager.getGroups(plugin);
                if(args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("addcmd") ||
                        args[0].equalsIgnoreCase("addtab") || args[0].equalsIgnoreCase("sethelp")){
                    ArrayList<String> cmds = new ArrayList<>();
                    if(!args[1].equals("")){
                        for(int i=0; i < groups.size(); i++){
                            if(groups.get(i).toLowerCase().startsWith(args[1].toLowerCase())){
                                cmds.add(groups.get(i));
                            }
                        }
                    } else {
                        cmds.addAll(groups);
                    }
                    Collections.sort(cmds);
                    return cmds;
                }
                if(args[0].equalsIgnoreCase("player")){
                    ArrayList<String> players = new ArrayList<>(plugin.getPlayers().getConfigurationSection("players").getKeys(false));
                    ArrayList<String> users = new ArrayList<>();
                    if(!args[1].equals("")){
                        for(int i=0; i < players.size(); i++){
                            if(players.get(i).toLowerCase().startsWith(args[1].toLowerCase())){
                                users.add(players.get(i));
                            }
                        }
                    } else {
                        users.addAll(players);
                    }
                    Collections.sort(users);
                    return users;
                }
                if(args[0].equalsIgnoreCase("reload")){
                    ArrayList<String> commands = new ArrayList<>();
                    ArrayList<String> cmds = new ArrayList<>();
                    commands.add("playersfile"); commands.add("groupsfile"); commands.add("configfile");
                    if(!args[1].equals("")){
                        for(int i=0; i < commands.size(); i++){
                            if(commands.get(i).toLowerCase().startsWith(args[1].toLowerCase())){
                                cmds.add(commands.get(i));
                            }
                        }
                    } else {
                        cmds.addAll(commands);
                    }
                    Collections.sort(cmds);
                    return cmds;
                }
            } else if(args.length == 3){
                if(args[0].equalsIgnoreCase("sethelp")){
                    ArrayList<String> commands = new ArrayList<>();
                    ArrayList<String> cmds = new ArrayList<>();
                    commands.add("true"); commands.add("false");
                    if(!args[2].equals("")){
                        for(int i=0; i < commands.size(); i++){
                            if(commands.get(i).toLowerCase().startsWith(args[2].toLowerCase())){
                                cmds.add(commands.get(i));
                            }
                        }
                    } else {
                        cmds.addAll(commands);
                    }
                    return cmds;
                }
                if(args[0].equalsIgnoreCase("addtab")){
                    ArrayList<String> cmdsfinal = new ArrayList<>();
                    ArrayList<String> blackList = new ArrayList<>();
                    for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
                        if(plugins != null){
                            if(!blackList.contains(plugins.getName())){
                                blackList.add(plugins.getName());
                            }
                        }
                    }
                    blackList.add("Aliases"); blackList.add("Bukkit"); blackList.add("Minecraft");
                    blackList.add("hprojectinternal"); blackList.add("hpp"); blackList.add("hpj");

                    ArrayList<String> commands = new ArrayList<>();
                    for(HelpTopic cmdLabel : plugin.getServer().getHelpMap().getHelpTopics()){
                        if(!commands.contains(cmdLabel.getName().replaceAll("/", ""))){
                            commands.add(cmdLabel.getName().replaceAll("/", ""));
                        }
                    }
                    for(int i=0; i < blackList.size(); i++){
                        if(commands.contains(blackList.get(i))){
                            commands.remove(blackList.get(i));
                        }
                    }

                    ArrayList<String> cmds = (ArrayList<String>) GroupManager.getTabList(args[1], plugin);
                    for(int i=0; i < cmds.size(); i++){
                        if(commands.contains(cmds.get(i))){
                            commands.remove(cmds.get(i));
                        }
                    }

                    if(!args[2].equals("")){
                        for(int i=0; i < commands.size(); i++){
                            if(commands.get(i).toLowerCase().startsWith(args[2].toLowerCase())){
                                cmdsfinal.add(commands.get(i));
                            }
                        }
                    } else {
                        cmdsfinal.addAll(commands);
                    }
                    Collections.sort(cmdsfinal);
                    return cmdsfinal;
                }

                if(args[0].equalsIgnoreCase("addcmd")){
                    ArrayList<String> cmdsfinal = new ArrayList<>();
                    ArrayList<String> blackList = new ArrayList<>();
                    for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
                        if(plugins != null){
                            if(!blackList.contains(plugins.getName())){
                                blackList.add(plugins.getName());
                            }
                        }
                    }
                    blackList.add("Aliases"); blackList.add("Bukkit"); blackList.add("Minecraft");
                    blackList.add("hprojectinternal"); blackList.add("hpp"); blackList.add("hpj");

                    ArrayList<String> commands = new ArrayList<>();
                    for(HelpTopic cmdLabel : plugin.getServer().getHelpMap().getHelpTopics()){
                        if(!commands.contains(cmdLabel.getName().replaceAll("/", ""))){
                            commands.add(cmdLabel.getName().replaceAll("/", ""));
                        }
                    }
                    for(int i=0; i < blackList.size(); i++){
                        if(commands.contains(blackList.get(i))){
                            commands.remove(blackList.get(i));
                        }
                    }

                    ArrayList<String> cmds = (ArrayList<String>) GroupManager.getGroupCommandsList(args[1], plugin);
                    for(int i=0; i < cmds.size(); i++){
                        if(commands.contains(cmds.get(i))){
                            commands.remove(cmds.get(i));
                        }
                    }

                    if(!args[2].equals("")){
                        for(int i=0; i < commands.size(); i++){
                            if(commands.get(i).toLowerCase().startsWith(args[2].toLowerCase())){
                                cmdsfinal.add(commands.get(i));
                            }
                        }
                    } else {
                        cmdsfinal.addAll(commands);
                    }
                    Collections.sort(cmdsfinal);
                    return cmdsfinal;
                }
            }
        }
        return null;
    }
}
