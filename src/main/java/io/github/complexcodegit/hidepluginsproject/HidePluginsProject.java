package io.github.complexcodegit.hidepluginsproject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import io.github.complexcodegit.hidepluginsproject.checkups.Checkups;
import io.github.complexcodegit.hidepluginsproject.commands.ChiefCommand;
import io.github.complexcodegit.hidepluginsproject.commands.ChiefCommandCompleter;
import io.github.complexcodegit.hidepluginsproject.commands.InternalCommand;
import io.github.complexcodegit.hidepluginsproject.events.PlayerRegister;
import io.github.complexcodegit.hidepluginsproject.events.TabCompletes_v1_12_R1;
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
    private FileConfiguration players;
    private File playersFile;

    public String prefix = "&6&l&m[&e&lHPP&6&l&m]&r ";

    PluginDescriptionFile pdffile = getDescription();

    String rutaConfig;
    public String version = pdffile.getVersion();

    public ConsoleCommandSender console = Bukkit.getConsoleSender();
    String a = getServer().getClass().getPackage().getName();
    public String serverVersion = a.substring(a.lastIndexOf('.') + 1);

    public void onEnable() {
        registerConfig();

        Updater.checkedUpdate(this, version);
        FileManager.registerFiles(this);
        Checkups.check(this);
        MetricManager.customMetrics(this);

        registerEvents();
        registerCommands();

        console.sendMessage("");
        console.sendMessage(colors(prefix + "&aEnable &bHidePlugins Project"));
        console.sendMessage(colors(prefix + "    &aVersion: &f" + version));
        console.sendMessage(colors(prefix + "    &aAuthor:  &fComplexCode"));
        console.sendMessage("");
    }

    public String colors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new LockedCommands(this, new Title()), this);
        if(serverVersion.equalsIgnoreCase("v1_12_R1")){
            pm.registerEvents(new TabCompletes_v1_12_R1(this), this);
        } else {
            pm.registerEvents(new TabCompletes(this), this);
        }
        pm.registerEvents(new PlayerRegister(this), this);
    }

    public void registerCommands(){
        getCommand("hproject").setExecutor(new ChiefCommand(this));
        getCommand("hproject").setTabCompleter(new ChiefCommandCompleter(this));
        getCommand("hprojectinternal").setExecutor(new InternalCommand(this));
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