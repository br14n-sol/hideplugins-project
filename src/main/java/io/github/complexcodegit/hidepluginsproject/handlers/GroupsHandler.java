package io.github.complexcodegit.hidepluginsproject.handlers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.objects.*;
import io.github.complexcodegit.hidepluginsproject.Utils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupsHandler {
    private static List<GroupObject> loadGroups = new ArrayList<>();

    public static List<GroupObject> getLoadGroups(){
        return loadGroups;
    }
    public static void read(HidePluginsProject plugin){
        if(!loadGroups.isEmpty()){
            loadGroups.clear();
        }
        FileConfiguration groups = plugin.getGroups();
        List<GroupObject> preGroups = new ArrayList<>();
        for(String groupKey : groups.getConfigurationSection("groups").getKeys(false)){
            GroupObject groupObject = new GroupObject(groupKey);
            if(groups.get("groups."+groupKey+".options.permission") != null && !groups.get("groups."+groupKey+".options.permission").equals("")){
                groupObject.setPermission(groups.getString("groups."+groupKey+".options.permission"));
            }
            if(groups.get("groups."+groupKey+".options.default") != null && !groups.get("groups."+groupKey+".options.default").equals("")){
                groupObject.setDefault(groups.getBoolean("groups."+groupKey+".options.default"));
            }
            if(groups.get("groups."+groupKey+".options.custom-help.enable") != null && !groups.get("groups."+groupKey+".options.custom-help.enable").equals("")){
                groupObject.setHelp(groups.getBoolean("groups."+groupKey+".options.custom-help.enable"));
            }
            if(groups.get("groups."+groupKey+".worlds") != null && !groups.get("groups."+groupKey+".worlds").equals("")){
                for(String worldKey : groups.getConfigurationSection("groups."+groupKey+".worlds").getKeys(false)){
                    WorldObject worldObject = new WorldObject(worldKey);
                    if(groups.get("groups."+groupKey+".worlds."+worldKey+".commands") != null && !groups.get("groups."+groupKey+".worlds."+worldKey+".commands").equals("")){
                        for(String command : Utils.toList(Objects.requireNonNull(groups.getString("groups." + groupKey + ".worlds." + worldKey + ".commands")))){
                            worldObject.addCommand(command);
                        }
                    }
                    if(groups.get("groups."+groupKey+".worlds."+worldKey+".tab") != null && !groups.get("groups."+groupKey+".worlds."+worldKey+".tab").equals("")){
                        for(String tab : Utils.toList(Objects.requireNonNull(groups.getString("groups." + groupKey + ".worlds." + worldKey + ".tab")))){
                            worldObject.addTab(tab);
                        }
                    }
                    groupObject.addWorld(worldObject);
                }
            }
            if(groups.get("groups."+groupKey+".options.custom-help.worlds") != null){
                for(String helpKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds").getKeys(false)){
                    if(groupObject.getWorld(helpKey) != null){
                        if(groups.get("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages") != null && !groups.get("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").equals("")){
                            for(String pageKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").getKeys(false)){
                                PageObject pageObject = new PageObject(pageKey, groups.getStringList("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages."+pageKey));
                                groupObject.getWorld(helpKey).addPage(pageObject);
                            }
                        }
                    } else {
                        WorldObject worldObject = new WorldObject(helpKey);
                        if(groups.get("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages") != null && !groups.get("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").equals("")){
                            for(String pageKey : groups.getConfigurationSection("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages").getKeys(false)){
                                PageObject pageObject = new PageObject(pageKey, groups.getStringList("groups."+groupKey+".options.custom-help.worlds."+helpKey+".pages."+pageKey));
                                worldObject.addPage(pageObject);
                            }
                        }
                        groupObject.addWorld(worldObject);
                    }
                }
            }
            if(groups.get("groups."+groupKey+".global") != null){
                GlobalObject globalObject = new GlobalObject();
                if(groups.get("groups."+groupKey+".global.commands") != null && !groups.get("groups."+groupKey+".global.commands").equals("")){
                    for(String command : Utils.toList(Objects.requireNonNull(groups.getString("groups." + groupKey + ".global.commands")))){
                        globalObject.addCommand(command);
                    }
                }
                if(groups.get("groups."+groupKey+".global.tab") != null && !groups.get("groups."+groupKey+".global.tab").equals("")){
                    for(String tab : Utils.toList(Objects.requireNonNull(groups.getString("groups." + groupKey + ".global.tab")))){
                        globalObject.addTab(tab);
                    }
                }
                groupObject.setGlobal(globalObject);
            }
            preGroups.add(groupObject);
        }
        for(GroupObject group : preGroups){
            if(groups.get("groups."+group.getName()+".options.inheritances") != null && !groups.get("groups."+group.getName()+".options.inheritances").equals("")){
                for(String inherit : Utils.toList(Objects.requireNonNull(groups.getString("groups." + group.getName() + ".options.inheritances")))){
                    for(GroupObject groupInherit : preGroups){
                        if(groupInherit.getName().equals(inherit)){
                            group.addInheritance(groupInherit);
                        }
                    }
                }
            }
            for(GroupObject inheritObject : group.getInheritances()){
                for(WorldObject worldObject : inheritObject.getWorlds()){
                    if(group.getWorld(worldObject.getName()) != null){
                        for(String command : worldObject.getCommands()){
                            group.getWorld(worldObject.getName()).addCommand(command);
                        }
                        for(String tab : worldObject.getTabs()){
                            group.getWorld(worldObject.getName()).addTab(tab);
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
            loadGroups.add(group);
        }
    }
}