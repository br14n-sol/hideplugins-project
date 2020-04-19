package io.github.complexcodegit.hidepluginsproject.managers;

import java.util.HashMap;
import java.util.UUID;

public class SelectorManager {
    private static HashMap<UUID, String> groups = new HashMap<>();
    private static HashMap<UUID, String> worlds = new HashMap<>();
    private static HashMap<UUID, String> global = new HashMap<>();

    public static HashMap<UUID, String> getGroups(){
        return groups;
    }
    public static HashMap<UUID, String> getWorlds(){
        return worlds;
    }
    public static HashMap<UUID, String> getGlobal(){
        return global;
    }
}
