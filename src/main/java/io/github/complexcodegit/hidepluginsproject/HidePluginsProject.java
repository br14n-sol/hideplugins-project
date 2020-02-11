package io.github.complexcodegit.hidepluginsproject;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import io.github.complexcodegit.hidepluginsproject.checkups.Checkups;
import io.github.complexcodegit.hidepluginsproject.external.Updater;
import io.github.complexcodegit.hidepluginsproject.managers.FileManager;
import io.github.complexcodegit.hidepluginsproject.managers.MetricManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.complexcodegit.hidepluginsproject.events.TabCompletes;
import io.github.complexcodegit.hidepluginsproject.events.LockedCommands;
import io.github.complexcodegit.hidepluginsproject.utils.Title;

public class HidePluginsProject extends JavaPlugin implements Listener {
    private FileConfiguration groups;
    private File groupsFile;
    private FileConfiguration languages;
    private File languagesFile;
    private FileConfiguration players;
    private File playersFile;

    PluginDescriptionFile pdffile = getDescription();

    String rutaConfig;
    String version = pdffile.getVersion();

    public ConsoleCommandSender console = Bukkit.getConsoleSender();
    
    public void onEnable() {
        registerConfig();

        Updater.checkedUpdate(this, version);
        FileManager.registerFiles(this);
        Checkups.check(this);
        MetricManager.customMetrics(this);

        registerEvents();

        console.sendMessage("");
        console.sendMessage(colors("&e>>> &aEnable &bHidePlugins Project"));
        console.sendMessage(colors("&e>>>    &aVersion &f>> &b" + version));
        console.sendMessage(colors("&e>>>    &aAuthor  &f>> &bComplexCode"));
        console.sendMessage("");
    }

    public String colors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String messages(String s) {
        return colors(getLang().getString("languages." + getConfig().getString("language") + "." + s));
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new LockedCommands(this, new Title()), this);
        pm.registerEvents(new TabCompletes(this), this);
    }

    public void registerConfig() {
        File config = new File(getDataFolder(), "config.yml");
        rutaConfig = config.getPath();
        if(!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    public void reloadGroups() {
        if(groups == null) {
            groupsFile = new File(getDataFolder(), "groups.yml");
        }

        groups = YamlConfiguration.loadConfiguration(groupsFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("groups.yml"), "UTF8");
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                groups.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getGroups() {
        if(groups == null) {
            reloadGroups();
        }
        return groups;
    }

    public FileConfiguration getLang() {
        if(languages == null) {
            reloadLang();
        }
        return languages;
    }

    public void reloadLang() {
        if(languages == null) {
            languagesFile = new File(getDataFolder(), "languages.yml");
        }

        languages = YamlConfiguration.loadConfiguration(languagesFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("languages.yml"), "UTF8");
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                languages.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getPlayers() {
        if(players == null) {
            reloadPlayers();
        }
        return players;
    }

    public void reloadPlayers() {
        if(players == null) {
            playersFile = new File(getDataFolder(), "players.yml");
        }

        players = YamlConfiguration.loadConfiguration(playersFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("players.yml"), "UTF8");
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                players.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}