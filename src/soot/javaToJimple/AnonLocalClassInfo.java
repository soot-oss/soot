package soot.javaToJimple;

import java.util.*;

public class AnonLocalClassInfo{

    private boolean inStaticMethod;
    private ArrayList finalFields;

    public boolean inStaticMethod(){
        return inStaticMethod;
    }
    public void inStaticMethod(boolean b){
        inStaticMethod = b;
    }

    public ArrayList finalFields(){
        return finalFields;
    }
    public void finalFields(ArrayList list){
        finalFields = list;
    }
}
