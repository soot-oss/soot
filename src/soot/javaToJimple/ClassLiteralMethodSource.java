package soot.javaToJimple;

import java.util.*;
public class ClassLiteralMethodSource implements soot.MethodSource {

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
        soot.Local throwLocal = soot.jimple.Jimple.v().newLocal("$r3", soot.RefType.v("java.lang.NoClassDefFoundError"));
        classBody.getLocals().add(throwLocal);
        soot.jimple.Expr newExpr = soot.jimple.Jimple.v().newNewExpr(soot.RefType.v("java.lang.NoClassDefFoundError"));
        soot.jimple.Stmt throwAssign = soot.jimple.Jimple.v().newAssignStmt(throwLocal, newExpr);
        classBody.getUnits().add(throwAssign);

        // get exception message
        soot.Local messageLocal = soot.jimple.Jimple.v().newLocal("$r4", soot.RefType.v("java.lang.String"));
        classBody.getLocals().add(messageLocal);
        //params = new ArrayList();
        //params.add(catchRefLocal);
        soot.SootMethod messageMethToInvoke = soot.Scene.v().getSootClass("java.lang.Throwable").getMethod("getMessage", new ArrayList(), soot.RefType.v("java.lang.String"));

        soot.jimple.Expr messageInvoke = soot.jimple.Jimple.v().newVirtualInvokeExpr(catchRefLocal, messageMethToInvoke, new ArrayList());
        soot.jimple.Stmt messageAssign = soot.jimple.Jimple.v().newAssignStmt(messageLocal, messageInvoke);
        classBody.getUnits().add(messageAssign);

        // no class def found init
        paramTypes = new ArrayList();
        paramTypes.add(soot.RefType.v("java.lang.String"));
        soot.SootMethod initMethToInvoke = soot.Scene.v().loadClassAndSupport("java.lang.NoClassDefFoundError").getMethod("<init>", paramTypes, soot.VoidType.v());
        params = new ArrayList();
        params.add(messageLocal);
        soot.jimple.Expr initInvoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(throwLocal, initMethToInvoke, params);
        soot.jimple.Stmt initStmt = soot.jimple.Jimple.v().newInvokeStmt(initInvoke);
        classBody.getUnits().add(initStmt);
            
        // throw            
        soot.jimple.Stmt throwStmt = soot.jimple.Jimple.v().newThrowStmt(throwLocal);
        throwStmt.addTag(new soot.tagkit.ThrowCreatedByCompilerTag());
        classBody.getUnits().add(throwStmt);
        
        // trap
        soot.Trap trap = soot.jimple.Jimple.v().newTrap(soot.Scene.v().getSootClass("java.lang.ClassNotFoundException"), assign, retStmt, caughtIdentity);
        classBody.getTraps().add(trap);
            

        return classBody;
     
    }
}
