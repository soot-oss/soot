package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.jimple.toolkits.scalar.*;
import soot.tagkit.*;

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
            else if ((s instanceof AssignStmt) && (((AssignStmt)s).getRightOp() instanceof InvokeExpr)){
                InvokeExpr ie = (InvokeExpr)((AssignStmt)s).getRightOp();
                checkInvokeExpr(b, ie, s);
            }
        }
    }

    private void checkThrow(Body b, ThrowStmt ts){
        if (isThrowDeclared(b, ((RefType)ts.getOp().getType()).getSootClass()) || isThrowFromCompiler(ts) || isExceptionCaught(b, ts, (RefType)ts.getOp().getType())) return;
        if (reporter != null){
            reporter.reportError(new ExceptionCheckerError(b.getMethod(), ((RefType)ts.getOp().getType()).getSootClass(), ts, (SourceLnPosTag)ts.getOpBox().getTag("SourceLnPosTag")));
        }
    }

    // does the method declare the throw if its a throw that needs declaring
    // RuntimeException and subclasses do not need to be declared
    // Error and subclasses do not need to be declared
    private boolean isThrowDeclared(Body b, SootClass throwClass){
        if (hierarchy == null){
            hierarchy = new FastHierarchy();
        }

        // handles case when exception is RuntimeException or Error
        if (throwClass.equals(Scene.v().getSootClass("java.lang.RuntimeException")) || throwClass.equals(Scene.v().getSootClass("java.lang.Error"))) return true;
        // handles case when exception is a subclass of RuntimeException or Error
        if (hierarchy.isSubclass(throwClass, Scene.v().getSootClass("java.lang.RuntimeException")) || hierarchy.isSubclass(throwClass, Scene.v().getSootClass("java.lang.Error"))) return true;

        // handles case when exact exception is thrown
        if (b.getMethod().throwsException(throwClass)) return true;

        // handles case when a super type of the exception is thrown
        Iterator it = b.getMethod().getExceptions().iterator();
        while (it.hasNext()){
            SootClass nextEx = (SootClass)it.next();
            if (hierarchy.isSubclass(throwClass, nextEx)) return true;
        }
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
            if (trap.getException().getType().equals(throwType) || hierarchy.isSubclass(throwType.getSootClass(), ((RefType)trap.getException().getType()).getSootClass())){
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
        checkInvokeExpr(b, is.getInvokeExpr(), is);
    }

    private void checkInvokeExpr(Body b, InvokeExpr ie, Stmt s){
        SootMethod meth = ie.getMethod();
        Iterator it = meth.getExceptions().iterator();
        while (it.hasNext()){
            SootClass sc = (SootClass)it.next();
            if (isThrowDeclared(b, sc) || isExceptionCaught(b, s, sc.getType())) continue;
            if (reporter != null){
                if (s instanceof InvokeStmt){
                    reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, s, (SourceLnPosTag)s.getTag("SourceLnPosTag")));
                }
                else if (s instanceof AssignStmt){
                    reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, s, (SourceLnPosTag)((AssignStmt)s).getRightOpBox().getTag("SourceLnPosTag")));
                }
            }
        }
    }
}
