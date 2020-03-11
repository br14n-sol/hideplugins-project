package io.github.complexcodegit.hidepluginsproject.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class PlayerChangeWorld implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChangeWorld(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        player.updateCommands();
    }
}
