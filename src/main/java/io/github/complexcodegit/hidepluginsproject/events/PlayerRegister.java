package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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
            plugin.getPlayers().set("players." + player.getName() + ".reports", "0");
            plugin.getPlayers().createSection("players." + player.getName() + ".last-command");
            plugin.getPlayers().set("players." + player.getName() + "ips-history", list);
            plugin.getPlayers().createSection("players." + player.getName() + ".command-history");
        }
        if(!plugin.getPlayers().getStringList("players." + player.getName() + ".ips-history").contains(player.getAddress().getAddress().getHostAddress())) {
            List<String> list = new ArrayList<>();
            list.add(player.getAddress().getAddress().getHostAddress());

            plugin.getPlayers().set("players." + player.getName() + "ips-history", list);
        }
        return false;
    }
}
