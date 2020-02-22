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
                        List<String> historyFinal1 = new ArrayList<>();
                        List<String> historyFinal2 = new ArrayList<>();
                        List<String> historyFinal3 = new ArrayList<>();
                        List<String> historyFinal4 = new ArrayList<>();
                        List<String> historyFinal5 = new ArrayList<>();
                        List<String> historyFinal6 = new ArrayList<>();
                        List<String> historyFinal7 = new ArrayList<>();
                        List<String> historyFinal8 = new ArrayList<>();
                        List<String> historyFinal9 = new ArrayList<>();
                        List<String> historyFinal10 = new ArrayList<>();

                        List<String> pages = new ArrayList<>();
                        if(!historyFinal.isEmpty()){
                            if(historyFinal.size() < 12){
                                for(int i=0; i < historyFinal.size(); i++){
                                    historyFinal1.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=0; i < 12; i++){
                                    historyFinal1.add(historyFinal.get(i));
                                }
                            }
                            String result1 = String.join("\n", historyFinal1);
                            pages.add(plugin.colors("&6&l" + arg + " &r&l- History&r\n\n" + result1).replaceAll("\\n", "\n"));
                        } else {
                            pages.add(plugin.colors("&6&l" + arg + " &r&l- History"));
                        }
                        if(historyFinal1.size() == 12){
                            if(historyFinal.size() > 26){
                                for(int i=12; i < 26; i++){
                                    historyFinal2.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=12; i < historyFinal.size(); i++){
                                    historyFinal2.add(historyFinal.get(i));
                                }
                            }
                            String result2 = String.join("\n", historyFinal2);
                            pages.add(result2.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal2.size() == 14){
                            if(historyFinal.size() > 40){
                                for(int i=26; i < 40; i++){
                                    historyFinal3.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=26; i < historyFinal.size(); i++){
                                    historyFinal3.add(historyFinal.get(i));
                                }
                            }
                            String result3 = String.join("\n", historyFinal3);
                            pages.add(result3.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal3.size() == 14){
                            if(historyFinal.size() > 54){
                                for(int i=40; i < 54; i++){
                                    historyFinal4.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=40; i < historyFinal.size(); i++){
                                    historyFinal4.add(historyFinal.get(i));
                                }
                            }
                            String result4 = String.join("\n", historyFinal4);
                            pages.add(result4.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal4.size() == 14){
                            if(historyFinal.size() > 68){
                                for(int i=54; i < 68; i++){
                                    historyFinal5.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=54; i < historyFinal.size(); i++){
                                    historyFinal5.add(historyFinal.get(i));
                                }
                            }
                            String result5 = String.join("\n", historyFinal5);
                            pages.add(result5.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal5.size() == 14){
                            if(historyFinal.size() > 82){
                                for(int i=68; i < 82; i++){
                                    historyFinal6.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=68; i < historyFinal.size(); i++){
                                    historyFinal6.add(historyFinal.get(i));
                                }
                            }
                            String result6 = String.join("\n", historyFinal6);
                            pages.add(result6.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal6.size() == 14){
                            if(historyFinal.size() > 96){
                                for(int i=68; i < 96; i++){
                                    historyFinal7.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=68; i < historyFinal.size(); i++){
                                    historyFinal7.add(historyFinal.get(i));
                                }
                            }
                            String result7 = String.join("\n", historyFinal7);
                            pages.add(result7.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal7.size() == 14){
                            if(historyFinal.size() > 110){
                                for(int i=82; i < 110; i++){
                                    historyFinal8.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=82; i < historyFinal.size(); i++){
                                    historyFinal8.add(historyFinal.get(i));
                                }
                            }
                            String result8 = String.join("\n", historyFinal8);
                            pages.add(result8.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal8.size() == 14){
                            if(historyFinal.size() > 124){
                                for(int i=96; i < 124; i++){
                                    historyFinal9.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=96; i < historyFinal.size(); i++){
                                    historyFinal9.add(historyFinal.get(i));
                                }
                            }
                            String result9 = String.join("\n", historyFinal9);
                            pages.add(result9.replaceAll("\\n", "\n"));
                        }
                        if(historyFinal9.size() == 14){
                            if(historyFinal.size() > 138){
                                for(int i=110; i < 138; i++){
                                    historyFinal10.add(historyFinal.get(i));
                                }
                            } else {
                                for(int i=110; i < historyFinal.size(); i++){
                                    historyFinal10.add(historyFinal.get(i));
                                }
                            }
                            String result10 = String.join("\n", historyFinal10);
                            pages.add(result10.replaceAll("\\n", "\n"));
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
