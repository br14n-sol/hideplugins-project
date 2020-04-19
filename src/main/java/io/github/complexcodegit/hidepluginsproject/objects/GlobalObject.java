package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class GlobalObject {
    private List<String> paramCommands = new ArrayList<>();
    private List<String> paramTabs = new ArrayList<>();

    public List<String> getCommands(){
        return paramCommands;
    }
    public List<String> getTabs(){
        return paramTabs;
    }
    public void addCommand(String command){
        if(!paramCommands.contains(command)){
            paramCommands.add(command);
        }
    }
    public void addTab(String tab){
        if(!paramTabs.contains(tab)){
            paramTabs.add(tab.replaceFirst("/", ""));
        }
    }
}
