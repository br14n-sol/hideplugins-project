package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerJoinData implements Listener {
    private HidePluginsProject plugin;
    public PlayerJoinData(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    @SuppressWarnings("ConstantConditions")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoinData(PlayerJoinEvent event){
        FileConfiguration players = plugin.getPlayers();
        FileConfiguration config = plugin.getConfig();
        FileConfiguration groups = plugin.getGroups();
        Player player = event.getPlayer();
        if(!players.contains("Players."+player.getName())){
            players.createSection("Players."+player.getName());
            players.set("Players."+player.getName()+".reports", 0);
            players.set("Players."+player.getName()+".last-command", "none");
            players.set("Players."+player.getName()+".ips-history", "");
            if(players.getString("Players." + player.getName() + ".ips-history") != null){
                List<String> ips = new ArrayList<>();
                ips.add(player.getAddress().getAddress().getHostAddress());
                String result = String.join("", ips);
                players.set("Players."+player.getName()+".ips-history", result);
            } else {
                String history = players.getString("Players."+player.getName()+".ips-history").replace(" ", "");
                List<String> ips = new ArrayList<>();
                ips.add(player.getAddress().getAddress().getHostAddress());
                ips.addAll(Arrays.asList(history.split(",")));
                String result = String.join(", ", ips);
                players.set("Players."+player.getName()+".ips-history", result);
            }
            if(config.getBoolean("playerCommandHistory")){
                players.set("Players."+player.getName()+".command-history", "");
            }
            plugin.savePlayers();
        } else if(players.contains("Players."+player.getName())){
            String history = players.getString("Players."+player.getName()+".ips-history").replace(" ", "");
            List<String> ips = new ArrayList<>(Arrays.asList(history.split(",")));
            if(!ips.contains(player.getAddress().getAddress().getHostAddress())){
                ips.add(player.getAddress().getAddress().getHostAddress());
                String result = String.join(", ", ips);
                players.set("Players."+player.getName()+".ips-history", result);
                plugin.savePlayers();
            }
        }
    }
}
