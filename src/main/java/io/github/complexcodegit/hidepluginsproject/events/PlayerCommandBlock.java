package io.github.complexcodegit.hidepluginsproject.events;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import io.github.complexcodegit.hidepluginsproject.Utils;
import io.github.complexcodegit.hidepluginsproject.handlers.PlayersHandler;
import io.github.complexcodegit.hidepluginsproject.managers.CooldownManager;
import io.github.complexcodegit.hidepluginsproject.managers.GroupManager;
import io.github.complexcodegit.hidepluginsproject.objects.GroupObject;
import io.github.complexcodegit.hidepluginsproject.objects.PlayerObject;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Objects;

public class PlayerCommandBlock implements Listener {
    private HidePluginsProject plugin;
    private GroupManager groupManager;
    public PlayerCommandBlock(HidePluginsProject plugin, GroupManager groupManager) {
        this.plugin = plugin;
        this.groupManager = groupManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public boolean playerCmdPreprocess(PlayerCommandPreprocessEvent event){
        FileConfiguration config = plugin.getConfig();
        Player player = event.getPlayer();
        if(!config.getBoolean("lockedCommands")) {
            return false;
        }
        event.setCancelled(true);
        if(player.isOp() || player.hasPermission(Objects.requireNonNull(config.getString("bypassPermission")))){
            event.setCancelled(false);
            return false;
        }
        if(!CooldownManager.checkCooldown(player)) {
            player.sendMessage(Utils.colors(config.getString("cooldown.message")).replaceAll("%time%", String.valueOf(CooldownManager.getCooldown(player))));
        } else {
            String[] playerMessage = event.getMessage().split(" ");
            List<String> commands = groupManager.getCommands(player, false);
            GroupObject playerGroup = groupManager.getPlayerGroup(player);
            if(commands.contains(playerMessage[0])){
                if(playerMessage[0].equalsIgnoreCase("/help") && playerGroup.hasHelp()){
                    if(playerMessage.length > 1){
                        if(groupManager.getHelpPage(player, playerMessage[1]) != null){
                            for(String line : groupManager.getHelpPage(player, playerMessage[1])) {
                                player.sendMessage(Utils.colors(line).replace("[PAGE]", playerMessage[1]).replace("[LASTPAGE]", String.valueOf(playerGroup.getWorld(player.getWorld().getName()).getPages().size())));
                            }
                        } else {
                            if(config.getBoolean("prefix.enable")){
                                player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+ Objects.requireNonNull(plugin.getMessages().getString("helpPageNotExist")).replace("[PAGE]", playerMessage[1])));
                            } else {
                                player.sendMessage(Utils.colors(Objects.requireNonNull(plugin.getMessages().getString("helpPageNotExist")).replace("[PAGE]", playerMessage[1])));
                            }
                        }
                    } else {
                        for(String pag : groupManager.getHelpPage(player, "1")) {
                            player.sendMessage(Utils.colors(pag).replace("[PAGE]", "1").replace("[LASTPAGE]", String.valueOf(playerGroup.getWorld(player.getWorld().getName()).getPages().size())));
                        }
                    }
                    return false;
                }
                event.setCancelled(false);
            } else if(!playerMessage[0].equals("/")) {
                if(config.getBoolean("cooldown.enable")){
                    CooldownManager.setCooldown(player, config.getInt("cooldown.time"));
                }
                if(config.getBoolean("warningMessage.title.enable")){
                    player.sendTitle(Utils.colors(config.getString("warningMessage.title.top")), Utils.colors(config.getString("warningMessage.title.bottom")), 10, 70, 20);
                }
                if(config.getBoolean("warningMessage.message.enable")){
                    for(String line : config.getStringList("warningMessage.message.lines")){
                        if(config.getBoolean("prefix.enable")){
                            player.sendMessage(Utils.colors(config.getString("prefix.prefix")+" "+line));
                        } else {
                            player.sendMessage(Utils.colors(line));
                        }
                    }
                }
                if(config.getBoolean("potionEffect.enable")){
                    PotionEffectType effect = PotionEffectType.getByName(Objects.requireNonNull(config.getString("potionEffect.effect")));
                    assert effect != null;
                    player.addPotionEffect(new PotionEffect(effect, config.getInt("potionEffect.time")*20, config.getInt("potionEffect.amplifier"), false, false, false));
                }
                if(config.getBoolean("particles.enable")){
                    player.spawnParticle(Particle.valueOf(config.getString("particles.particle")), player.getLocation(), config.getInt("particles.amount"));
                }
                if(config.getBoolean("sounds.enable")){
                    Utils.randomSound(player);
                }
                if(PlayersHandler.getPlayer(player) != null){
                    PlayerObject playerObject = PlayersHandler.getPlayer(player);
                    PlayersHandler.getPlayers().remove(playerObject);
                    assert playerObject != null;
                    playerObject.setLast(playerMessage[0]);
                    playerObject.addReport();
                    if(config.getBoolean("playerCommandHistory")){
                        playerObject.addCommand(playerMessage[0]);
                    }
                    PlayersHandler.getPlayers().add(playerObject);
                }
            }
        }
        return false;
    }
}
