package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class WorldObject {
    private final String paramName;
    private List<PageObject> paramPages = new ArrayList<>();
    private List<String> paramCommands = new ArrayList<>();
    private List<String> paramTabs = new ArrayList<>();

    public WorldObject(String paramName){
        this.paramName = paramName;
    }

    public String getName(){
        return paramName;
    }
    public List<PageObject> getPages(){
        return paramPages;
    }
    public PageObject getPage(String paramNumber){
        for(PageObject pageObject : paramPages){
            if(pageObject.getNumber().equals(paramNumber)){
                return pageObject;
            }
        }
        return null;
    }
    public List<String> getCommands(){
        return paramCommands;
    }
    public List<String> getTabs(){
        return paramTabs;
    }
    public void addPage(PageObject pageObject){
        paramPages.add(pageObject);
    }
    public void addCommand(String command){
        if(!paramCommands.contains(command)){
            paramCommands.add(command);
        }
    }
    public void addTab(String command){
        if(!paramTabs.contains(command)){
            paramTabs.add(command.replaceFirst("/", ""));
        }
    }
}
