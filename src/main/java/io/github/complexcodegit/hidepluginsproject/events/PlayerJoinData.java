package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PlayerJoinData implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public PlayerJoinData(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoinData(PlayerJoinEvent event){
        FileConfiguration players = plugin.getPlayers();
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();
        if(!players.contains("Players."+player.getName())){
            players.createSection("Players."+player.getName());
            players.set("Players."+player.getName()+".reports", 0);
            if(groupManager.getDefaultGroup() != null){
                players.set("Players."+player.getName()+".group", groupManager.getPlayerGroup(player));
            } else {
                players.set("Players."+player.getName()+".group", "none");
            }
            players.set("Players."+player.getName()+".last-command", "none");

            players.set("Players."+player.getName()+".ips-history", "[]");
            if(Objects.equals(players.getString("Players." + player.getName() + ".ips-history"), "[]")){
                List<String> ips = new ArrayList<>();
                ips.add(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
                String result = String.join("", ips);
                players.set("Players."+player.getName()+".ips-history", result);
            } else {
                String history = Objects.requireNonNull(players.getString("Players."+player.getName()+".ips-history")).replace(" ", "");
                List<String> ips = new ArrayList<>();
                ips.add(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
                ips.addAll(Arrays.asList(history.split(",")));
                String result = String.join(", ", ips);
                players.set("Players."+player.getName()+".ips-history", result);
            }

            if(config.getBoolean("player-command-history")){
                players.set("Players."+player.getName()+".command-history", "[]");
            }
            plugin.savePlayers();
        } else if(players.contains("Players."+player.getName())){
            String history = Objects.requireNonNull(plugin.getPlayers().getString("Players."+player.getName()+".ips-history"))
                    .replace("[", "").replace("]", "").replace(" ", "");
            List<String> ips = new ArrayList<>(Arrays.asList(history.split(",")));
            if(!ips.contains(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress())){
                ips.add(player.getAddress().getAddress().getHostAddress());
                String result = String.join(", ", ips);
                players.set("Players."+player.getName()+".ips-history", result);
                plugin.savePlayers();
            }
        }
    }
}
