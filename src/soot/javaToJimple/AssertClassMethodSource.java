package soot.javaToJimple;

import java.util.*;
public class AssertClassMethodSource implements soot.MethodSource {

    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
            
        soot.Body classBody = soot.jimple.Jimple.v().newBody(sootMethod);

        // static invoke of forName
        soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(soot.RefType.v("java.lang.String"), 0);

        
        soot.Local paramLocal = soot.jimple.Jimple.v().newLocal("$r0", soot.RefType.v("java.lang.String"));
        classBody.getLocals().add(paramLocal);
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
        classBody.getUnits().add(stmt);

        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        soot.SootMethod methodToInvoke = soot.Scene.v().getSootClass("java.lang.Class").getMethod("forName", paramTypes, soot.RefType.v("java.lang.Class"));
        soot.Local invokeLocal = soot.jimple.Jimple.v().newLocal("$r1", soot.RefType.v("java.lang.Class"));
        classBody.getLocals().add(invokeLocal);
        ArrayList params = new ArrayList();
        params.add(paramLocal);
        soot.jimple.Expr invokeExpr = soot.jimple.Jimple.v().newStaticInvokeExpr(methodToInvoke, params);
        soot.jimple.Stmt assign = soot.jimple.Jimple.v().newAssignStmt(invokeLocal, invokeExpr);
        classBody.getUnits().add(assign);
            
        // return
        soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnStmt(invokeLocal);
        classBody.getUnits().add(retStmt);

        // catch
        soot.Local catchRefLocal = soot.jimple.Jimple.v().newLocal("$r2", soot.RefType.v("java.lang.ClassNotFoundException"));
        classBody.getLocals().add(catchRefLocal);
        soot.jimple.CaughtExceptionRef caughtRef = soot.jimple.Jimple.v().newCaughtExceptionRef();
        soot.jimple.Stmt caughtIdentity = soot.jimple.Jimple.v().newIdentityStmt(catchRefLocal, caughtRef);
        classBody.getUnits().add(caughtIdentity);
         
        // new no class def found error
        soot.Local noClassDefLocal = soot.jimple.Jimple.v().newLocal("$r3", soot.RefType.v("java.lang.NoClassDefFoundError"));
        classBody.getLocals().add(noClassDefLocal);
        soot.jimple.Expr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v("java.lang.NoClassDefFoundError"));
        soot.jimple.Stmt noClassDefAssign = soot.jimple.Jimple.v().newAssignStmt(noClassDefLocal, newExpr);
        classBody.getUnits().add(noClassDefAssign);
        
        // no class def found invoke
        paramTypes = new ArrayList();
        soot.SootMethod initMethToInvoke = soot.Scene.v().getSootClass("java.lang.NoClassDefFoundError").getMethod("<init>", paramTypes, soot.VoidType.v());
        params = new ArrayList();
        soot.jimple.Expr initInvoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(noClassDefLocal, initMethToInvoke, params);
        soot.jimple.Stmt initStmt = soot.jimple.Jimple.v().newInvokeStmt(initInvoke);
        classBody.getUnits().add(initStmt);

        // get exception message
        soot.Local throwLocal = soot.jimple.Jimple.v().newLocal("$r4", soot.RefType.v("java.lang.Throwable"));
        classBody.getLocals().add(throwLocal);
        paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.Throwable"));
        params = new ArrayList();
        params.add(catchRefLocal);
        soot.SootMethod messageMethToInvoke = soot.Scene.v().getSootClass("java.lang.Throwable").getMethod("initCause", paramTypes, soot.RefType.v("java.lang.Throwable"));

        soot.jimple.Expr messageInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(noClassDefLocal, messageMethToInvoke, params);
        soot.jimple.Stmt messageAssign = soot.jimple.Jimple.v().newAssignStmt(throwLocal, messageInvoke);
        classBody.getUnits().add(messageAssign);

        // throw            
        soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(throwLocal);
        classBody.getUnits().add(throwStmt);
        
        // trap
        soot.Trap trap = soot.jimple.Jimple.v().newTrap(soot.Scene.v().getSootClass("java.lang.ClassNotFoundException"), assign, retStmt, caughtIdentity);
        classBody.getTraps().add(trap);
            

        return classBody;
     
    }
}
