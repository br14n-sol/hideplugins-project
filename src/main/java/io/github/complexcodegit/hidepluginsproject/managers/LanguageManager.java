package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager {
    public static String specifiedLanguage(HidePluginsProject plugin){
        return plugin.getConfig().getString("language");
    }

    public static List<String> languagesList(HidePluginsProject plugin){
        return new ArrayList<>(plugin.getLanguages().getConfigurationSection("languages").getKeys(false));
    }

    public static String getTranslate(String path, HidePluginsProject plugin){
        return plugin.getLanguages().getString("languages."+specifiedLanguage(plugin)+"."+path);
    }

    public static boolean checkPrefix(HidePluginsProject plugin){
        return plugin.getConfig().getBoolean("prefix.enable");
    }

    public static String getPrefix(HidePluginsProject plugin){
        return plugin.getConfig().getString("prefix.prefix") + " ";
    }

    public static String translate(String path, HidePluginsProject plugin){
        if(checkPrefix(plugin)){
            return plugin.colors(getPrefix(plugin)+getTranslate(path, plugin));
        }
        return plugin.colors(getTranslate(path, plugin));
    }

    public static String internalTranslate(String path, HidePluginsProject plugin){
        return plugin.colors(plugin.prefix+getTranslate("internal-translations."+path, plugin));
    }

    public static String internalTranslateNoPrefix(String path, HidePluginsProject plugin){
        return plugin.colors(getTranslate("internal-translations."+path, plugin));
    }

    public static List<String> internalTranslateList(String pathList, HidePluginsProject plugin){
        return plugin.getLanguages().getStringList("languages."+specifiedLanguage(plugin)+".internal-translations."+pathList);
    }
}
