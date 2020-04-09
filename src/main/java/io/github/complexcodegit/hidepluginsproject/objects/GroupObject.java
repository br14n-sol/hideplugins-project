package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    private String groupName;
    private int id;
    private boolean paramDefault = false;
    private boolean paramCustomHelp = false;
    private String permission = null;
    private List<GroupObject> inheritanceList = new ArrayList<>();
    private List<WorldObject> worldList = new ArrayList<>();
    private GlobalObject globalObject = new GlobalObject();

    public GroupObject(String groupName, int id){
        this.groupName = groupName;
        this.id = id;
    }

    public String getGroupName(){
        return groupName;
    }
    public int getId(){
        return id;
    }
    public void setGlobal(GlobalObject globalObject){
        this.globalObject = globalObject;
    }
    public GlobalObject getGlobal(){
        return globalObject;
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
}
