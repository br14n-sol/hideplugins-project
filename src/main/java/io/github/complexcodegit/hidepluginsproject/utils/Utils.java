package io.github.complexcodegit.hidepluginsproject.utils;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.help.HelpTopic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Utils {
    private static Random random = new Random(System.currentTimeMillis());
    private static ItemStack book = new ItemStack(Material.WRITABLE_BOOK, 1);

    public static String colors(String s){
        return ChatColor.translateAlternateColorCodes('&', s);
    }
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
    public static Integer getItemSlot(String displayName, Player player){
        PlayerInventory inv = player.getInventory();
        int slot = -1;
        for(int i=0; i < 36; i++){
            if(inv.getItem(i) != null && inv.getItem(i).getItemMeta().getDisplayName().equals(displayName)){
                slot = i;
                break;
            }
        }
        if(inv.getItem(40) != null && inv.getItem(40).getItemMeta().getDisplayName().equals(displayName)){
            slot = 40;
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
    public static ItemStack createBook(String displayname, String...lore){
        BookMeta meta = (BookMeta)book.getItemMeta();
        assert meta != null;
        meta.setDisplayName(displayname);
        meta.setLore(Arrays.asList(lore));
        book.setItemMeta(meta);
        return book;
    }
    private static <E extends Enum<E>> boolean isValidSound(String paramString){
        if(paramString == null)
            return false;
        try {
            Enum.valueOf(Sound.class, paramString);
            return true;
        } catch (IllegalArgumentException ignored){}
        return false;
    }
    public static void randomSound(Player player){
        List<String> configSounds = JavaPlugin.getPlugin(HidePluginsProject.class).getConfig().getStringList("sounds.list");
        if(!configSounds.isEmpty()){
            configSounds.removeIf(sound -> !isValidSound(sound));
            if(!configSounds.isEmpty()){
                int intRandom = random.nextInt(configSounds.size());

                String sound = configSounds.get(intRandom);
                player.playSound(player.getLocation(), Sound.valueOf(sound), 100.0F, 100.0F);
                random.setSeed(System.currentTimeMillis());
            }
        }
    }
}
