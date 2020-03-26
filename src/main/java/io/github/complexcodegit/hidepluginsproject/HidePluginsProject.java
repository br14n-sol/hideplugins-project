package io.github.complexcodegit.hidepluginsproject;

import io.github.complexcodegit.hidepluginsproject.commands.ChiefCommand;
import io.github.complexcodegit.hidepluginsproject.events.*;
import io.github.complexcodegit.hidepluginsproject.managers.FileManager;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.utils.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"ConstantConditions", "CharsetObjectCanBeUsed"})
public class HidePluginsProject extends JavaPlugin implements Listener {
    private FileConfiguration groups;
    private File groupsFile;
    private FileConfiguration messages;
    private File messagesFile;
    private FileConfiguration commands;
    private File commandsFile;
    private FileConfiguration players;
    private File playersFile;
    public String prefix = "§a§lHPP§7§l>§r ";

    public void onEnable(){
        registerConfig();
        commands();
        FileManager.save(this);
        registerEvents();
        registerCommands();
        getLogger().info("§aHidePlugins Project is enabled.");
    }
    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerEditBook(this, new GroupManager(this)), this);
        pm.registerEvents(new LockedCommands(this, new GroupManager(this)), this);
        pm.registerEvents(new TabCompletes(this, new GroupManager(this)), this);
        pm.registerEvents(new PlayerChangeWorld(), this);
        pm.registerEvents(new PlayerJoinData(this), this);
    }
    private void registerCommands(){
        getCommand("hproject").setExecutor(new ChiefCommand(this, new GroupManager(this)));
    }
    private void commands(){
        List<String> cmds = new ArrayList<>(getCommands().getConfigurationSection("custom-commands").getKeys(false));
        if(cmds != null && !cmds.isEmpty()){
            try {
                final Field serverCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                serverCommandMap.setAccessible(true);

                CommandMap commandMap = (CommandMap)serverCommandMap.get(Bukkit.getServer());
                for(String command : cmds){
                    if(getCommands().getConfigurationSection("custom-commands."+command) != null){
                        String description = getCommands().getString("custom-commands."+command+".description");
                        String usageMessage = getCommands().getString("custom-commands."+command+".usageMessage");
                        List<String> aliases = getConfig().getStringList("custom-commands."+command+".aliases");
                        if(description != null && usageMessage != null){
                            commandMap.register(command.replaceFirst("/", ""), new Command(command.replaceFirst("/", ""), description, usageMessage, aliases));
                        }
                    }
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
    public void reloadCommands() {
        if(commands == null) {
            commandsFile = new File(getDataFolder(), "commands.yml");
        }

        commands = YamlConfiguration.loadConfiguration(commandsFile);
        try {
            Reader defConfigStream = new InputStreamReader(getResource("commands.yml"), "UTF8");
            if(defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                commands.setDefaults(defConfig);
            }
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    public FileConfiguration getCommands() {
        if(commands == null) {
            reloadCommands();
        }
        return commands;
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
