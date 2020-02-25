package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.*;

public class TabCompletes_v1_12_R1 implements Listener {
    private HidePluginsProject plugin;

    public TabCompletes_v1_12_R1(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public boolean onCommandTabSend(TabCompleteEvent event) {
        Player player = (Player)event.getSender();
        if(plugin.getConfig().getBoolean("locked-commands")) {
            if(player.isOp()){
                event.getCompletions().remove("/hprojectinternal");
                event.getCompletions().remove("/hideplugins_project:hprojectinternal");
                event.getCompletions().remove("/hideplugins_project:hproject");
                event.getCompletions().remove("/hideplugins_project:hpp");
                event.getCompletions().remove("/hideplugins_project:hpj");
                return false;
            }
            String group = GroupManager.getPlayerGroup(player, plugin);
            List<String> tabs = plugin.getGroups().getStringList("groups." + group + ".tab-completes");
            List<String> completetions = new ArrayList<>();
            for(int i=0; i < tabs.size(); i++) {
                completetions.add("/"+tabs.get(i));
            }
            event.setCompletions(completetions);
        }
        return false;
    }
}
