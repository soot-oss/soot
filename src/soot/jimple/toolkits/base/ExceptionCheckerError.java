package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;

public class ExceptionCheckerError extends Exception {

    public ExceptionCheckerError(SootMethod m, SootClass sc, Stmt s){
        method(m);
        excType(sc);
        throwing(s);
    }
        
    private SootMethod method;
    private SootClass excType;
    private Stmt throwing;

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
}
