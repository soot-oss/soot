package soot.javaToJimple;

import java.util.*;
public class PrivateFieldAccMethodSource implements soot.MethodSource {

    private polyglot.types.FieldInstance fieldInst;
    private soot.Type fieldType;
    private String fieldName;
    private soot.SootClass classToInvoke;
    
    public void fieldName(String n){
        fieldName = n;
    }
    
    public void fieldType(soot.Type type){
        fieldType = type;
    }

    public void classToInvoke(soot.SootClass sc){
        classToInvoke = sc;
    }
        
    public void setFieldInst(polyglot.types.FieldInstance fi) {
        fieldInst = fi;
    }
    
    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
        
        soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);
        LocalGenerator lg = new LocalGenerator(body);
        
        soot.Local fieldBase = null;
        // create parameters
        Iterator paramIt = sootMethod.getParameterTypes().iterator();
        while (paramIt.hasNext()) {
            soot.Type sootType = (soot.Type)paramIt.next();
            soot.Local paramLocal = lg.generateLocal(sootType);
            
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, 0);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(paramLocal, paramRef);
            body.getUnits().add(stmt);
            fieldBase = paramLocal;
        }
        
        // create field type local
        soot.Local fieldLocal = lg.generateLocal(fieldType);
        // assign local to fieldRef
        soot.SootField field = classToInvoke.getField(fieldName, fieldType);

        soot.jimple.FieldRef fieldRef = null;
        if (field.isStatic()) {
            fieldRef = soot.jimple.Jimple.v().newStaticFieldRef(field);
        }
        else {
            fieldRef = soot.jimple.Jimple.v().newInstanceFieldRef(fieldBase, field);
        }
        soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(fieldLocal, fieldRef);
        body.getUnits().add(assign);

        //return local
        soot.jimple.Stmt retStmt = soot.jimple.Jimple.v().newReturnStmt(fieldLocal);
        body.getUnits().add(retStmt);
        
        return body;
     
    }
}
