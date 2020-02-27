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

    public static boolean checkGroups(HidePluginsProject plugin){
        if(GroupManager.getGroups(plugin).contains("default")) {
            return true;
        }
        plugin.console.sendMessage(plugin.colors("&e>>>&r &cDisabled &bHidePlugins Project"));
        plugin.console.sendMessage(plugin.colors("&e>>>&r &fPlease define the group 'default' in groups.yml"));
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }

    public static boolean checkLanguage(HidePluginsProject plugin){
        List<String> list = LanguageManager.languagesList(plugin);
        if(list.contains(LanguageManager.specifiedLanguage(plugin))){
            return true;
        }
        plugin.console.sendMessage(plugin.colors("&e>>>&r &cDisabled &bHidePlugins Project"));
        plugin.console.sendMessage(plugin.colors("&e>>>&r &fPlease define a valid language in config.yml."));
        plugin.getPluginLoader().disablePlugin(plugin);
        return false;
    }
}
