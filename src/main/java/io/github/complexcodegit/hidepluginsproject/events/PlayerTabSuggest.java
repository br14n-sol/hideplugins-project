package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Objects;

public class PlayerTabSuggest implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public PlayerTabSuggest(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean playerTabSend(PlayerCommandSendEvent event){
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();
        if(config.getBoolean("lockedCommands")) {
            if(player.isOp() || player.hasPermission(Objects.requireNonNull(config.getString("tabUsagePermission")))){
                return false;
            }
            event.getCommands().clear();
            event.getCommands().addAll(groupManager.getCommands(player, true));
        }
        return false;
    }
}
