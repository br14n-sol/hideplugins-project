package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager {
    public static List<String> getLanguagesList(HidePluginsProject plugin){
        List<String> langList = new ArrayList<>();
        for(String key : plugin.getLang().getConfigurationSection("languages.").getKeys(false)) {
            if(!langList.contains(key)) {
                langList.add(key);
            }
        }
        return langList;
    }
}
