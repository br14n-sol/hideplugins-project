package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class GlobalObject {
    private List<String> commandList = new ArrayList<>();
    private List<String> tabList = new ArrayList<>();

    public void addCommand(String command){
        if(!commandList.contains(command)){
            commandList.add(command);
        }
    }
    public List<String> getCommands(){
        return commandList;
    }
    public void addTab(String tab){
        if(!tabList.contains(tab)){
            tabList.add(tab.replaceFirst("/", ""));
        }
    }
    public List<String> getTabs(){
        return tabList;
    }
}
