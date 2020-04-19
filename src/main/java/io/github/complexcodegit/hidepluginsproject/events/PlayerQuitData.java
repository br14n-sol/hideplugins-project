package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.handlers.PlayersHandler;
import io.github.complexcodegit.hidepluginsproject.managers.SelectorManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitData implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerComeOut(PlayerQuitEvent event){
        if(SelectorManager.getGroups().containsKey(event.getPlayer().getUniqueId())){
            event.getPlayer().chat("/hproject finish");
        }
        PlayersHandler.savePlayer(event.getPlayer());
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChangeWorld(PlayerChangedWorldEvent event){
        event.getPlayer().updateCommands();
    }
}
