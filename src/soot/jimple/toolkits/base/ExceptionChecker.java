/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

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
        SootMethod meth = ie.XgetMethod();
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
