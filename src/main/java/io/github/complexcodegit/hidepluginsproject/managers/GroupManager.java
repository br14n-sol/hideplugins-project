package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class GroupManager {
    private HidePluginsProject plugin;
    public GroupManager(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    public List<String> getGroups(){
        return new ArrayList<>(Objects.requireNonNull(plugin.getGroups().getConfigurationSection("groups")).getKeys(false));
    }
    public List<String> getInheritsCmds(Player player){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.inheritances")){
            for(String inherit : Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".options.inheritances")).replace(" ", "").split(",")){
                if(plugin.getGroups().contains("groups."+inherit+".worlds."+player.getWorld().getName())) {
                    list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                            +inherit+".worlds."+player.getWorld().getName()+".commands")).replace(" ", "").split(",")));
                }
                if(plugin.getGroups().contains("groups."+inherit+".global")){
                    list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                            +inherit+".global.commands")).replace(" ", "").split(",")));
                }
            }
        }
        if(list.contains("/help") && !plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName()) &&
                plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable")){
            list.remove("/help");
        }
        return list;
    }
    public List<String> getInheritsTabs(Player player){
        List<String> list = new ArrayList<>();
        List<String> result = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.inheritances")){
            for(String inherit : Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".options.inheritances")).replace(" ", "").split(",")){
                if(plugin.getGroups().contains("groups."+inherit+".worlds."+player.getWorld().getName())){
                    list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+inherit
                            +".worlds."+player.getWorld().getName()+".tab")).replace(" ", "").split(",")));
                }
                if(plugin.getGroups().contains("groups."+inherit+".global")){
                    list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+inherit
                            +".global.tab")).replace(" ", "").split(",")));
                }
            }
        }
        if(list.contains("/help") && !plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName()) &&
                plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable")){
            list.remove("/help");
        }
        for(String l : list){
            result.add(l.replaceFirst("/", ""));
        }
        return result;
    }
    public List<String> getTabs(Player player){
        List<String> list = new ArrayList<>();
        List<String> result = getInheritsTabs(player);
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName())){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".worlds."+player.getWorld().getName()+".tab")).replace(" ", "").split(",")));
        }
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".global")){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".global.tab")).replace(" ", "").split(",")));
        }
        if(list.contains("/help") && !plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName())&&
                plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable")){
            list.remove("/help");
        }
        for(String l : list){
            result.add(l.replaceFirst("/", ""));
        }
        return result;
    }
    public List<String> getCommands(Player player){
        List<String> list = getInheritsCmds(player);
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".worlds."+player.getWorld().getName())){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".worlds."+player.getWorld().getName()+".commands")).replace(" ", "").split(",")));
        }
        if(plugin.getGroups().contains("groups."+getPlayerGroup(player)+".global")){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."+getPlayerGroup(player)
                    +".global.commands")).replace(" ", "").split(",")));
        }
        if(list.contains("/help") && !plugin.getGroups().contains("groups."+getPlayerGroup(player)+".options.custom-help.worlds."+player.getWorld().getName()) &&
                plugin.getGroups().getBoolean("groups."+getPlayerGroup(player)+".options.custom-help.enable")){
            list.remove("/help");
        }
        return list;
    }
    public String getDefaultGroup(){
        String def = null;
        for(String group : getGroups()){
            if(plugin.getGroups().contains("groups."+group+".options.default") &&
                    plugin.getGroups().getBoolean("groups."+group+".options.default")){
                def = group;
            }
        }
        return def;
    }
    public List<String> getGroupsPerms(){
        List<String> permAndGroup = new ArrayList<>();
        for(String group : getGroups()){
            permAndGroup.add("hidepjpremium.group."+group);
        }
        return permAndGroup;
    }
    public String getPlayerGroupPerm(Player player) {
        String permission = null;
        for(String perm : getGroupsPerms()) {
            if(player.hasPermission(perm)){
                permission = perm;
            }
        }
        if(permission == null && getDefaultGroup() != null){
            permission = "hidepjpremium.group."+getDefaultGroup();
        }
        return permission;
    }
    public String getPlayerGroup(Player player) {
        return getPlayerGroupPerm(player).substring(20);
    }
    public List<String> getWorlds(){
        List<String> worlds = new ArrayList<>();
        for(World world : Bukkit.getWorlds()){
            worlds.add(world.getName());
        }
        return worlds;
    }
    public List<String> getGroupWorlds(String group){
        List<String> worlds = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".worlds")){
            worlds.addAll(Objects.requireNonNull(plugin.getGroups().getConfigurationSection("groups."
                    +group+".worlds")).getKeys(false));
        }
        return worlds;
    }
    public List<String> getGroupWorldCommands(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".worlds."+world)){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                    +group+".worlds."+world+".commands")).replace(" ", "").split(",")));
        }
        return list;
    }
    public List<String> getGroupWorldTab(String group, String world){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".worlds."+world)){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                    +group+".worlds."+world+".tab")).replace(" ", "").split(",")));
        }
        return list;
    }
    public List<String> getGroupGlobalCommands(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".global")){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                    +group+".global.commands")).replace(" ", "").split(",")));
        }
        return list;
    }
    public List<String> getGroupGlobalTab(String group){
        List<String> list = new ArrayList<>();
        if(plugin.getGroups().contains("groups."+group+".global")){
            list.addAll(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                    +group+".global.tab")).replace(" ", "").split(",")));
        }
        return list;
    }
    public List<String> getInherit(String group){
        return new ArrayList<>(Arrays.asList(Objects.requireNonNull(plugin.getGroups().getString("groups."
                +group+".options.inheritances")).replace(" ", "").split(",")));
    }
}
