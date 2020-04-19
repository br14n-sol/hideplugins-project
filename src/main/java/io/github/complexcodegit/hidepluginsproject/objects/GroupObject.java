package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class GroupObject {
    private final String paramName;
    private final int paramID;
    private static int paramIdNext = 0;
    private String paramPermission = null;
    private boolean paramDefault = false;
    private boolean paramHelp = false;
    private List<GroupObject> paramInheritances = new ArrayList<>();
    private List<WorldObject> paramWorlds = new ArrayList<>();
    private GlobalObject paramGlobal = new GlobalObject();

    public GroupObject(String paramName){
        this.paramName = paramName;
        paramID = paramIdNext;
        paramIdNext++;
    }

    public String getName(){
        return paramName;
    }
    public int getID(){
        return paramID;
    }
    public String getPermission(){
        return paramPermission;
    }
    public boolean isDefault(){
        return paramDefault;
    }
    public boolean hasHelp(){
        return paramHelp;
    }
    public List<GroupObject> getInheritances(){
        return paramInheritances;
    }
    public List<WorldObject> getWorlds(){
        return paramWorlds;
    }
    public WorldObject getWorld(String paramName){
        for(WorldObject worldObject : paramWorlds){
            if(worldObject.getName().equals(paramName)){
                return worldObject;
            }
        }
        return null;
    }
    public GlobalObject getGlobal(){
        return paramGlobal;
    }
    public void setPermission(String paramPermission){
        this.paramPermission = paramPermission;
    }
    public void setDefault(boolean paramDefault){
        this.paramDefault = paramDefault;
    }
    public void setHelp(boolean paramHelp){
        this.paramHelp = paramHelp;
    }
    public void addInheritance(GroupObject groupObject){
        paramInheritances.add(groupObject);
    }
    public void addWorld(WorldObject worldObject){
        paramWorlds.add(worldObject);
    }
    public void setGlobal(GlobalObject globalObject){
        paramGlobal = globalObject;
    }
}
