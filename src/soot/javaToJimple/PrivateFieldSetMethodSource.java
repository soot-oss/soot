package soot.javaToJimple;

import java.util.*;
public class PrivateFieldSetMethodSource implements soot.MethodSource {

    private final soot.Type fieldType;
    private final String fieldName;
    private final boolean isStatic;
    public PrivateFieldSetMethodSource( soot.Type fieldType, String fieldName, boolean isStatic ) {
    	this.fieldType = fieldType;
    	this.fieldName = fieldName;
    	this.isStatic = isStatic;
    }
    
    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
        
        soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);
        LocalGenerator lg = new LocalGenerator(body);
        
        soot.Local fieldBase = null;
        soot.Local assignLocal = null;
        // create parameters
        int paramCounter = 0;
        Iterator paramIt = sootMethod.getParameterTypes().iterator();
        while (paramIt.hasNext()) {
            soot.Type sootType = (soot.Type)paramIt.next();
            soot.Local paramLocal = lg.generateLocal(sootType);
            
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, paramCounter);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
            body.getUnits().add(stmt);
            
            if (paramCounter == 0){
                fieldBase = paramLocal;
            }
            assignLocal = paramLocal;
            paramCounter++;
        }
        
        // create field type local
        //soot.Local fieldLocal = lg.generateLocal(fieldType);
        // assign local to fieldRef
        soot.SootFieldRef field = soot.Scene.v().makeFieldRef( sootMethod.getDeclaringClass(), fieldName, fieldType);

        soot.jimple.FieldRef fieldRef = null;
        if (isStatic) {
            fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(field);
        }
        else {
            fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(fieldBase, field);
        }
        //System.out.println("fieldRef: "+fieldRef+" assignLocal: "+assignLocal);
        soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldRef, assignLocal);
        body.getUnits().add(assign);

        //return local
        soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnStmt(assignLocal);
        body.getUnits().add(retStmt);
        
        return body;
     
    }
}
