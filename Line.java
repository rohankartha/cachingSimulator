package org.example;

public class Line {
    private int valid = 0;
    private String tag = "0x00000000";
    private int last_touch = 0;
    private String lineNumber;

    public int getValid(){
        return valid;
    }
    public String getTag(){
        return tag;
    }
    public int getLast_touch(){
        return last_touch;
    }
    public String getLineNumber(){
        return lineNumber;
    }
    public void setValid(int valid){
        this.valid = valid;
    }
    public void setTag(String tag){
        this.tag = tag;
    }
    public void setLast_touch(int last_touch){
        this.last_touch = last_touch;
    }
    public void setLineNumber(String lineNumber){
        this.lineNumber = lineNumber;
    }
}
