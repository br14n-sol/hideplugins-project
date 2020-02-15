package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerRegister implements Listener {
    private HidePluginsProject plugin;

    public PlayerRegister(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public boolean playerJoinData(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(plugin.getPlayers().getString("players." + player.getName()) == null){
            List<String> list = new ArrayList<>();
            list.add(player.getAddress().getAddress().getHostAddress());

            plugin.getPlayers().createSection("players." + player.getName());
            plugin.getPlayers().set("players." + player.getName() + ".reports", 0);
            plugin.getPlayers().set("players." + player.getName() + ".group", "default");
            plugin.getPlayers().set("players." + player.getName() + ".last-command", "none");
            plugin.getPlayers().set("players." + player.getName() + ".ips-history", list);
            plugin.getPlayers().createSection("players." + player.getName() + ".command-history");
            plugin.savePlayers();
        }
        if(!plugin.getPlayers().getStringList("players." + player.getName() + ".ips-history").contains(player.getAddress().getAddress().getHostAddress())) {
            List<String> list = plugin.getPlayers().getStringList("players." + player.getName() + ".ips-history");
            list.add(player.getAddress().getAddress().getHostAddress());

            plugin.getPlayers().set("players." + player.getName() + ".ips-history", list);
            plugin.savePlayers();
        }
        return false;
    }

    @EventHandler
    public boolean playerQuitData(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayers().set("players." + player.getName() + ".group", GroupManager.getPlayerGroup(player, plugin));
        plugin.savePlayers();
        return false;
    }
}
