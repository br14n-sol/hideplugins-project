package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PlayerDataManager {
    public static boolean checkPlayerRecord(String player, HidePluginsProject plugin){
        return playersRecord(plugin).contains(player);
    }
    public static boolean checkPlayerOnline(String player){
        return playersOnline().contains(player);
    }
    public static ArrayList<String> playersRecord(HidePluginsProject plugin){
        return new ArrayList<>(plugin.getPlayers().getConfigurationSection("Players").getKeys(false));
    }
    public static ArrayList<String> playersOnline(){
        ArrayList<String> playerList = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            playerList.add(player.getName());
        }
        return playerList;
    }
}
