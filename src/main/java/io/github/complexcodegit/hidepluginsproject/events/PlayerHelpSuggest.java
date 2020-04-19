package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerHelpSuggest implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public PlayerHelpSuggest(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean tabComplete(TabCompleteEvent event){
        if(event.getSender() instanceof Player){
            Player player = (Player)event.getSender();
            GroupObject group = groupManager.getPlayerGroup(player);
            if(plugin.getConfig().getBoolean("lockedCommands") && group.hasHelp()
                    && event.getBuffer().startsWith("/help") && group.getWorld(player.getWorld().getName()) != null){
                List<String> pages = new ArrayList<>();
                group.getWorld(player.getWorld().getName()).getPages().forEach(pageObject -> pages.add(pageObject.getNumber()));
                event.setCompletions(pages);
            }
        }
        return false;
    }
}
