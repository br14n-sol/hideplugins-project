package io.github.complexcodegit.hidepluginsproject.utils;

import org.bukkit.Bukkit;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class CommandChecker {
    public static boolean checkCommand(String command){
        return commands().contains(command);
    }
    public static ArrayList<String> commands(){
        ArrayList<String> blacklist = new ArrayList<>();
        for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
            if(plugins != null && !blacklist.contains(plugins.getName())){
                blacklist.add(plugins.getName());
            }
        }
        blacklist.add("Aliases");
        blacklist.add("Bukkit");
        blacklist.add("Minecraft");

        ArrayList<String> cmds = new ArrayList<>();
        for(HelpTopic cmdLabel : Bukkit.getServer().getHelpMap().getHelpTopics()){
            if(!cmds.contains(cmdLabel.getName())){
                cmds.add(cmdLabel.getName());
            }
        }

        for(String black : blacklist){
            cmds.remove(black);
        }
        return cmds;
    }
}
