package soot.javaToJimple;

import java.util.*;
import soot.*;

public class AnonClassInitMethodSource implements soot.MethodSource {

    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
            
        soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);

        // this formal needed
        soot.RefType type = sootMethod.getDeclaringClass().getType();
        soot.Local thisLocal = soot.jimple.Jimple.v().newLocal("this", type);
        body.getLocals().add(thisLocal);

        soot.jimple.ThisRef thisRef = soot.jimple.Jimple.v().newThisRef(type);

        soot.jimple.Stmt thisStmt = soot.jimple.Jimple.v().newIdentityStmt(thisLocal, thisRef);
        body.getUnits().add(thisStmt);
       
        // param
        soot.Type sootType = sootMethod.getParameterType(0);
        soot.Local formalLocal = soot.jimple.Jimple.v().newLocal("r0", sootType);
       
        body.getLocals().add(formalLocal);
        
        soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, 0);
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, paramRef);
        body.getUnits().add(stmt);
                   
        // invoke
        soot.SootMethod callMethod = soot.Scene.v().getSootClass("java.lang.Object").getMethod("<init>", new ArrayList(), soot.VoidType.v());
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(thisLocal, callMethod, new ArrayList());

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        body.getUnits().add(invokeStmt);
        
        // field assign
        soot.SootField field = sootMethod.getDeclaringClass().getField("this$0", sootType);
        soot.jimple.InstanceFieldRef ref = soot.jimple.Jimple.v().newInstanceFieldRef(thisLocal, field);
        soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(ref, formalLocal);
        body.getUnits().add(assign);
       

        // return
        soot.jimple.ReturnVoidStmt retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
        body.getUnits().add(retStmt);
        
        PackManager.v().getPack("jb").apply(body);
    
        return body;
    }
}
