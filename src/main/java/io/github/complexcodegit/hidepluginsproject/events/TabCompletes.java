package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.List;
import java.util.Objects;

public class TabCompletes implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public TabCompletes(HidePluginsProject plugin, GroupManager groupManager){
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean onCommandTabSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if(plugin.getConfig().getBoolean("lockedCommands")) {
            if(player.isOp() || player.hasPermission(Objects.requireNonNull(plugin.getConfig().getString("tabUsagePermission")))){
                return false;
            }
            event.getCommands().clear();
            List<String> commands = groupManager.getCommands(player, true);
            event.getCommands().addAll(commands);
        }
        return false;
    }
    @EventHandler
    public boolean tabComplete(TabCompleteEvent event){
        if(event.getSender() instanceof Player){
            Player player = (Player)event.getSender();
            GroupObject group = GroupManager.getPlayerGroup(player);
            if(plugin.getConfig().getBoolean("lockedCommands") && group.hasCustomHelp() && event.getBuffer().startsWith("/help")){
                event.setCancelled(true);
            }
        }
        return false;
    }
}
