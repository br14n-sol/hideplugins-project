package io.github.complexcodegit.hidepluginsproject.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;

public class TabCompletes implements Listener {
    private HidePluginsProject plugin;

    public TabCompletes(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public boolean onCommandTabSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if(plugin.getConfig().getBoolean("locked-commands")) {
            if(player.isOp()){
                return false;
            }

            event.getCommands().clear();
            String permission = GroupManager.getPlayerGroupPermission(player, plugin);
            if(!permission.isEmpty() || !(permission == null)) {
                String group = GroupManager.getPlayerGroup(player, plugin);
                for(String commands : plugin.getGroups().getStringList("groups."+group+".commands")) {
                    event.getCommands().add(commands);
                }
            }
        }
        return false;
    }
}
