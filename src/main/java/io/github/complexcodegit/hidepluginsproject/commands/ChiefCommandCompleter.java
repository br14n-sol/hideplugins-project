package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChiefCommandCompleter implements TabCompleter {
    private GroupManager groupManager;
    public ChiefCommandCompleter(GroupManager groupManager){
        this.groupManager = groupManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String string, String[] args) {
        Player player = (Player)sender;
        if(player.hasPermission("hidepluginsproject.hproject") || groupManager.getCommands(player, false).contains("/hproject")){
            /*
             /hproject <select, create, set, add, remove, reload, finish, help>
             */
            if(args.length == 1){
                List<String> arguments = new ArrayList<>();
                List<String> completes = new ArrayList<>();
                arguments.add("select"); arguments.add("set"); arguments.add("remove"); arguments.add("finish");
                arguments.add("create"); arguments.add("add"); arguments.add("reload"); arguments.add("help");
                if(!args[0].equals("")){
                    for(String argument : arguments){
                        if(argument.toLowerCase().startsWith(args[0].toLowerCase())) {
                            completes.add(argument);
                        }
                    }
                } else {
                    completes.addAll(arguments);
                }
                Collections.sort(completes);
                return completes;
            }
            /*
             /hproject <select, create, set, add, remove> <group, world, global, help, inherit, commands, tabs>
             */
            else if(args.length == 2){
                /*
                 /hproject select <group, world, global>
                 */
                if(args[0].equalsIgnoreCase("select")){
                    List<String> arguments = new ArrayList<>();
                    List<String> completes = new ArrayList<>();
                    arguments.add("group"); arguments.add("world"); arguments.add("global");
                    if(!args[1].equals("")){
                        for(String argument : arguments){
                            if(argument.toLowerCase().startsWith(args[1].toLowerCase())){
                                completes.add(argument);
                            }
                        }
                    } else {
                        completes.addAll(arguments);
                    }
                    Collections.sort(completes);
                    return completes;
                }
                /*
                 /hproject create <group>
                 */
                else if(args[0].equalsIgnoreCase("create")){
                    List<String> arguments = new ArrayList<>();
                    List<String> completes = new ArrayList<>();
                    arguments.add("group");
                    if(!args[1].equals("")){
                        for(String argument : arguments){
                            if(argument.toLowerCase().startsWith(args[1].toLowerCase())){
                                completes.add(argument);
                            }
                        }
                    } else {
                        completes.addAll(arguments);
                    }
                    return completes;
                }
                /*
                 /hproject set help
                 */
                else if(args[0].equalsIgnoreCase("set")){
                    List<String> arguments = new ArrayList<>();
                    List<String> completes = new ArrayList<>();
                    arguments.add("help"); arguments.add("permission");
                    if(!args[1].equals("")){
                        for(String argument : arguments){
                            if(argument.toLowerCase().startsWith(args[1].toLowerCase())){
                                completes.add(argument);
                            }
                        }
                    } else {
                        completes.addAll(arguments);
                    }
                    return completes;
                }
                /*
                 /hproject add <world, global, inherit, commands, tabs>
                 */
                else if(args[0].equalsIgnoreCase("add")){
                    List<String> arguments = new ArrayList<>();
                    List<String> completes = new ArrayList<>();
                    arguments.add("world"); arguments.add("global"); arguments.add("inherit");
                    arguments.add("commands"); arguments.add("tabs");
                    if(!args[1].equals("")){
                        for(String argument : arguments){
                            if(argument.toLowerCase().startsWith(args[1].toLowerCase())){
                                completes.add(argument);
                            }
                        }
                    } else {
                        completes.addAll(arguments);
                    }
                    return completes;
                }
                /*
                 /hproject remove <inherit, commands, tabs>
                 */
                else if(args[0].equalsIgnoreCase("remove")){
                    List<String> arguments = new ArrayList<>();
                    List<String> completes = new ArrayList<>();
                    arguments.add("inherit"); arguments.add("commands"); arguments.add("tabs");
                    if(!args[1].equals("")){
                        for(String argument : arguments){
                            if(argument.toLowerCase().startsWith(args[1].toLowerCase())){
                                completes.add(argument);
                            }
                        }
                    } else {
                        completes.addAll(arguments);
                    }
                    return completes;
                }
            }
            /*
             /hproject <set, select> <help, group> <true, false, [name]>
             */
            else if(args.length == 3){
                /*
                 /hproject set help <true, false>
                 */
                if(args[0].equalsIgnoreCase("set")){
                    if(args[1].equalsIgnoreCase("help")){
                        List<String> arguments = new ArrayList<>();
                        List<String> completes = new ArrayList<>();
                        arguments.add("true"); arguments.add("false");
                        if(!args[2].equals("")){
                            for(String argument : arguments){
                                if(argument.toLowerCase().startsWith(args[2].toLowerCase())){
                                    completes.add(argument);
                                }
                            }
                        } else {
                            completes.addAll(arguments);
                        }
                        return completes;
                    }
                }
                /*
                 /hproject select group [name]
                 */
                else if(args[0].equalsIgnoreCase("select")){
                    if(args[1].equalsIgnoreCase("group")){
                        List<String> arguments = groupManager.getGroups();
                        List<String> completes = new ArrayList<>();
                        if(!args[2].equals("")){
                            for(String argument : arguments){
                                if(argument.toLowerCase().startsWith(args[2].toLowerCase())){
                                    completes.add(argument);
                                }
                            }
                        } else {
                            completes.addAll(arguments);
                        }
                        return completes;
                    }
                }
            }
        }
        return null;
    }
}
