package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.List;

public class PageObject {
    private String pageNumber;
    private List<String> pageLines;

    public PageObject(String pageNumber, List<String> pageLines){
        this.pageNumber = pageNumber;
        this.pageLines = pageLines;
    }

    public String getPageNumber(){
        return pageNumber;
    }
    public List<String> getLines(){
        return pageLines;
    }
}
