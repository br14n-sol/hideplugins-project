package io.github.complexcodegit.hidepluginsproject;

import io.github.complexcodegit.hidepluginsproject.commands.MainCommand;
import io.github.complexcodegit.hidepluginsproject.commands.MainCommandSuggest;
import io.github.complexcodegit.hidepluginsproject.events.*;
import io.github.complexcodegit.hidepluginsproject.externals.Metrics;
import io.github.complexcodegit.hidepluginsproject.externals.UpdateCheck;
import io.github.complexcodegit.hidepluginsproject.handlers.GroupsHandler;
import io.github.complexcodegit.hidepluginsproject.handlers.PlayersHandler;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class HidePluginsProject extends JavaPlugin {
    private FileConfiguration groups; private File groupsFile;
    private FileConfiguration messages; private File messagesFile;
    private FileConfiguration players; private File playersFile;

    public void onLoad(){
        registerConfig();
        registerFiles();
        GroupsHandler.read(this);
        if(!Bukkit.getOnlinePlayers().isEmpty()){
            PlayersHandler.setLoadPlayers();
        }
    }
    public void onEnable(){
        ((Logger)LogManager.getRootLogger()).addFilter(new CommandFilter());
        registerEvents();
        registerCommands();
        if(!GroupsHandler.getLoadGroups().isEmpty()){
            Metrics metrics = new Metrics(this, 5432);
            metrics.addCustomChart(new Metrics.SingleLineChart("groups", () -> GroupsHandler.getLoadGroups().size()));
            getLogger().info(GroupsHandler.getLoadGroups().size() + " groups were loaded correctly.");
        }
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
    public void onDisable(){
        if(!PlayersHandler.getPlayers().isEmpty()){
            PlayersHandler.savePlayers();
            getLogger().info(PlayersHandler.getPlayers().size() + " players were saved correctly.");
        }
    }

    private void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerCommandBlock(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerTabSuggest(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerHelpSuggest(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerJoinData(this), this);
        pm.registerEvents(new PlayerQuitData(), this);
        pm.registerEvents(new PlayerDropItem(), this);
        pm.registerEvents(new PlayerEditBook(this, new GroupManager(this)), this);
    }
    private void registerCommands(){
        getCommand("hproject").setExecutor(new MainCommand(this, new GroupManager(this)));
        getCommand("hproject").setTabCompleter(new MainCommandSuggest(new GroupManager(this)));
    }
    public String getPrefix(){
        return "§a§lHPP§7§l>§r ";
    }
    private void registerConfig() {
        File config = new File(getDataFolder(), "config.yml");
        if(!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }
    private void registerFiles(){
        groupsFile = new File(getDataFolder(), "groups.yml");
        playersFile = new File(getDataFolder(), "players.yml");
        messagesFile = new File(getDataFolder(), "messages.yml");
        if(!groupsFile.exists())
            saveResource("groups.yml", false);
        if(!playersFile.exists())
            saveResource("players.yml", false);
        if(!messagesFile.exists())
            saveResource("messages.yml", false);
    }
    public void reloadGroups() {
        if(groups == null) {
            groupsFile = new File(getDataFolder(), "groups.yml");
        }
        groups = YamlConfiguration.loadConfiguration(groupsFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("groups.yml"), "UTF8");
            if(defConfigStream != null){
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                groups.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException ignored) { }
    }
    public void saveGroups() {
        try {
            groups.save(groupsFile);
        } catch (IOException ignored) { }
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
        } catch(UnsupportedEncodingException ignored) {}
    }
    public FileConfiguration getMessages() {
        if(messages == null) {
            reloadMessages();
        }
        return messages;
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
        } catch(UnsupportedEncodingException ignored) {}
    }
    public FileConfiguration getPlayers() {
        if(players == null) {
            reloadPlayers();
        }
        return players;
    }
    public void savePlayers() {
        try {
            players.save(playersFile);
        } catch (IOException ignored) {}
    }
}
