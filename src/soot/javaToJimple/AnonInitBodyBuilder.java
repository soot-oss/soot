package soot.javaToJimple;

import soot.*;
import java.util.*;

public class AnonInitBodyBuilder extends JimpleBodyBuilder {

    public soot.jimple.JimpleBody createBody(soot.SootMethod sootMethod){
        
        body = soot.jimple.Jimple.v().newBody(sootMethod);
       
        lg = new LocalGenerator(body);

        ArrayList fields = ((AnonClassInitMethodSource)body.getMethod().getSource()).getFinalsList();
        boolean inStaticMethod = ((AnonClassInitMethodSource)body.getMethod().getSource()).inStaticMethod();
        boolean isSubType = ((AnonClassInitMethodSource)body.getMethod().getSource()).isSubType();
        soot.Type superOuterType = ((AnonClassInitMethodSource)body.getMethod().getSource()).superOuterType();
        soot.Type thisOuterType = ((AnonClassInitMethodSource)body.getMethod().getSource()).thisOuterType();
        ArrayList fieldInits = ((AnonClassInitMethodSource)body.getMethod().getSource()).getFieldInits();
        soot.Type outerClassType = ((AnonClassInitMethodSource)body.getMethod().getSource()).outerClassType();
        // this formal needed
        soot.RefType type = sootMethod.getDeclaringClass().getType();
        specialThisLocal = soot.jimple.Jimple.v().newLocal("this", type);
        body.getLocals().add(specialThisLocal);

        soot.jimple.ThisRef thisRef = soot.jimple.Jimple.v().newThisRef(type);

        soot.jimple.Stmt thisStmt = soot.jimple.Jimple.v().newIdentityStmt(specialThisLocal, thisRef);
        body.getUnits().add(thisStmt);
       
        ArrayList invokeList = new ArrayList();
        ArrayList invokeTypeList = new ArrayList();
        
        int numParams = sootMethod.getParameterCount();
        int numFinals = 0;
        
        if (fields != null){
            numFinals = fields.size();
        }
        
        //System.out.println("fields : "+fields);
        int startFinals = numParams - numFinals;
        ArrayList paramsForFinals = new ArrayList();

        soot.Local outerLocal = null;
        
        // param
        Iterator fIt = sootMethod.getParameterTypes().iterator();
        int counter = 0;
        while (fIt.hasNext()){
            soot.Type fType = (soot.Type)fIt.next();
            soot.Local local = soot.jimple.Jimple.v().newLocal("r"+counter, fType);
            body.getLocals().add(local);
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(fType, counter);
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(local, paramRef);

            if (fType.equals(thisOuterType)){
                outerLocal = local;
            }
            //System.out.println("counter: "+counter+" startFinals: "+startFinals);
            if ((counter != 0) && (counter < startFinals)){
                invokeTypeList.add(fType);
                invokeList.add(local);
            }
            else if ((counter == 0) && (!inStaticMethod)) {
                outerLocal = local;   
            }
            else {
                paramsForFinals.add(local);
            }
            body.getUnits().add(stmt);
            counter++;
        }
        SootClass superClass = sootMethod.getDeclaringClass().getSuperclass();
        ArrayList needsRef = soot.javaToJimple.InitialResolver.v().getHasOuterRefInInit();
        if ((needsRef != null) && (needsRef.contains(superClass.getType()))){
            invokeTypeList.add(0, superOuterType);
        }
        SootMethod callMethod = sootMethod.getDeclaringClass().getSuperclass().getMethod("<init>",  invokeTypeList, VoidType.v());
        if ((needsRef != null) && (needsRef.contains(superClass.getType()))){
            if (isSubType){
                invokeList.add(0, outerLocal);
            }
            else {
                System.out.println("super outer type: "+superOuterType);
                System.out.println("outer local: "+outerLocal);
                invokeList.add(0, Util.getThisGivenOuter(superOuterType, new HashMap(), body, new LocalGenerator(body), outerLocal));
            }
        }
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(specialThisLocal, callMethod, invokeList);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        body.getUnits().add(invokeStmt);
        
        // field assign
        if (!inStaticMethod){
            soot.SootField field = sootMethod.getDeclaringClass().getField("this$0", outerClassType);
            soot.jimple.InstanceFieldRef ref = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, field);
            soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(ref, outerLocal);
            body.getUnits().add(assign);
        }
        if (fields != null){
            Iterator finalsIt = paramsForFinals.iterator();
            Iterator fieldsIt = fields.iterator();
            while (finalsIt.hasNext() && fieldsIt.hasNext()){
            
                soot.Local pLocal = (soot.Local)finalsIt.next();
                soot.SootField pField = (soot.SootField)fieldsIt.next();
            
                soot.jimple.FieldRef pRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, pField);
            
                soot.jimple.AssignStmt pAssign = soot.jimple.Jimple.v().newAssignStmt(pRef, pLocal);
                body.getUnits().add(pAssign);
 
            }
        }

        // need to be able to handle any kind of field inits -> make this class
        // extend JimpleBodyBuilder to have access to everything
        
        if (fieldInits != null) {
            handleFieldInits(fieldInits);
        }
   
        ArrayList staticBlocks = ((AnonClassInitMethodSource)body.getMethod().getSource()).getInitializerBlocks();
        if (staticBlocks != null){
            handleStaticBlocks(staticBlocks);
        }
        
        // return
        soot.jimple.ReturnVoidStmt retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
        body.getUnits().add(retStmt);
        //PackManager.v().getTransform("jb.ne").apply(body);
        
    
        return body;
    }
}
