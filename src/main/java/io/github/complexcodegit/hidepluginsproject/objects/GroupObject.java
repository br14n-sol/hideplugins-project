package io.github.complexcodegit.hidepluginsproject.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    private String groupName;
    private boolean paramDefault = false;
    private boolean paramCustomHelp = false;
    private String permission = null;
    private List<GroupObject> inheritanceList = new ArrayList<>();
    private List<WorldObject> worldList = new ArrayList<>();
    private List<Player> playerList = new ArrayList<>();

    public GroupObject(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName(){
        return groupName;
    }
    public void setDefault(Boolean paramDefault){
        this.paramDefault = paramDefault;
    }
    public boolean isDefault(){
        return paramDefault;
    }
    public void setCustomHelp(Boolean paramCustomHelp){
        this.paramCustomHelp = paramCustomHelp;
    }
    public boolean hasCustomHelp(){
        return paramCustomHelp;
    }
    public void setPermission(String permission){
        this.permission = permission;
    }
    public String getPermission(){
        return permission;
    }
    public void addInheritance(GroupObject groupObject){
        inheritanceList.add(groupObject);
    }
    public List<GroupObject> getInheritances(){
        return inheritanceList;
    }
    public void addWorld(WorldObject worldObject){
        worldList.add(worldObject);
    }
    public List<WorldObject> getWorlds(){
        return worldList;
    }
    public WorldObject getWorld(String worldName) {
        for(WorldObject worldObject : worldList){
            if(worldObject.getWorldName().equals(worldName)){
                return worldObject;
            }
        }
        return null;
    }
    public void addPlayer(Player player){
        playerList.add(player);
    }
    public List<Player> getPlayerList(){
        return playerList;
    }
    public void removePlayer(Player player){
        playerList.remove(player);
    }
}
