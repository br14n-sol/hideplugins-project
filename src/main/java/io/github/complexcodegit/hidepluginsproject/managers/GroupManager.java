package io.github.complexcodegit.hidepluginsproject.managers;

import java.util.*;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

public class GroupManager {
    static List<String> groupsPermissions = new ArrayList<>();
    static List<String> playerPermissions = new ArrayList<>();
    static List<String> groupsList = new ArrayList<>();
    static String permission;
    static String group;

    public static List<String> getGroups(HidePluginsProject plugin) {
        for(String groups : plugin.getGroups().getConfigurationSection("groups").getKeys(false)) {
            if(!groupsList.contains(groups)) {
                groupsList.add(groups);
            }
        }
        return groupsList;
    }

    public static List<String> getGroupsPermissions(HidePluginsProject plugin) {
        for(String groups : plugin.getGroups().getConfigurationSection("groups").getKeys(false)) {
            if(!groupsPermissions.contains("hidepluginsproject.group."+groups)) {
                groupsPermissions.add("hidepluginsproject.group."+groups);
            }
        }
        return groupsPermissions;
    }

    public static String getGroupPermission(String group) {
        return "hidepluginsproject.group."+group;
    }

    public static String getPlayerGroupPermission(Player player, HidePluginsProject plugin) {
        for(PermissionAttachmentInfo pio : player.getEffectivePermissions()) {
            String perm = pio.getPermission();
            playerPermissions.add(perm);
        }
        for(int i=0; i < getGroupsPermissions(plugin).size(); i++) {
            if(playerPermissions.contains(getGroupsPermissions(plugin).get(i))) {
                permission = getGroupsPermissions(plugin).get(i);
            }
        }
        if(permission == null) {
            permission = "hidepluginsproject.group.default";
        }
        return permission;
    }

    public static String getPlayerGroup(Player player, HidePluginsProject plugin) {
        String groupPerm = getPlayerGroupPermission(player, plugin);
        group = groupPerm.substring(25, permission.length());
        return group;
    }

    public static List<String> getCommandsList(Player player, HidePluginsProject plugin){
        List<String> commandsList = new ArrayList<>();
        for(String commands : plugin.getGroups().getStringList("groups."+getPlayerGroup(player, plugin)+".commands")) {
            if(!commandsList.contains("/"+commands)) {
                commandsList.add("/"+commands);
            }
        }
        if(!plugin.getGroups().getStringList("groups."+getPlayerGroup(player, plugin)+".inheritance").isEmpty()){
            List<String> inheritances = plugin.getGroups().getStringList("groups."+getPlayerGroup(player, plugin)+".inheritance");
            List<String> groups = getGroups(plugin);
            for(String inheritance : inheritances){
                if(groups.contains(inheritance)){
                    for(String commands : plugin.getGroups().getStringList("groups."+inheritance+".commands")){
                        if(!commandsList.contains("/"+commands)) {
                            commandsList.add("/"+commands);
                        }
                    }
                }
            }
        }
        return commandsList;
    }

    public static List<String> getGroupCommandsList(String group, HidePluginsProject plugin){
        List<String> commandsList = new ArrayList<>();
        for(String commands : plugin.getGroups().getStringList("groups."+group+".commands")) {
            if(!commandsList.contains(commands)) {
                commandsList.add(commands);
            }
        }
        return commandsList;
    }

    public static List<String> getTabList(String group, HidePluginsProject plugin){
        List<String> tabList = new ArrayList<>();
        for(String tab : plugin.getGroups().getStringList("groups."+group+".tab-completes")) {
            if(!tabList.contains(tab)) {
                tabList.add(tab);
            }
        }
        if(!plugin.getGroups().getStringList("groups."+group+".inheritance").isEmpty()){
            List<String> inheritances = plugin.getGroups().getStringList("groups."+group+".inheritance");
            List<String> groups = getGroups(plugin);
            for(String inheritance : inheritances){
                if(groups.contains(inheritance)){
                    for(String tab : plugin.getGroups().getStringList("groups."+inheritance+".tab-completes")){
                        if(!tabList.contains(tab)) {
                            tabList.add(tab);
                        }
                    }
                }
            }
        }
        return tabList;
    }

    public static int getMembersGroup(String group, HidePluginsProject plugin){
        List<String> groups = new ArrayList<>();
        for(Player user : Bukkit.getOnlinePlayers()){
            if(!user.isOp()){
                String userGroup = getPlayerGroup(user, plugin);
                groups.add(userGroup);
            }
        }

        groups = groups.stream().filter(x -> x.equals(group)).collect(Collectors.toList());
        return groups.size();
    }
}
