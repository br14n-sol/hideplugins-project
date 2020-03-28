package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

import java.io.File;

public class FileManager {
    public static void save(HidePluginsProject plugin){
        File groupd = new File(plugin.getDataFolder(), "groups.yml");
        File playerd = new File(plugin.getDataFolder(), "players.yml");
        File messagesd = new File(plugin.getDataFolder(), "messages.yml");
        File commandsd = new File(plugin.getDataFolder(), "commands.yml");
        if(!groupd.exists())
            plugin.saveResource("groups.yml", false);
        if(!playerd.exists())
            plugin.saveResource("players.yml", false);
        if(!messagesd.exists())
            plugin.saveResource("messages.yml", false);
        if(!commandsd.exists())
            plugin.saveResource("commands.yml", false);
    }
}
