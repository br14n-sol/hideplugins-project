package io.github.complexcodegit.hidepluginsproject.checkups;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.LanguageManager;

import java.util.List;

public class Checkups {
    public static void check(HidePluginsProject plugin){
        checkGroups(plugin);
        checkLanguage(plugin);
    }

    public static boolean checkLanguage(HidePluginsProject plugin){
        List<String> langList = LanguageManager.getLanguagesList(plugin);
        if(langList.contains(plugin.getConfig().getString("language"))){
            return true;
        }
        plugin.console.sendMessage("");
        plugin.console.sendMessage(plugin.colors("&6&l&m>>>&r &cDisabled &bHidePlugins Project"));
        plugin.console.sendMessage(plugin.colors("&6&l&m>>>&r &fPlease select a correct language."));
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }

    public static boolean checkGroups(HidePluginsProject plugin){
        if(GroupManager.getGroups(plugin).contains("default")) {
            return true;
        }
        plugin.console.sendMessage(plugin.colors(""));
        plugin.console.sendMessage(plugin.colors("&6&l&m>>>&r &cDisabled &bHidePlugins Project"));
        plugin.console.sendMessage(plugin.colors("&6&l&m>>>&r &fPlease define the group 'default' in groups.yml"));
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }
}
