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
import java.util.List;

public class ChiefCommandCompleter implements TabCompleter {
    private HidePluginsProject plugin;

    public ChiefCommandCompleter(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String string, String[] args) {
        Player player = (Player)sender;
        List<String> hprojectGroup = GroupManager.getCommandsList(player, plugin);
        List<String> commandsArg1 = new ArrayList<>();
        List<String> commandsArg2 = new ArrayList<>();
        List<String> commandsArg3 = new ArrayList<>();
        if(player.hasPermission("hidepluginsproject.hproject") || hprojectGroup.contains("/hproject")) {
            if(args.length == 1){
                commandsArg1.add("help");
                commandsArg1.add("groups");
                commandsArg1.add("group");
                commandsArg1.add("reload");
                commandsArg1.add("player");
                commandsArg1.add("addcmd");
                commandsArg1.add("sethelp");
                return commandsArg1;
            } else if(args.length == 2){
                List<String> groups = GroupManager.getGroups(plugin);
                if(args[0].equalsIgnoreCase("group") || args[0].equalsIgnoreCase("addcmd") || args[0].equalsIgnoreCase("sethelp")){
                    for(int i=0; i < groups.size(); i++){
                        commandsArg2.add(groups.get(i));
                    }
                    return commandsArg2;
                }

                if(args[0].equalsIgnoreCase("player")){
                    for(String users : plugin.getPlayers().getConfigurationSection("players").getKeys(false)){
                        commandsArg2.add(users);
                    }
                    return commandsArg2;
                }

                if(args[0].equalsIgnoreCase("reload")){
                    commandsArg2.add("playersfile");
                    commandsArg2.add("groupsfile");
                    commandsArg2.add("configfile");
                    commandsArg2.add("allfiles");
                    return commandsArg2;
                }
            } else if(args.length == 3){
                if(args[0].equalsIgnoreCase("sethelp")){
                    commandsArg3.add("true");
                    commandsArg3.add("false");
                    return commandsArg3;
                }

                if(args[0].equalsIgnoreCase("addcmd")){
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

                    List<String> groupCommands = GroupManager.getGroupCommandsList(args[1], plugin);
                    for(int i=0; i < groupCommands.size(); i++){
                        if(cmds.contains(groupCommands.get(i))){
                            cmds.remove(groupCommands.get(i));
                        }
                    }
                    return cmds;
                }
            }
        }
        return null;
    }
}
