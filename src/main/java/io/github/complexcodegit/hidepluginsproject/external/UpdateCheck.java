package io.github.complexcodegit.hidepluginsproject.external;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateCheck {
    private URL checkURL;
    private String newVersion = "";
    private JavaPlugin plugin;

    public UpdateCheck(JavaPlugin plugin) {
        this.plugin = plugin;
        this.newVersion = plugin.getDescription().getVersion();
        try {
            this.checkURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 25317);
        } catch (MalformedURLException ignored) {
        }
    }

    public String getLatestVersion() {
        return newVersion;
    }
    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + 25317;
    }
    public boolean checkForUpdates() throws Exception {
        URLConnection con = checkURL.openConnection();
        this.newVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
        return !plugin.getDescription().getVersion().equals(newVersion);
    }
}

