package soot.tagkit;

public class SourceLnPosTag implements Tag {

    private int startLn;
    private int endLn;
    private int startPos;
    private int endPos;
    
    public SourceLnPosTag(int sline, int eline, int spos, int epos){
        startLn = sline;
        endLn = eline;
        startPos = spos;
        endPos = epos;
    }

    public int startLn(){
        return startLn;
    }

    public int endLn(){
        return endLn;
    }

    public int startPos(){
        return startPos;
    }

    public int endPos(){
        return endPos;
    }

    public String getName(){
        return "SourceLnPosTag";
    }

    public byte[] getValue() {
        byte[] v = new byte[4];
        return v;
    }
    
}   
