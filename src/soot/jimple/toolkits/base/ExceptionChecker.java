package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.jimple.toolkits.scalar.*;

public class ExceptionChecker extends BodyTransformer{

    FastHierarchy hierarchy;
    ExceptionCheckerErrorReporter reporter;
    
    public ExceptionChecker(ExceptionCheckerErrorReporter r){
        this.reporter = r;
    }
    
    protected void internalTransform(Body b, String phaseName, Map options){
        
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            if (s instanceof ThrowStmt){
                ThrowStmt ts = (ThrowStmt)s;
                checkThrow(b, ts);
            }
            else if (s instanceof InvokeStmt){
                InvokeStmt is = (InvokeStmt)s;
                checkInvoke(b, is);
            }
        }
    }

    private void checkThrow(Body b, ThrowStmt ts){
        if (isThrowDeclared(b, ((RefType)ts.getOp().getType()).getSootClass()) || isThrowFromCompiler(ts) || isExceptionCaught(b, ts, (RefType)ts.getOp().getType())) return;
        if (reporter != null){
            reporter.reportError(new ExceptionCheckerError(b.getMethod(), ((RefType)ts.getOp().getType()).getSootClass(), ts));
        }
    }

    // does the method declare the throw
    private boolean isThrowDeclared(Body b, SootClass throwClass){
        if (b.getMethod().throwsException(throwClass)) return true;
        return false;
    }

    // is the throw created by the compiler
    private boolean isThrowFromCompiler(ThrowStmt ts){
        if (ts.hasTag("ThrowCreatedByCompilerTag")) return true;
        return false;
    }

    // is the throw caught inside the method
    private boolean isExceptionCaught(Body b, Stmt s, RefType throwType){
        if (hierarchy == null){
            hierarchy = new FastHierarchy();
        }
        Iterator it = b.getTraps().iterator();
        while (it.hasNext()){
            Trap trap = (Trap)it.next();
            if (trap.getException().getType().equals(throwType) || hierarchy.getSubclassesOf(((RefType)trap.getException().getType()).getSootClass()).contains(throwType.getSootClass())){
                if (isThrowInStmtRange(b, (Stmt)trap.getBeginUnit(), (Stmt)trap.getEndUnit(), s)) return true;
            }
        }
        return false;
    }

    private boolean isThrowInStmtRange(Body b, Stmt begin, Stmt end, Stmt s){
        Iterator it = b.getUnits().iterator(begin, end);
        while (it.hasNext()){
            if (it.next().equals(s)) return true;
        }
        return false;
    }

    private void checkInvoke(Body b, InvokeStmt is){
        SootMethod meth = is.getInvokeExpr().getMethod();
        Iterator it = meth.getExceptions().iterator();
        while (it.hasNext()){
            SootClass sc = (SootClass)it.next();
            if (isThrowDeclared(b, sc) || isExceptionCaught(b, is, sc.getType())) continue;
            if (reporter != null){
                reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, is));
            }
        }
    }
}
