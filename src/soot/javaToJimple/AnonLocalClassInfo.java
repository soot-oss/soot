package soot.javaToJimple;

import java.util.*;

public class AnonLocalClassInfo{

    private boolean inStaticMethod;
    private ArrayList finalLocals;

    public boolean inStaticMethod(){
        return inStaticMethod;
    }
    public void inStaticMethod(boolean b){
        inStaticMethod = b;
    }

    public ArrayList finalLocals(){
        return finalLocals;
    }
    public void finalLocals(ArrayList list){
        finalLocals = list;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("static: ");
        sb.append(inStaticMethod);
        sb.append(" finalLocals: ");
        sb.append(finalLocals);
        return sb.toString();
    }
}
