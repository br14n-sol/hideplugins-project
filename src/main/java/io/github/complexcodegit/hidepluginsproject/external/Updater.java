package io.github.complexcodegit.hidepluginsproject.external;

import io.github.complexcodegit.hidepluginsproject.HidePluginsProject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {
    private static String latestVersion;

    public static void checkedUpdate(HidePluginsProject plugin, String version) {
        if(plugin.getConfig().getBoolean("updates")) {
            try {
                HttpURLConnection con = (HttpURLConnection)(new URL("https://api.spigotmc.org/legacy/update.php?resource=25317")).openConnection();
                int timed_out = 1250;
                con.setConnectTimeout(timed_out);
                con.setReadTimeout(timed_out);
                latestVersion = (new BufferedReader(new InputStreamReader(con.getInputStream()))).readLine();

                if(latestVersion.length() <= 7 && !version.equals(latestVersion)) {
                    plugin.console.sendMessage("");
                    plugin.console.sendMessage(plugin.colors("&e>>> &bHidePlugins Project"));
                    plugin.console.sendMessage(plugin.colors("&e>>> &fThere is a new version available. &b" + latestVersion));
                    plugin.console.sendMessage(plugin.colors("&e>>> &fYou can download it at: &bhttps://www.spigotmc.org/resources/25317/"));
                }
            } catch(Exception ex) {
                plugin.console.sendMessage("");
                plugin.console.sendMessage(plugin.colors("&e>>> &bHidePlugins Project"));
                plugin.console.sendMessage(plugin.colors("&e>>> &fError verifying update."));
            }
        }
    }
}
