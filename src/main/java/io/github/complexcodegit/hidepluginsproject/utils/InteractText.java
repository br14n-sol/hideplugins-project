package io.github.complexcodegit.hidepluginsproject.utils;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.managers.LanguageManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class InteractText {
    public static void sendUser(String key, String user, Player player, HidePluginsProject plugin){
        if(key.equals("playerHistory")){
            TextComponent use = new TextComponent(LanguageManager.internalTranslateNoPrefix("commands.player.command-history", plugin));
            TextComponent click = new TextComponent(LanguageManager.internalTranslateNoPrefix("commands.player.open-history", plugin));
            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hprojectinternal open " + user));
            click.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Open the command history.").create()));
            use.addExtra(click);

            player.spigot().sendMessage(use);
        }
    }
}
