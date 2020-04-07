package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.ArrayList;
import java.util.List;

public class WorldObject {
    private String worldName;
    private List<String> commandList = new ArrayList<>();
    private List<String> tabList = new ArrayList<>();
    private List<PageObject> pagesList = new ArrayList<>();

    public WorldObject(String worldName){
        this.worldName = worldName;
    }

    public String getWorldName(){
        return worldName;
    }
    public void addCommand(String command){
        commandList.add(command);
    }
    public List<String> getCommands(){
        return commandList;
    }
    public void addTab(String tab){
        tabList.add(tab.replaceFirst("/", ""));
    }
    public List<String> getTabs(){
        return tabList;
    }
    public void addPage(PageObject pageObject){
        pagesList.add(pageObject);
    }
    public List<PageObject> getPages(){
        return pagesList;
    }
    public PageObject getPage(String pageNumber){
        for(PageObject pageObject : pagesList){
            if(pageObject.getPageNumber().equals(pageNumber)){
                return pageObject;
            }
        }
        return null;
    }
}
