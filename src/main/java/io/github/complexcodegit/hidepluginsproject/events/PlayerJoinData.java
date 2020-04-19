package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.handlers.PlayersHandler;
import io.github.complexcodegit.hidepluginsproject.objects.PlayerObject;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;

public class PlayerJoinData implements Listener {
    private HidePluginsProject plugin;
    public PlayerJoinData(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerJoinData(PlayerJoinEvent event){
        FileConfiguration players = plugin.getPlayers();
        Player player = event.getPlayer();
        if(!players.contains("Players."+player.getUniqueId())){
            PlayerObject playerObject = new PlayerObject(player.getName(), player.getUniqueId());
            playerObject.addIP(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
            PlayersHandler.addPlayer(playerObject);
        } else {
            PlayerObject playerObject = PlayerObject.deserialize(player.getUniqueId(), players);
            playerObject.addIP(Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
            PlayersHandler.addPlayer(playerObject);
        }
    }
}
