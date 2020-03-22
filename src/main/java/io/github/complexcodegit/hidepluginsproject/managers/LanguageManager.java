package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ConstantConditions"})
public class LanguageManager {
    private HidePluginsProject plugin;
    public LanguageManager(HidePluginsProject plugin){
        this.plugin = plugin;
    }

    public static boolean checkLanguage(HidePluginsProject plugin){
        return languages(plugin).contains(specifiedLang(plugin));
    }

    public static List<String> languages(HidePluginsProject plugin){
        return new ArrayList<>(plugin.getLanguages().getConfigurationSection("languages").getKeys(false));
    }

    public static String specifiedLang(HidePluginsProject plugin){
        return plugin.getConfig().getString("language");
    }

    public String getMessage(String path){
        if(checkPrefix())
            return plugin.colors(getPrefix()+" "+plugin.getLanguages().getString("languages."+plugin.getConfig().getString("language")+"."+path));
        return plugin.colors(plugin.getLanguages().getString("languages."+plugin.getConfig().getString("language")+"."+path));
    }

    public String getInternal(String path){
        return plugin.colors(plugin.getLanguages().getString("languages."+plugin.getConfig().getString("language")+"."+path));
    }

    public List<String> getList(String path){
        return plugin.getLanguages().getStringList("languages."+plugin.getConfig().getString("language")+"."+path);
    }

    public boolean checkPrefix(){
        return plugin.getConfig().getBoolean("prefix.enable");
    }

    public String getPrefix() {
        return plugin.getConfig().getString("prefix.prefix");
    }
}