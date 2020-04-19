package io.github.complexcodegit.hidepluginsproject.objects;

import io.github.complexcodegit.hidepluginsproject.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings({"ConstantConditions"})
public class PlayerObject implements ConfigurationSerializable {
    private String paramName;
    private UUID paramUUID;
    private int paramReports = 0;
    private String paramLastCommand = "none";
    private List<String> paramIpHistory = new ArrayList<>();
    private List<String> paramCommandHistory = new ArrayList<>();

    public PlayerObject(String paramName, UUID paramUUID){
        this.paramName = paramName;
        this.paramUUID = paramUUID;
    }
    public PlayerObject(String paramName, UUID paramUUID, int paramReports, List<String> paramIpHistory, List<String> paramCommandHistory){
        this.paramName = paramName;
        this.paramUUID = paramUUID;
        this.paramReports = paramReports;
        this.paramIpHistory = paramIpHistory;
        this.paramCommandHistory = paramCommandHistory;
    }

    public String getName(){
        return paramName;
    }
    public UUID getUUID(){
        return paramUUID;
    }
    public int getReports(){
        return paramReports;
    }
    public String getLast(){
        return paramLastCommand;
    }
    public List<String> getIPS(){
        return paramIpHistory;
    }
    public List<String> getCmdHistory(){
        return paramCommandHistory;
    }
    public void addReport(){
        this.paramReports++;
    }
    public void setLast(String paramLastCommand){
        this.paramLastCommand = paramLastCommand;
    }
    public void addIP(String paramIP){
        if(!this.paramIpHistory.contains(paramIP)){
            this.paramIpHistory.add(paramIP);
        }
    }
    public void addCommand(String command){
        this.paramCommandHistory.add(command);
    }

    @Nonnull
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("name", this.paramName);
        result.put("reports", this.paramReports);
        result.put("last-command", this.paramLastCommand);
        result.put("ips-history", Utils.toString(this.paramIpHistory));
        result.put("command-history", Utils.toString(this.paramCommandHistory));
        return result;
    }

    public static PlayerObject deserialize(UUID playerUUID, FileConfiguration players) {
        String name = "none";
        int reports = 0;
        List<String> ips = new ArrayList<>();
        List<String> commands = new ArrayList<>();
        if(players.get("Players."+playerUUID+".name") != null){
            name = players.getString("Players."+playerUUID+".name");
        }
        if(players.get("Players."+playerUUID+".reports") != null){
            reports = players.getInt("Players."+playerUUID+".reports");
        }
        if(players.get("Players." + playerUUID + ".ips-history") != null){
            ips = Utils.toList(players.getString("Players." + playerUUID + ".ips-history"));
        }
        if(players.get("Players." + playerUUID + ".command-history") != null){
            commands = Utils.toList(players.getString("Players." + playerUUID + ".command-history"));
        }
        PlayerObject playerObject = new PlayerObject(name, playerUUID, reports, ips, commands);
        if(players.get("Players."+playerUUID+".last-command") != null){
            playerObject.setLast(players.getString("Players."+playerUUID+".last-command"));
        }
        return playerObject;
    }
}