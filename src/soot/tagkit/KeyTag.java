package soot.tagkit;

public class KeyTag implements Tag {

    private int red;
    private int green;
    private int blue;
    private String key;
    
    public KeyTag(int r, int g, int b, String k){
        red = r;
        green = g;
        blue = b;
        key = k;
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue() {
        return blue;
    }

    public String key(){
        return key;
    }

    public String getName(){
        return "KeyTag";
    }

    public byte[] getValue() {
        byte[] v = new byte[4];
        return v;
    }
    
}   
