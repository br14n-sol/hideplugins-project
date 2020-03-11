package io.github.complexcodegit.hidepluginsproject.utils;

import org.bukkit.entity.Player;

public class Messages {
    public void commandNotExist(Player player){
        player.sendMessage("");
        player.sendMessage("§7The command you entered does not exist.");
    }
    public void globalMessages(Player player, String string){
        player.sendMessage("");
        if(string.equals("select")){
            player.sendMessage("§7The §eglobal §7submenu was selected correctly.");
        } else {
            player.sendMessage("§7The §eglobal §7submenu was created correctly.");
        }
        player.sendMessage("");
        player.sendMessage("§7Available commands:");
        player.sendMessage("§e/hproject add command [command] §7- To add a command to the global.");
        player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the global.");
        player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the global.");
        player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the global.");
    }
    public void worldMessages(Player player, String string, String world){
        player.sendMessage("");
        if(string.equals("select")){
            player.sendMessage("§7The §e" + world + " §7world was selected successfully.");
        } else {
            player.sendMessage("§7The §e" + world + " §7world was set successfully.");
        }
        player.sendMessage("");
        player.sendMessage("§7Available commands:");
        player.sendMessage("§e/hproject add command [command] §7- To add a command to the world.");
        player.sendMessage("§e/hproject add tab [command] §7- To add a tab suggestion to the world.");
        player.sendMessage("§e/hproject remove command [command] §7- To remove a command to the world.");
        player.sendMessage("§e/hproject remove tab [command] §7- To remove a tab suggestion to the world.");
        player.sendMessage("§e/hproject select world [worldname] §7- To select another world.");
    }
    public void groupMessages(Player player, String string, String group){
        player.sendMessage("");
        if(string.equals("select")){
            player.sendMessage("§7The §e" + group + " §7group has been selected.");
        } else {
            player.sendMessage("§7The §e" + group + " §7group was created successfully.");
        }
        player.sendMessage("");
        player.sendMessage("§7Available commands:");
        player.sendMessage("§e/hproject set world [worldname] §7- To add a world to the group.");
        player.sendMessage("§e/hproject select world [worldname] §7- To select a world to the group.");
        player.sendMessage("§e/hproject finish §7- To finish editing the group.");
    }
    public void selectWorldMsg(Player player, String world){
        player.sendMessage("");
        player.sendMessage("§7The §c" + world + " §7world is no longer selected.");
    }
    public void selectGlobalMsg(Player player){
        player.sendMessage("");
        player.sendMessage("§7The §cglobal §7submenu is no longer selected.");
    }
}
