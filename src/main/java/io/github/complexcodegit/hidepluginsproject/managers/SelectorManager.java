package io.github.complexcodegit.hidepluginsproject.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class SelectorManager {
    private HashMap<UUID, String> selectGroup = new HashMap<>();
    private HashMap<UUID, String> selectWorld = new HashMap<>();
    private HashMap<UUID, String> selectGlobal = new HashMap<>();

    private HashMap<UUID, String> getGroup(){
        return selectGroup;
    }
    private HashMap<UUID, String> getWorld(){
        return selectWorld;
    }
    private HashMap<UUID, String> getGlobal(){
        return selectGlobal;
    }

    public boolean containsGroup(Player key){
        return getGroup().containsKey(key.getUniqueId());
    }
    public boolean containsWorld(Player key){
        return getWorld().containsKey(key.getUniqueId());
    }
    public boolean containsGlobal(Player key){
        return getGlobal().containsKey(key.getUniqueId());
    }

    public void putGroup(Player key, String value){
        getGroup().put(key.getUniqueId(), value);
    }
    public void putWorld(Player key, String value){
        getWorld().put(key.getUniqueId(), value);
    }
    public void putGlobal(Player key, String value){
        getGlobal().put(key.getUniqueId(), value);
    }

    public String getGroup(Player key){
        return getGroup().get(key.getUniqueId());
    }
    public String getWorld(Player key){
        return getWorld().get(key.getUniqueId());
    }
    public String getGlobal(Player key){
        return getGlobal().get(key.getUniqueId());
    }

    public void removeGroup(Player key){
        getGroup().remove(key.getUniqueId());
    }
    public void removeWorld(Player key){
        getWorld().remove(key.getUniqueId());
    }
    public void removeGlobal(Player key){
        getGlobal().remove(key.getUniqueId());
    }
}
