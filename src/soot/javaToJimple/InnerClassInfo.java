package soot.javaToJimple;

import soot.*;

public class InnerClassInfo {
    
    public static final int NESTED = 0;
    public static final int STATIC = 1;
    public static final int LOCAL = 2;
    public static final int ANON = 3;
    
    private soot.SootClass outerClass;
    public soot.SootClass getOuterClass(){
        return outerClass;
    }
    
    private String simpleName;
    public String getSimpleName(){
        return simpleName;
    }
    
    private int innerType;
    public int getInnerType(){
        return innerType;
    }
    
    public InnerClassInfo(soot.SootClass outerClass, String simpleName, int innerType){
        this.outerClass = outerClass;
        this.simpleName = simpleName;
        this.innerType = innerType;
    }
}

