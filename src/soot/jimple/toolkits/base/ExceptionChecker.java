package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.jimple.toolkits.scalar.*;

public class ExceptionChecker extends BodyTransformer{

    public void internalTransform(Body b, String phaseName, Map options){
        
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            if (s instanceof ThrowStmt){
                ThrowStmt ts = (ThrowStmt)s;
                if (!isThrowDeclared(b, ts)) {
                    //throw new RuntimeException("throw of type: "+ts.getOp().getType()+" is not declared to be thrown by method: "+b.getMethod().getName());
                    System.out.println("throw of type: "+ts.getOp().getType()+" is not declared to be thrown by method: "+b.getMethod().getName());
                }
            }
        }
    }

    private boolean isThrowDeclared(Body b, ThrowStmt ts){

        if (!b.getMethod().throwsException(((RefType)ts.getOp().getType()).getSootClass()) && !ts.hasTag("ThrowCreatedByCompilerTag")) return false;
        return true;
    }
}
