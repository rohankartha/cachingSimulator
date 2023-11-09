package org.example;

import java.util.ArrayList;
import java.util.List;

public class CacheSet {
    public List<Line> setOfLines = new ArrayList<>();
    public String setIndex;
    public CacheSet(){}


    /**
     *
     * @param numLinesInSet
     */
    public void initializeLines(int numLinesInSet){
        for (int i = 0; i < numLinesInSet; i++){
            Line newLine = new Line();
            newLine.setLineNumber(Integer.toHexString(i));
            newLine.setTag("000000");
            setOfLines.add(newLine);
        }
    }

    public void setSetIndex(String setIndex){
        this.setIndex = setIndex;
    }
}
