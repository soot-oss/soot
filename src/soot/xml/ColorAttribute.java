package soot.xml;

import soot.*;

public class ColorAttribute{
            
    private int red;
    private int green;
    private int blue;
    private int fg;
    private String analysisType;
    
    public ColorAttribute(int red, int green, int blue, boolean fg, String type){
        this.red = red;
        this.green = green;
        this.blue = blue;
        if (fg){
            this.fg = 1;
        }
        else {
            this.fg = 0;
        }
        analysisType = type;
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue(){
        return blue;
    }

    public int fg(){
        return fg;
    }

    public String analysisType(){
        return analysisType;
    }

}
