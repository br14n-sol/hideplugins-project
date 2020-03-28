package io.github.complexcodegit.hidepluginsproject.packetadapters;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class PlayClientTabComplete extends PacketAdapter {
    private GroupManager groupManager;
    public PlayClientTabComplete(Plugin plugin, GroupManager groupManager, PacketType... types){
        super(plugin, types);
        this.groupManager = groupManager;
    }

    public void onPacketReceiving(PacketEvent event){
        PacketType packetType = event.getPacketType();
        if(packetType.equals(PacketType.Play.Client.TAB_COMPLETE)){
            if(!plugin.getConfig().getBoolean("locked-commands"))
                return;
            Player player = event.getPlayer();
            if(player.hasPermission(Objects.requireNonNull(plugin.getConfig().getString("tab-usage-permission"))))
                return;
            PacketContainer packetContainer = event.getPacket();
            String message = packetContainer.getSpecificModifier(String.class).read(0).toLowerCase();
            if(groupManager.getCommands(player, true).stream().anyMatch(s -> message.startsWith("/"+s.toLowerCase())))
                event.setCancelled(true);
        }
    }
}
