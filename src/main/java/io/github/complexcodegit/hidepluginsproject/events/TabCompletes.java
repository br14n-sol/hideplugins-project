package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.Collections;
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
        if(plugin.getConfig().getBoolean("locked-commands")) {
            if(player.isOp() || player.hasPermission(Objects.requireNonNull(plugin.getConfig().getString("tab-usage-permission")))){
                return false;
            }
            event.getCommands().clear();
            List<String> commands = groupManager.getCommands(player, true);
            event.getCommands().addAll(commands);
        }
        return false;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean tabSuggest(TabCompleteEvent event){
        if(event.getSender() instanceof Player){
            if(event.getBuffer().startsWith("/help")){
                FileConfiguration groups = plugin.getGroups();
                Player player = (Player)event.getSender();
                String group = groupManager.getPlayerGroup(player);
                if(groups.getBoolean("groups."+group+".options.custom-help.enable") &&
                        groups.contains("groups."+group+".options.custom-help.worlds."+player.getWorld().getName())){
                    ArrayList<String> pages = new ArrayList<>(groups.getConfigurationSection("groups."
                            +group+".options.custom-help.worlds."+player.getWorld().getName()+".pages").getKeys(false));
                    Collections.sort(pages);
                    event.setCompletions(pages);
                }
            }
        }
        return false;
    }
}
