package io.github.complexcodegit.hidepluginsproject.managers;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class SoundManager {
    public static void checkSoundPlayer(HidePluginsProject plugin, Player player){
        if(plugin.getConfig().getBoolean("sounds.enable")){
            playRandomSound(plugin, player);
        }
    }

    public static void playRandomSound(HidePluginsProject plugin, Player player){
        List<String> sounds = plugin.getConfig().getStringList("sounds..");
        int soundsSize = sounds.size();

        Random random = new Random(System.currentTimeMillis());
        int intRandom = random.nextInt(soundsSize);

        String intSound = sounds.get(intRandom);
        random.setSeed(System.currentTimeMillis());

        player.playSound(player.getLocation(), Sound.valueOf(intSound), 100.0F, 100.0F);
    }
}
