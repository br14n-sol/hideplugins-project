package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.objects.GlobalObject;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import io.github.complexcodegit.hidepluginsproject.objects.PageObject;
import io.github.complexcodegit.hidepluginsproject.objects.WorldObject;
import io.github.complexcodegit.hidepluginsproject.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

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
        int id = 0;
        for(String groupKey : groups.getConfigurationSection("groups").getKeys(false)) {
            GroupObject groupObject = new GroupObject(groupKey, id);
            if(groups.get("groups."+groupKey+".options.default") != null){
                groupObject.setDefault(groups.getBoolean("groups."+groupKey+".options.default"));
            }
            if(groups.get("groups."+groupKey+".options.custom-help.enable") != null){
                groupObject.setCustomHelp(groups.getBoolean("groups."+groupKey+".options.custom-help.enable"));
            }
            if(groups.get("groups."+groupKey+".options.permission") != null){
                groupObject.setPermission(groups.getString("groups."+groupKey+".options.permission"));
            }
            if(groups.get("groups."+groupKey+".worlds") != null){
                for(String worldKey : groups.getConfigurationSection("groups."+groupKey+".worlds").getKeys(false)){
                    WorldObject worldObject = new WorldObject(worldKey);
                    for(String command : Utils.toList(groups.getString("groups."+groupKey+".worlds."+worldKey+".commands"))){
                        worldObject.addCommand(command);
                    }
                    for(String tab : Utils.toList(groups.getString("groups."+groupKey+".worlds."+worldKey+".tab"))){
                        worldObject.addTab(tab);
                    }
                    groupObject.addWorld(worldObject);
                }
            }
            if(groups.get("groups."+groupKey+".options.custom-help.worlds") != null){
                for(String helpKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds").getKeys(false)){
                    if(groupObject.getWorld(helpKey) != null){
                        for(String pageKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").getKeys(false)){
                            PageObject pageObject = new PageObject(pageKey, groups.getStringList("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages."+pageKey));
                            groupObject.getWorld(helpKey).addPage(pageObject);
                        }
                    } else {
                        WorldObject worldObject = new WorldObject(helpKey);
                        for(String pageKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").getKeys(false)){
                            PageObject pageObject = new PageObject(pageKey, groups.getStringList("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages."+pageKey));
                            worldObject.addPage(pageObject);
                        }
                        groupObject.addWorld(worldObject);
                    }
                }
            }
            if(groups.get("groups."+groupKey+".global") != null){
                GlobalObject globalObject = new GlobalObject();
                for(String command : Utils.toList(groups.getString("groups."+groupKey+".global.commands"))){
                    globalObject.addCommand(command);
                }
                for(String tab : Utils.toList(groups.getString("groups."+groupKey+".global.tab"))){
                    globalObject.addTab(tab);
                }
                groupObject.setGlobal(globalObject);
            }
            groupObjects.add(groupObject);
            id++;
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
                for(String command : inheritObject.getGlobal().getCommands()){
                    group.getGlobal().addCommand(command);
                }
                for(String tab : inheritObject.getGlobal().getTabs()){
                    group.getGlobal().addTab(tab);
                }
            }
            getGroupsObjects().add(group);
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
        List<Integer> groupIds = new ArrayList<>();
        for(GroupObject group : getGroupsObjects()){
            if(group.getPermission() != null){
                if(player.hasPermission(group.getPermission())){
                    groupIds.add(group.getId());
                }
            }
        }
        if(!groupIds.isEmpty()){
            int id = groupIds.stream().mapToInt(i -> i).max().getAsInt();
            for(GroupObject groupObject : getGroupsObjects()){
                if(groupObject.getId() == id){
                    return groupObject;
                }
            }
        }
        return getDefault();
    }
    public List<String> getCommands(Player player, Boolean tab){
        List<String> commands = new ArrayList<>();
        if(!tab){
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                for(String command : getPlayerGroup(player).getWorld(player.getWorld().getName()).getCommands()){
                    if(!commands.contains(command)){
                        commands.add(command);
                    }
                }
            }
            for(String command : getPlayerGroup(player).getGlobal().getCommands()){
                if(!commands.contains(command)){
                    commands.add(command);
                }
            }
            if(commands.contains("/help") && getPlayerGroup(player).getWorld(player.getWorld().getName()) == null){
                commands.remove("/help");
            }
        } else {
            if(getPlayerGroup(player).getWorld(player.getWorld().getName()) != null){
                for(String tabs : getPlayerGroup(player).getWorld(player.getWorld().getName()).getTabs()){
                    if(!commands.contains(tabs)){
                        commands.add(tabs);
                    }
                }
            }
            for(String tabs : getPlayerGroup(player).getGlobal().getTabs()){
                if(!commands.contains(tabs)){
                    commands.add(tabs);
                }
            }
            if(commands.contains("help") && getPlayerGroup(player).getWorld(player.getWorld().getName()) == null){
                commands.remove("help");
            }
        }
        return commands;
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
    public static void updateCmdGroup(String groupName){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(getPlayerGroup(player).getGroupName().equals(groupName)){
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
