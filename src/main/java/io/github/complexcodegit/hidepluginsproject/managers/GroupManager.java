package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class GroupManager {
    private HidePluginsProject plugin;
    private FileConfiguration groups = plugin.getGroups();
    public GroupManager(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    private boolean isActiveGroup(String group){
        return getGroups().contains(group) && groups.getBoolean("groups."+group+".options.enable");
    }
    private boolean isActiveWorld(String group, Player player){
        return groups.get("groups."+group+".worlds."+player.getWorld().getName()) != null && groups.getBoolean("groups."+group+".worlds."+player.getWorld().getName()+".enable");
    }
    private boolean isActiveGlobal(String group){
        return groups.get("groups."+group+".global") != null && groups.getBoolean("groups."+group+".global.enable");
    }
    private boolean isActiveHelp(String group, Player player){
        return groups.get("groups."+group+".options.custom-help.worlds."+player.getWorld().getName()) == null && groups.getBoolean("groups."+group+".options.custom-help.enable");
    }

    public List<String> getGroups(){
        return new ArrayList<>(groups.getConfigurationSection("groups").getKeys(false));
    }

    private String getDefault(){
        String groupName = null;
        for(String group : getGroups()){
            if(groups.get("groups."+group+".options.default") != null && groups.getBoolean("groups."+group+".options.default")){
                groupName = group;
                break;
            }
        }
        return groupName;
    }

    public String getPlayerGroup(Player player) {
        String playerGroup = null;
        for(String group : getGroups()) {
            if(groups.get("groups."+group+".options.permission") != null && player.hasPermission(groups.getString("groups."+group+".options.permission"))){
                playerGroup = group;
                break;
            }
        }
        if(playerGroup == null && getDefault() != null)
            playerGroup = getDefault();
        return playerGroup;
    }
    public List<String> getCommands(Player player, Boolean tab){
        List<String> commands = new ArrayList<>();
        String group = getPlayerGroup(player);
        if(isActiveGroup(group)){
            if(!tab){
                if(groups.get("groups."+group+".options.inheritances") != null){
                    for(String inherit : groups.getString("groups."+group+".options.inheritances").replace(" ", "").split(",")){
                        if(isActiveGroup(inherit)){
                            if(isActiveGlobal(inherit)){
                                commands.addAll(Arrays.asList(groups.getString("groups."+inherit+".global.commands").replace(" ", "").split(",")));
                            }
                            if(isActiveWorld(inherit, player)){
                                commands.addAll(Arrays.asList(groups.getString("groups."+inherit+".worlds."+player.getWorld().getName()+".commands").replace(" ", "").split(",")));
                            }
                        }
                    }
                }
                if(isActiveGlobal(group)){
                    commands.addAll(Arrays.asList(groups.getString("groups."+group+".global.commands").replace(" ", "").split(",")));
                }
                if(isActiveWorld(group, player)){
                    commands.addAll(Arrays.asList(groups.getString("groups."+group+".worlds."+player.getWorld().getName()+".commands").replace(" ", "").split(",")));
                }
                if(commands.contains("/help") && isActiveHelp(group, player)){
                    commands.remove("/help");
                }
                return commands;
            } else {
                List<String> tabs = new ArrayList<>();
                if(groups.get("groups."+group+".options.inheritances") != null){
                    for(String inherit : groups.getString("groups."+group+".options.inheritances").replace(" ", "").split(",")){
                        if(isActiveGroup(inherit)){
                            if(isActiveGlobal(inherit)){
                                commands.addAll(Arrays.asList(groups.getString("groups."+inherit+".global.tabs").replace(" ", "").split(",")));
                            }
                            if(isActiveWorld(inherit, player)){
                                commands.addAll(Arrays.asList(groups.getString("groups."+inherit+".worlds."+player.getWorld().getName()+".tabs").replace(" ", "").split(",")));
                            }
                        }
                    }
                }
                if(isActiveGlobal(group)){
                    commands.addAll(Arrays.asList(groups.getString("groups."+group+".global.tabs").replace(" ", "").split(",")));
                }
                if(isActiveWorld(group, player)){
                    commands.addAll(Arrays.asList(groups.getString("groups."+group+".worlds."+player.getWorld().getName()+".tabs").replace(" ", "").split(",")));
                }
                if(commands.contains("/help") && isActiveHelp(group, player)){
                    commands.remove("/help");
                }
                for(String command : commands) {
                    tabs.add(command.replaceFirst("/", ""));
                }
                return tabs;
            }
        }
        return commands;
    }
    public List<String> getWorlds(){
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds())
            worlds.add(world.getName());
        return worlds;
    }
    public List<String> getGroupWorlds(String group){
        List<String> worlds = new ArrayList<>();
        if(groups.get("groups."+group+".worlds") != null)
            worlds.addAll(plugin.getGroups().getConfigurationSection("groups."+group+".worlds").getKeys(false));
        return worlds;
    }
    public List<String> getWorldCommands(String group, String world){
        List<String> list = new ArrayList<>();
        if(groups.get("groups."+group+".worlds."+world) != null)
            list.addAll(Arrays.asList(groups.getString("groups."+group+".worlds."+world+".commands").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getWorldTab(String group, String world){
        List<String> list = new ArrayList<>();
        if(groups.get("groups."+group+".worlds."+world) != null)
            list.addAll(Arrays.asList(groups.getString("groups."+group+".worlds."+world+".tab").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalCommands(String group){
        List<String> list = new ArrayList<>();
        if(groups.get("groups."+group+".global") != null)
            list.addAll(Arrays.asList(groups.getString("groups."+group+".global.commands").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalTab(String group){
        List<String> list = new ArrayList<>();
        if(groups.get("groups."+group+".global") != null)
            list.addAll(Arrays.asList(groups.getString("groups."+group+".global.tab").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getInherit(String group){
        return new ArrayList<>(Arrays.asList(groups.getString("groups."+group+".options.inheritances").replace(" ", "").split(",")));
    }
}
