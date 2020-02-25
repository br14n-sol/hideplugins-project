package io.github.complexcodegit.hidepluginsproject.commands;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.*;

public class InternalCommand implements CommandExecutor {
    private HidePluginsProject plugin;

    public InternalCommand(HidePluginsProject plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String string, String[] args) {
        if(!command.getName().equalsIgnoreCase("hprojectinternal")){
            return false;
        }

        Player player = (Player)sender;
        List<String> hprojectGroup = GroupManager.getCommandsList(player, plugin);
        if(!player.hasPermission("hidepluginsproject.hproject") && !hprojectGroup.contains("/hproject")){
            return false;
        }

        if(args.length == 2){
            if(args[0].equalsIgnoreCase("open")){
                List<String> historyPlayers = new ArrayList<>();
                for(String users : plugin.getPlayers().getConfigurationSection("players").getKeys(false)){
                    if(!historyPlayers.contains(users)){
                        historyPlayers.add(users);
                    }
                }

                String arg = args[1];
                if(historyPlayers.contains(arg)) {
                    if(args[1].equalsIgnoreCase(arg)) {
                        List<String> history = plugin.getPlayers().getStringList("players." + arg + ".command-history");
                        Set<String> miSet = new HashSet<String>(history);
                        List<String> historyFinal = new ArrayList<>();
                        for(String s : miSet) {
                            historyFinal.add(s + " (" + Collections.frequency(history, s) + ")");
                        }

                        ArrayList<String> pages = new ArrayList<String>();
                        ArrayList<String> pageContent = new ArrayList<String>();

                        if(!historyFinal.isEmpty()) {
                            for(String s : historyFinal){
                                if(!(pageContent.size() == 14)){
                                    if(s != null){
                                        pageContent.add(s);
                                    } else {
                                        String result = String.join("\n", pageContent);
                                        pages.add(result.replaceAll("\\n", "\n"));
                                    }
                                } else {
                                    String result = String.join("\n", pageContent);
                                    pages.add(result.replaceAll("\\n", "\n"));
                                    pageContent.clear();
                                    pageContent.add(s);
                                }
                            }
                        } else {
                            pages.add(plugin.colors("&c&lNo records."));
                        }

                        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
                        BookMeta meta = (BookMeta) book.getItemMeta();

                        meta.setPages(pages);
                        meta.setAuthor("");
                        meta.setTitle("");

                        book.setItemMeta(meta);
                        player.openBook(book);
                    }
                }
            }
        }
        return false;
    }
}
