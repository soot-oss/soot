package soot.javaToJimple.jj;

import polyglot.util.Position;

public class DPosition extends Position{

    private int endCol;
    private int endLine;
    
    public DPosition(String f, int l, int c, int e){
        this(f, l, c, e, l);
    }
    
    public DPosition(String f, int l, int c, int e, int el){
        super(f, l, c);
        endCol = e;
        endLine = el;
    }

    public int endCol(){
        return endCol;
    }

    public int endLine(){
        return endLine;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("file: ");
        sb.append(super.file());
        sb.append(" line: ");
        sb.append(super.line());
        sb.append(" column: ");
        sb.append(super.column());
        sb.append(" end column: ");
        sb.append(endCol);
        sb.append(" end line: ");
        sb.append(endLine);
    
        return sb.toString();
    }
}
