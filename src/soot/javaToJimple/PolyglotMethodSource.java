package soot.javaToJimple;

import java.util.*;
import soot.*;

public class PolyglotMethodSource implements MethodSource {

    private polyglot.ast.Block block;
    private List formals;
    private ArrayList fieldInits;
    private ArrayList staticFieldInits;
    private ArrayList initializerBlocks;
    private ArrayList staticInitializerBlocks;
    private soot.Local outerClassThisInit;
    private HashMap privateAccessMap;
    
    public PolyglotMethodSource(){
        this.block = null;
        this.formals = null;
    }
    
    public PolyglotMethodSource(polyglot.ast.Block block, List formals){
        this.block = block;
        this.formals = formals;
    }

    public soot.Body getBody(soot.SootMethod sm, String phaseName) {
        ////System.out.println("Creating block for sm: "+sm.getName());
        JimpleBodyBuilder jbb = new JimpleBodyBuilder();
        soot.jimple.JimpleBody jb = jbb.createJimpleBody(block, formals, sm);
        
        PackManager.v().getPack("jb").apply(jb);
        return jb;
    }

    public void setFieldInits(ArrayList fieldInits){
        this.fieldInits = fieldInits;
    }
    
    public void setStaticFieldInits(ArrayList staticFieldInits){
        this.staticFieldInits = staticFieldInits;
    }

    public ArrayList getFieldInits() {
        return fieldInits;
    }
    
    public ArrayList getStaticFieldInits() {
        return staticFieldInits;
    }

    public void setStaticInitializerBlocks(ArrayList staticInits) {
        staticInitializerBlocks = staticInits;
    }
    
    public void setInitializerBlocks(ArrayList inits) {
        initializerBlocks = inits;
    }

    public ArrayList getStaticInitializerBlocks() {
        return staticInitializerBlocks;
    }
    
    public ArrayList getInitializerBlocks() {
        return initializerBlocks;
    }
    
    public void setOuterClassThisInit(soot.Local l) {
        outerClassThisInit = l;
    }

    public soot.Local getOuterClassThisInit(){
        return outerClassThisInit;
    }

    public void setPrivateAccessMap(HashMap map){
        privateAccessMap = map;
    }

    public HashMap getPrivateAccessMap() {
        return privateAccessMap;
    }
}
