package soot.xml;

import soot.*;

public class PosColorAttribute{
        
    private ColorAttribute color;
    private int jimpleStartPos;
    private int jimpleEndPos;
    private int javaStartPos;
    private int javaEndPos;
    private int javaStartLn;
    private int javaEndLn;
    private int jimpleStartLn;
    private int jimpleEndLn;

    public PosColorAttribute(){
    }

    public ColorAttribute color(){
        return color;
    }

    public void color(ColorAttribute c){
        color = c;
    }

    public int jimpleStartPos(){
        return jimpleStartPos;
    }

    public void jimpleStartPos(int x){
        jimpleStartPos = x;
    }

    public int jimpleEndPos(){
        return jimpleEndPos;
    }

    public void jimpleEndPos(int x){
        jimpleEndPos = x;
    }
    
    public int javaStartPos(){
        return javaStartPos;
    }

    public void javaStartPos(int x){
        javaStartPos = x;
    }

    public int javaEndPos(){
        return javaEndPos;
    }

    public void javaEndPos(int x){
        javaEndPos = x;
    }
    
    public int jimpleStartLn(){
        return jimpleStartLn;
    }

    public void jimpleStartLn(int x){
        jimpleStartLn = x;
    }

    public int jimpleEndLn(){
        return jimpleEndLn;
    }

    public void jimpleEndLn(int x){
        jimpleEndLn = x;
    }
    
    public int javaStartLn(){
        return javaStartLn;
    }

    public void javaStartLn(int x){
        javaStartLn = x;
    }

    public int javaEndLn(){
        return javaEndLn;
    }

    public void javaEndLn(int x){
        javaEndLn = x;
    }

    public boolean hasColor(){
        if (color() != null) return true;
        else return false;
    }
}
