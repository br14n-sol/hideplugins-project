package io.github.complexcodegit.hidepluginsproject.utils;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InteractText {
    public static void send(String key, Player player, HidePluginsProject plugin){
        if(key.equals("useCommand")){
            TextComponent use = new TextComponent(plugin.colors(plugin.prefix + "&cUse "));
            TextComponent click = new TextComponent(plugin.colors("&f/hproject help"));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hproject help"));
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to execute the command.").color(ChatColor.AQUA).create()));
            use.addExtra(click);

            player.spigot().sendMessage(use);
        }
        if(key.equals("listGroups")){
            List<String> groups = GroupManager.getGroups(plugin);
            List<TextComponent> textGroups = new ArrayList<>();
            TextComponent text1;
            TextComponent text2;
            for(int i=0; i < groups.size(); i++){
                text1 = new TextComponent(plugin.colors("&6&l&m>>>&r "));
                text2 = new TextComponent(groups.get(i));
                text2.setColor(ChatColor.AQUA);
                text2.setBold(true);
                text2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hproject group " + groups.get(i)));
                text2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to see more group " + plugin.colors("&b" + groups.get(i) + "&r") + " information.").create()));
                text1.addExtra(text2);

                textGroups.add(text1);
                player.spigot().sendMessage(textGroups.get(i));
            }
        }
        if(key.equals("commandExistUse")){
            TextComponent use = new TextComponent(plugin.colors(plugin.prefix + "&cThe command does not exist, try using &b"));
            TextComponent click = new TextComponent(plugin.colors("&f/hproject help"));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hproject help"));
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to execute the command.").color(ChatColor.AQUA).create()));
            use.addExtra(click);

            player.spigot().sendMessage(use);
        }
    }

    public static void sendUser(String key, String user, Player player, HidePluginsProject plugin){
        if(key.equals("playerHistory")){
            TextComponent use = new TextComponent(plugin.colors("&6&l&m>>>&r &f&lCommand history: &r"));
            TextComponent click = new TextComponent(plugin.colors("&b&l&o---CLICK"));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hprojectinternal open " + user));
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Open the command history.").create()));
            use.addExtra(click);

            player.spigot().sendMessage(use);
        }
    }
}
