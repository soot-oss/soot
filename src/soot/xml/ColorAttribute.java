package soot.xml;

import soot.*;

public class ColorAttribute{
            
    private int red;
    private int green;
    private int blue;
    private boolean fg;

    public ColorAttribute(int red, int green, int blue, boolean fg){
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.fg = fg;
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

    public boolean fg(){
        return fg;
    }

}
