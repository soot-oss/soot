package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.tagkit.*;

public class ExceptionCheckerError extends Exception {

    public ExceptionCheckerError(SootMethod m, SootClass sc, Stmt s, SourceLnPosTag pos){
        method(m);
        excType(sc);
        throwing(s);
        position(pos);
    }
        
    private SootMethod method;
    private SootClass excType;
    private Stmt throwing;
    private SourceLnPosTag position;
    
    public SootMethod method(){
        return method;
    }

    public void method(SootMethod sm){
        method = sm;
    }

    public SootClass excType(){
        return excType;
    }

    public void excType(SootClass sc){
        excType = sc;
    }

    public Stmt throwing(){
        return throwing;
    }

    public void throwing(Stmt s){
        throwing = s;
    }

    public SourceLnPosTag position(){
        return position;
    }

    public void position(SourceLnPosTag pos){
        position = pos;
    }
    
}
