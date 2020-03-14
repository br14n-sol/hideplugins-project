package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class GroupManager {
    private HidePluginsProject plugin;
    public GroupManager(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    public List<String> getGroups(){
        return new ArrayList<>(plugin.getGroups().getConfigurationSection("groups").getKeys(false));
    }
    public String getDefaultGroup(){
        String def = null;
        for(String group : getGroups()){
            if(plugin.getGroups().get("groups."+group+".options.default") != null &&
                    plugin.getGroups().getBoolean("groups."+group+".options.default")){
                def = group;
            }
        }
        return def;
    }
    public String getPlayerGroup(Player player) {
        List<String> permAndGroup = new ArrayList<>();
        String playerGroup = null;
        for(String group : getGroups()){
            permAndGroup.add("hidepluginsproject.group."+group);
        }
        for(String perm : permAndGroup) {
            if(player.hasPermission(perm))
                playerGroup = perm;
        }
        if(playerGroup == null && getDefaultGroup() != null)
            playerGroup = "hidepluginsproject.group."+getDefaultGroup();
        return playerGroup.substring(25);
    }
    public List<String> getCommands(Player player){
        List<String> commandsList = new ArrayList<>();
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".options.inheritances") != null){
            for(String inherit : plugin.getGroups().getString("groups."+getPlayerGroup(player)+".options.inheritances")
                    .replace(" ", "").split(",")){
                if(plugin.getGroups().get("groups."+inherit+".worlds."+player.getWorld().getName()) != null)
                    commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+inherit+".worlds."
                            +player.getWorld().getName()+".commands").replace(" ", "").split(",")));
                if(plugin.getGroups().get("groups."+inherit+".global") != null)
                    commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+inherit+".global.commands")
                            .replace(" ", "").split(",")));
            }
        }
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName()) != null)
            commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName()
                    +".commands").replace(" ", "").split(",")));
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".global") != null)
            commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+getPlayerGroup(player)+".global.commands")
                    .replace(" ", "").split(",")));
        if(commandsList.contains("/help") && plugin.getGroups().get("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName()) == null
                && plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable"))
            commandsList.remove("/help");
        return commandsList;
    }
    public List<String> getTabs(Player player){
        List<String> commandsList = new ArrayList<>();
        List<String> tabList = new ArrayList<>();
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".options.inheritances") != null){
            for(String inherit : plugin.getGroups().getString("groups."+getPlayerGroup(player)+".options.inheritances")
                    .replace(" ", "").split(",")){
                if(plugin.getGroups().get("groups."+inherit+".worlds."+player.getWorld().getName()) != null)
                    commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+inherit+".worlds."
                            +player.getWorld().getName()+".tab").replace(" ", "").split(",")));
                if(plugin.getGroups().contains("groups."+inherit+".global"))
                    commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+inherit+".global.tab")
                            .replace(" ", "").split(",")));
            }
        }
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName()) != null)
            commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName()
                    +".tab").replace(" ", "").split(",")));
        if(plugin.getGroups().get("groups."+getPlayerGroup(player)+".global") != null)
            commandsList.addAll(Arrays.asList(plugin.getGroups().getString("groups."+getPlayerGroup(player)+".global.tab")
                    .replace(" ", "").split(",")));
        if(commandsList.contains("/help") && plugin.getGroups().get("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName()) == null
                && plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable"))
            commandsList.remove("/help");
        for(String tab : commandsList)
            tabList.add(tab.replaceFirst("/", ""));
        return tabList;
    }

    public List<String> getWorlds(){
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds())
            worlds.add(world.getName());
        return worlds;
    }
    public List<String> getGroupWorlds(String group){
        List<String> worlds = new ArrayList<>();
        if(plugin.getGroups().get("groups."+group+".worlds") != null)
            worlds.addAll(plugin.getGroups().getConfigurationSection("groups."+group+".worlds").getKeys(false));
        return worlds;
    }
    public List<String> getWorldCommands(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".worlds."+world))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".worlds."+world+".commands")
                    .replace(" ", "").split(",")));
        return list;
    }
    public List<String> getWorldTab(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".worlds."+world))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".worlds."+world+".tab")
                    .replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalCommands(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".global"))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".global.commands")
                    .replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalTab(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".global"))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".global.tab")
                    .replace(" ", "").split(",")));
        return list;
    }
    public List<String> getInherit(String group){
        return new ArrayList<>(Arrays.asList(plugin.getGroups().getString("groups."+group+".options.inheritances")
                .replace(" ", "").split(",")));
    }
}
