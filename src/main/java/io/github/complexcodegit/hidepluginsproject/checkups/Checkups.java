package io.github.complexcodegit.hidepluginsproject.checkups;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;

public class Checkups {
    public static void check(HidePluginsProject plugin){
        checkGroups(plugin);
    }

    public static boolean checkGroups(HidePluginsProject plugin){
        if(GroupManager.getGroups(plugin).contains("default")) {
            return true;
        }
        plugin.console.sendMessage(plugin.colors(""));
        plugin.console.sendMessage(plugin.colors("&e>>>&r &cDisabled &bHidePlugins Project"));
        plugin.console.sendMessage(plugin.colors("&e>>>&r &fPlease define the group 'default' in groups.yml"));
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }
}
