package io.github.complexcodegit.hidepluginsproject.handlers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.objects.PlayerObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class PlayersHandler {
    private static List<PlayerObject> loadPlayers = new ArrayList<>();
    private static HidePluginsProject plugin = JavaPlugin.getPlugin(HidePluginsProject.class);

    public static List<PlayerObject> getPlayers(){
        return loadPlayers;
    }
    public static void addPlayer(PlayerObject playerObject){
        loadPlayers.add(playerObject);
    }
    public static PlayerObject getPlayer(Player player){
        for(PlayerObject playerObject : loadPlayers){
            if(playerObject.getUUID() == player.getUniqueId()){
                return playerObject;
            }
        }
        return null;
    }
    public static void savePlayer(Player player){
        if(getPlayer(player) != null){
            plugin.getPlayers().set("Players."+player.getUniqueId(), getPlayer(player).serialize());
            plugin.savePlayers();
            plugin.reloadPlayers();
            loadPlayers.remove(getPlayer(player));
        }
    }
    public static void savePlayers(){
        for(PlayerObject playerObject : loadPlayers){
            plugin.getPlayers().set("Players."+playerObject.getUUID(), playerObject.serialize());
            plugin.savePlayers();
        }
    }
    public static void setLoadPlayers(){
        for(Player player : Bukkit.getOnlinePlayers()){
            if(!plugin.getPlayers().contains("Players."+player.getUniqueId())){
                PlayerObject playerObject = new PlayerObject(player.getName(), player.getUniqueId());
                playerObject.addIP(player.getAddress().getAddress().getHostAddress());
                loadPlayers.add(playerObject);
            } else {
                PlayerObject playerObject = PlayerObject.deserialize(player.getUniqueId(), plugin.getPlayers());
                playerObject.addIP(player.getAddress().getAddress().getHostAddress());
                loadPlayers.add(playerObject);
            }
        }
    }
}
