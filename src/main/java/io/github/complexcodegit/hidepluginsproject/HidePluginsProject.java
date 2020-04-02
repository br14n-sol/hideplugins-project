package io.github.complexcodegit.hidepluginsproject;

import io.github.complexcodegit.hidepluginsproject.commands.ChiefCommand;
import io.github.complexcodegit.hidepluginsproject.commands.ChiefCommandCompleter;
import io.github.complexcodegit.hidepluginsproject.external.UpdateCheck;
import io.github.complexcodegit.hidepluginsproject.filters.CommandFilter;
import io.github.complexcodegit.hidepluginsproject.managers.FileManager;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.managers.MetricManager;
import io.github.complexcodegit.hidepluginsproject.managers.SelectorManager;
import io.github.complexcodegit.hidepluginsproject.events.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class HidePluginsProject extends JavaPlugin {
    private FileConfiguration groups;
    private File groupsFile;
    private FileConfiguration messages;
    private File messagesFile;
    private FileConfiguration players;
    private File playersFile;
    public String prefix = "§a§lHPP§7§l>§r ";

    public void onEnable(){
        ((Logger)LogManager.getRootLogger()).addFilter(new CommandFilter());
        registerConfig();
        FileManager.save(this);
        registerEvents();
        registerCommands();
        getLogger().finest("HidePlugins Project is enabled.");
        MetricManager.customMetrics(this, new GroupManager(this));
        if(getConfig().getBoolean("updates")){
            UpdateCheck updater = new UpdateCheck(this);
            try {
                if(updater.checkForUpdates())
                    getLogger().warning("An update was found! New version: " + updater.getLatestVersion() + " download: " + updater.getResourceURL());
            } catch (Exception e) {
                getLogger().severe("Could not check for updates! Stacktrace:");
                e.printStackTrace();
            }
        }
    }
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TabCompletes(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerEditBook(this, new GroupManager(this)), this);
        pm.registerEvents(new LockedCommands(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerChangeWorld(), this);
        pm.registerEvents(new PlayerJoinData(this), this);
        pm.registerEvents(new PlayerCameOut(new GroupManager(this)), this);
    }
    private void registerCommands(){
        getCommand("hproject").setExecutor(new ChiefCommand(this, new GroupManager(this), new SelectorManager()));
        getCommand("hproject").setTabCompleter(new ChiefCommandCompleter(new GroupManager(this)));
    }
    private void registerConfig() {
        File config = new File(getDataFolder(), "config.yml");
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
    public void saveGroups() {
        try {
            groups.save(groupsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getGroups() {
        if(groups == null) {
            reloadGroups();
        }
        return groups;
    }
    public void reloadMessages() {
        if(messages == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("messages.yml"), "UTF8");
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                messages.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getMessages() {
        if(messages == null) {
            reloadMessages();
        }
        return messages;
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
    public void savePlayers() {
        try {
            players.save(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
