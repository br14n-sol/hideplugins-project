package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerCameOut implements Listener {
    private GroupManager groupManager;
    public PlayerCameOut(GroupManager groupManager){
        this.groupManager = groupManager;
    }

    @EventHandler
    public void playerCameOut(PlayerQuitEvent event){
        if(event.getPlayer().hasPermission("hidepluginsproject.hproject") || groupManager.getCommands(event.getPlayer(), false).contains("/hproject")) {
            event.getPlayer().chat("/hproject finish");
        }
    }
}
