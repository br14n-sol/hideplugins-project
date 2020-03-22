package io.github.complexcodegit.hidepluginsproject.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Utils {
    public static Location getHandLocation(Player player){
        Location loc = player.getLocation().clone();

        double a = loc.getYaw() / 180D * Math.PI + Math.PI / 2;
        double l = Math.sqrt(0.8D * 0.8D + 0.4D * 0.4D);

        loc.setX(loc.getX() + l * Math.cos(a) - 0.8D * Math.sin(a));
        loc.setY(loc.getY() + player.getEyeHeight() - 0.2D);
        loc.setZ(loc.getZ() + l * Math.sin(a) + 0.8D * Math.cos(a));

        return loc;
    }
    public static Integer slotFree(Player player){
        PlayerInventory inv = player.getInventory();
        int slot = -1;
        for(int i=0; i < 9; i++){
            if(inv.getItem(i) == null){
                slot = i;
                break;
            }
        }
        return slot;
    }
    @SuppressWarnings("ConstantConditions")
    public static Integer getItemSlot(String displayName, Player player){
        PlayerInventory inv = player.getInventory();
        int slot = -1;
        for(int i=0; i < 9; i++){
            if(inv.getItem(i) != null && inv.getItem(i).getItemMeta().getDisplayName().equals(displayName)){
                slot = i;
                break;
            }
        }
        return slot;
    }
    public static ItemStack getItem(String displayname, Player player){
        int slot = getItemSlot(displayname, player);
        if(slot != -1)
            return player.getInventory().getItem(slot);
        return null;
    }
    public static boolean checkCommand(String command){
        ArrayList<String> commands = new ArrayList<>();
        ArrayList<String> blacklist = new ArrayList<>();
        for(Plugin plugins : Bukkit.getServer().getPluginManager().getPlugins()){
            if(plugins != null && !blacklist.contains(plugins.getName()))
                blacklist.add(plugins.getName());
        }
        blacklist.add("Aliases");
        blacklist.add("Bukkit");
        blacklist.add("Minecraft");

        for(HelpTopic cmdLabel : Bukkit.getServer().getHelpMap().getHelpTopics()){
            if(!commands.contains(cmdLabel.getName()) && !blacklist.contains(cmdLabel.getName()))
                commands.add(cmdLabel.getName());
        }
        return commands.contains(command);
    }
}
