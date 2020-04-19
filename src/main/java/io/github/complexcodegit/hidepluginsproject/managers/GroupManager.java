package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.handlers.GroupsHandler;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupManager {
    private HidePluginsProject plugin;
    public GroupManager(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public GroupObject getDefault(){
        for(GroupObject groupObject : GroupsHandler.getLoadGroups()) {
            if(groupObject.isDefault()) {
                return groupObject;
            }
        }
        return null;
    }
    public GroupObject getPlayerGroup(Player player){
        List<GroupObject> playerGroups = new ArrayList<>();
        for(GroupObject group : GroupsHandler.getLoadGroups()){
            if(group.getPermission() != null){
                if(player.hasPermission(group.getPermission())){
                    playerGroups.add(group);
                }
            }
        }
        if(!playerGroups.isEmpty()){
            int id = playerGroups.stream().mapToInt(GroupObject::getID).max().getAsInt();
            for(GroupObject groupObject : playerGroups){
                if(groupObject.getID() == id){
                    return groupObject;
                }
            }
        }
        return getDefault();
    }
    public List<String> getCommands(Player player, boolean tab){
        List<String> playerCommands = new ArrayList<>();
        if(tab){
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                playerCommands.addAll(getPlayerGroup(player).getWorld(player.getWorld().getName()).getTabs());
            }
            playerCommands.addAll(getPlayerGroup(player).getGlobal().getTabs());
            if(playerCommands.contains("help") && getPlayerGroup(player).getWorld(player.getWorld().getName()) == null && getPlayerGroup(player).hasHelp()){
                playerCommands.remove("help");
            }
        } else {
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                playerCommands.addAll(getPlayerGroup(player).getWorld(player.getWorld().getName()).getCommands());
            }
            playerCommands.addAll(getPlayerGroup(player).getGlobal().getCommands());
            if(playerCommands.contains("/help") && getPlayerGroup(player).getWorld(player.getWorld().getName()) == null && getPlayerGroup(player).hasHelp()){
                playerCommands.remove("/help");
            }
        }
        return playerCommands;
    }
    public List<String> getHelpPage(Player player, String pageNumber){
        if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null && getPlayerGroup(player).getWorld(player.getWorld().getName()).getPage(pageNumber) != null){
            return getPlayerGroup(player).getWorld(player.getWorld().getName()).getPage(pageNumber).getLines();
        }
        return null;
    }

    public List<String> getGroups(){
        return new ArrayList<>(plugin.getGroups().getConfigurationSection("groups").getKeys(false));
    }
    public List<String> getWorlds(){
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()){
            worlds.add(world.getName());
        }
        return worlds;
    }
    public List<String> getGroupWorlds(String groupName){
        List<String> worlds = new ArrayList<>();
        if(plugin.getGroups().get("groups."+groupName+".worlds") != null)
            worlds.addAll(plugin.getGroups().getConfigurationSection("groups."+groupName+".worlds").getKeys(false));
        return worlds;
    }
    public List<String> getInherit(String groupName){
        return new ArrayList<>(Arrays.asList(plugin.getGroups().getString("groups."+groupName+".options.inheritances").replace(" ", "").split(",")));
    }
    public List<String> getWorldCommands(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().get("groups."+group+".worlds."+world) != null && !plugin.getGroups().getString("groups."+group+".worlds."+world+".commands").equals(""))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".worlds."+world+".commands").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getWorldTab(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().get("groups."+group+".worlds."+world) != null && !plugin.getGroups().getString("groups."+group+".worlds."+world+".tab").equals(""))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".worlds."+world+".tab").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalCommands(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().get("groups."+group+".global") != null && !plugin.getGroups().getString("groups."+group+".global.commands").equals(""))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".global.commands").replace(" ", "").split(",")));
        return list;
    }
    public List<String> getGlobalTab(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().get("groups."+group+".global") != null && !plugin.getGroups().getString("groups."+group+".global.tab").equals(""))
            list.addAll(Arrays.asList(plugin.getGroups().getString("groups."+group+".global.tab").replace(" ", "").split(",")));
        return list;
    }
    public void updateCmdGroup(String groupName){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(getPlayerGroup(player).getName().equals(groupName)){
                player.updateCommands();
            }
        }
    }

    public static void updateCmds(){
        for(Player player : Bukkit.getOnlinePlayers()){
            player.updateCommands();
        }
    }
}