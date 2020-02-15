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
                event.getCommands().remove("hprojectinternal");
                event.getCommands().remove("hideplugins_project:hprojectinternal");
                event.getCommands().remove("hideplugins_project:hproject");
                event.getCommands().remove("hideplugins_project:hpp");
                event.getCommands().remove("hideplugins_project:hpj");
                return false;
            }

            event.getCommands().clear();
            String group = GroupManager.getPlayerGroup(player, plugin);
            for(String commands : plugin.getGroups().getStringList("groups."+group+".commands")) {
                event.getCommands().add(commands);
            }
        }
        return false;
    }
}
