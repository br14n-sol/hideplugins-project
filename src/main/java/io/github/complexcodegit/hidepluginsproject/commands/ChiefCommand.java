package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.messages.InteractText;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class ChiefCommand implements CommandExecutor {
    private HidePluginsProject plugin;

    public ChiefCommand(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(!command.getName().equalsIgnoreCase("hproject")){
            return false;
        }

        if(!(sender instanceof Player)){
            if(args.length == 0) {
                plugin.console.sendMessage(plugin.colors(plugin.prefix + "&cUse &f/hproject help"));
            } else if(args.length == 1){
                if(args[0].equalsIgnoreCase("help")){

                }
            }
            return false;
        }

        Player player = (Player)sender;
        List<String> hprojectGroup = GroupManager.getCommandsList(player, plugin);
        if(!player.hasPermission("hidepluginsproject.hproject") && !hprojectGroup.contains("/hproject")){
            return false;
        }

        if(args.length == 0){
            InteractText.send("useCommand", player, plugin);
        } else if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){

            } else if(args[0].equalsIgnoreCase("groups")){
                player.sendMessage("");
                player.sendMessage(plugin.colors(plugin.prefix + "&fGroups List:"));

                InteractText.send("listGroups", player, plugin);
            } else {
                InteractText.send("commandExistUse", player, plugin);
            }
        } else if(args.length == 2){
            if(args[0].equalsIgnoreCase("group")){
                List<String> groups = GroupManager.getGroups(plugin);
                String arg = args[1];
                if(groups.contains(arg)){
                    if(args[1].equalsIgnoreCase(arg)){
                        player.sendMessage("");
                        player.sendMessage(plugin.colors(plugin.prefix + "&b"+ arg + " &fgroup information."));
                        if(arg.equalsIgnoreCase("default")){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fPermission: &bYou don't need permission."));
                        } else{
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fPermission: &b" + GroupManager.getGroupPermission(arg)));
                        }
                        player.sendMessage(plugin.colors("&6&l&m>>>&r &fOnline members: &b" + GroupManager.getMembersGroup(arg , plugin)));
                        player.sendMessage(plugin.colors("&6&l&m>>>&r &fCommands List:"));

                        List<String> list = GroupManager.getGroupCommandsList(arg, plugin);
                        String result = String.join("&f, &b", list);

                        player.sendMessage(plugin.colors("&b" + result));
                    }
                } else{
                    player.sendMessage(plugin.colors(plugin.prefix + "&cThe &b" + arg + " &cgroup not exist."));
                }
            } else if(args[0].equalsIgnoreCase("player")){
                List<String> historyPlayers = new ArrayList<>();
                for(String users : plugin.getPlayers().getConfigurationSection("players").getKeys(false)){
                    if(!historyPlayers.contains(users)){
                        historyPlayers.add(users);
                    }
                }

                String arg = args[1];
                if(historyPlayers.contains(arg)){
                    if(args[1].equalsIgnoreCase(arg)){
                        player.sendMessage("");
                        player.sendMessage(plugin.colors(plugin.prefix + "&b"+ arg + " &fplayer information."));
                        if(player.isOp()){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fGroup: &bAn operator does not need a group."));
                        } else{
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fGroup: &6[&b" + GroupManager.getPlayerGroup(Bukkit.getPlayer(arg) , plugin) + "&6]"));
                        }
                        if(plugin.getPlayers().getString("players." + arg + ".reports") == null){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fReports: &b0"));
                        } else {
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fReports: &b" + plugin.getPlayers().getString("players." + arg + ".reports")));
                        }
                        if(plugin.getPlayers().getString("players." + arg + ".last-command") == null){
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fLast command used: &bNo registered commands."));
                        } else {
                            player.sendMessage(plugin.colors("&6&l&m>>>&r &fLast command used: &b" + plugin.getPlayers().getString("players." + arg + ".last-command")));

                            InteractText.sendUser("playerHistory", arg, player, plugin);
                        }
                    }
                } else{
                    player.sendMessage(plugin.colors(plugin.prefix + "&b" + arg + " &cplayer was not found."));
                }
            } else if(args[0].equalsIgnoreCase("open")){
                List<String> historyPlayers = new ArrayList<>();
                for(String users : plugin.getPlayers().getConfigurationSection("players").getKeys(false)){
                    if(!historyPlayers.contains(users)){
                        historyPlayers.add(users);
                    }
                }

                String arg = args[1];
                if(historyPlayers.contains(arg)) {
                    if (args[1].equalsIgnoreCase(arg)) {
                        List<String> history = plugin.getPlayers().getStringList("players." + arg + ".command-history");
                        Set<String> miSet = new HashSet<String>(history);
                        List<String> historyFinal = new ArrayList<>();
                        for (String s : miSet) {
                            historyFinal.add(s + " (" + Collections.frequency(history, s) + ")");
                        }
                        String result = String.join("\n", historyFinal);

                        List<String> pages = new ArrayList<>();
                        pages.add(plugin.colors("&b" + arg + " &r&l- Command History&r\n" + result).replaceAll("\\n", "\n"));

                        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
                        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
                        bookMeta.setTitle(plugin.colors("&b" + arg + " &r&l- Command History"));
                        bookMeta.setAuthor("&6&lComplexCode");
                        bookMeta.setPages(pages);
                        writtenBook.setItemMeta(bookMeta);

                        player.openBook(writtenBook);
                    }
                }
            }
        }
        return false;
    }
}
