package io.github.complexcodegit.hidepluginsproject.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerDropItem implements Listener {
    @EventHandler
    public void dropItemCheck(PlayerDropItemEvent event){
        ItemStack itemDrop = event.getItemDrop().getItemStack();
        ItemMeta meta = itemDrop.getItemMeta();
        assert meta != null;
        if(meta.getDisplayName().equals("§aAdd Commands §8- §7HidePlugins Project") || meta.getDisplayName().equals("§aAdd Tabs §8- §7HidePlugins Project") ||
                meta.getDisplayName().equals("§aRemove Commands §8- §7HidePlugins Project") || meta.getDisplayName().equals("§aRemove Tabs §8- §7HidePlugins Project")){
            event.setCancelled(true);
        }
    }
}
