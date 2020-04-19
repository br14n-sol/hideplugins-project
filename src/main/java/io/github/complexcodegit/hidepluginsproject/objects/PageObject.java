package io.github.complexcodegit.hidepluginsproject.objects;

import java.util.List;

public class PageObject {
    private final String paramNumber;
    private final List<String> paramLines;

    public PageObject(String paramNumber, List<String> paramLines){
        this.paramNumber = paramNumber;
        this.paramLines = paramLines;
    }

    public String getNumber(){
        return paramNumber;
    }
    public List<String> getLines(){
        return paramLines;
    }
}
