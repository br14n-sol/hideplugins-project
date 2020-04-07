package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import io.github.complexcodegit.hidepluginsproject.objects.PageObject;
import io.github.complexcodegit.hidepluginsproject.objects.WorldObject;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class GroupManager {
    private HidePluginsProject plugin;
    private static List<GroupObject> groupsObjects = new ArrayList<>();
    public GroupManager(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    public static List<GroupObject> getGroupsObjects(){
        return groupsObjects;
    }
    public static void generateGroups(HidePluginsProject plugin) {
        getGroupsObjects().clear();
        FileConfiguration groups = plugin.getGroups();
        List<GroupObject> groupObjects = new ArrayList<>();
        for(String groupKey : groups.getConfigurationSection("groups").getKeys(false)) {
            GroupObject groupObject = new GroupObject(groupKey);
            if(groups.get("groups."+groupKey+".options.default") != null){
                groupObject.setDefault(groups.getBoolean("groups."+groupKey+".options.default"));
            }
            if(groups.get("groups."+groupKey+".options.custom-help.enable") != null){
                groupObject.setCustomHelp(groups.getBoolean("groups."+groupKey+".options.custom-help.enable"));
            }
            if(groups.get("groups."+groupKey+".options.permission") != null){
                groupObject.setPermission(groups.getString("groups."+groupKey+".options.permission"));
            }
            for(String worldKey : groups.getConfigurationSection("groups."+groupKey+".worlds").getKeys(false)){
                WorldObject worldObject = new WorldObject(worldKey);
                for(String command : Utils.toList(groups.getString("groups."+groupKey+".worlds."+worldKey+".commands"))){
                    worldObject.addCommand(command);
                }
                for(String tab : Utils.toList(groups.getString("groups."+groupKey+".worlds."+worldKey+".tab"))){
                    worldObject.addTab(tab);
                }
                if(groups.get("groups."+groupKey+".options.custom-help.worlds."+worldKey) != null){
                    for(String pageKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds."+worldKey+".pages").getKeys(false)){
                        PageObject pageObject = new PageObject(pageKey, groups.getStringList("groups."+groupKey+".options.custom-help.worlds."+worldKey+".pages."+pageKey));
                        worldObject.addPage(pageObject);
                    }
                }
                if(groups.get("groups."+groupKey+".global") != null){
                    for(String command : Utils.toList(groups.getString("groups."+groupKey+".global.commands"))){
                        worldObject.addCommand(command);
                    }
                    for(String tab : Utils.toList(groups.getString("groups."+groupKey+".global.tab"))){
                        worldObject.addTab(tab);
                    }
                }
                groupObject.addWorld(worldObject);
            }
            groupObjects.add(groupObject);
        }
        for(GroupObject group : groupObjects){
            if(groups.get("groups."+group.getGroupName()+".options.inheritances") != null){
                for(String inherit : Utils.toList(groups.getString("groups."+group.getGroupName()+".options.inheritances"))){
                    for(GroupObject groupInherit : groupObjects){
                        if(groupInherit.getGroupName().equals(inherit)){
                            group.addInheritance(groupInherit);
                        }
                    }
                }
            }
            for(GroupObject inheritObject : group.getInheritances()){
                for(WorldObject worldObject : inheritObject.getWorlds()){
                    if(group.getWorld(worldObject.getWorldName()) != null){
                        for(String command : worldObject.getCommands()){
                            group.getWorld(worldObject.getWorldName()).addCommand(command);
                        }
                        for(String tab : worldObject.getTabs()){
                            group.getWorld(worldObject.getWorldName()).addTab(tab);
                        }
                    }
                }
            }
            getGroupsObjects().add(group);
        }
        if(Bukkit.getOnlinePlayers().size() > 1){
            for(Player player : Bukkit.getOnlinePlayers()){
                getPlayerGroup(player).addPlayer(player);
            }
        }
    }
    private static GroupObject getDefault(){
        for(GroupObject group : getGroupsObjects()){
            if(group.isDefault()){
                return group;
            }
        }
        return null;
    }
    public static GroupObject getPlayerGroup(Player player){
        for(GroupObject group : getGroupsObjects()){
            if(group.getPermission() != null && player.hasPermission(group.getPermission())){
                return group;
            }
        }
        return getDefault();
    }
    public List<String> getCommands(Player player, Boolean tab){
        if(!tab){
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                return getPlayerGroup(player).getWorld(player.getWorld().getName()).getCommands();
            }
        } else {
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                return getPlayerGroup(player).getWorld(player.getWorld().getName()).getTabs();
            }
        }
        return new ArrayList<>();
    }
    public List<String> getHelpPage(Player player, String pageNumber){
        if(getPlayerGroup(player).getWorld(player.getWorld().getName()).getPage(pageNumber) != null){
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
    public static void updateCmdGroup(String groupName){
        GroupObject group = null;
        for(GroupObject groupObject : getGroupsObjects()){
            if(groupObject.getGroupName().equals(groupName)){
                group = groupObject;
            }
        }
        if(group != null){
            for(Player player : group.getPlayerList()){
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
