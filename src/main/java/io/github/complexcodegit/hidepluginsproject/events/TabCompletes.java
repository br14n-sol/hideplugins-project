package io.github.complexcodegit.hidepluginsproject.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabCompletes implements Listener {
    private HidePluginsProject plugin;

    public TabCompletes(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public boolean onCommandTabSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if(plugin.getConfig().getBoolean("locked-commands")) {
            if(player.isOp() || player.hasPermission("hidepluginsproject.tabusage")){
                event.getCommands().remove("hprojectinternal");
                event.getCommands().remove("hideplugins_project:hprojectinternal");
                event.getCommands().remove("hideplugins_project:hproject");
                event.getCommands().remove("hideplugins_project:hpp");
                event.getCommands().remove("hideplugins_project:hpj");
                return false;
            }
            event.getCommands().clear();
            String group = GroupManager.getPlayerGroup(player, plugin);
            List<String> tabList = GroupManager.getTabList(group, plugin);
            for(String commands : tabList) {
                event.getCommands().add(commands);
            }
        }
        return false;
    }

    @EventHandler
    public void tabSuggest(TabCompleteEvent event){
        Player player = (Player)event.getSender();
        String group = GroupManager.getPlayerGroup(player, plugin);
        if(plugin.getGroups().getBoolean("groups."+group+".custom-help.enable")){
            if(event.getBuffer().startsWith("/help")){
                if(!player.isOp()){
                    ArrayList<String> pages = new ArrayList<>(plugin.getGroups().getConfigurationSection("groups." + group + ".custom-help.pages").getKeys(false));
                    Collections.sort(pages);
                    event.setCompletions(pages);
                }
            }
        }
    }
}
