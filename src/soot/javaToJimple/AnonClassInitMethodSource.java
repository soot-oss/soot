package soot.javaToJimple;

import java.util.*;
import soot.*;

public class AnonClassInitMethodSource implements soot.MethodSource {

    private ArrayList fields;
   
    private boolean inStaticMethod;

    public void inStaticMethod(boolean b){
        inStaticMethod = b;
    }
    
    public void setFieldList(ArrayList list){
        fields = list;
    }

    private soot.Type superOuterType = null;
    private soot.Type thisOuterType = null;

    public void superOuterType(soot.Type t){
        superOuterType = t;
    }

    public void thisOuterType(soot.Type t){
        thisOuterType = t;
    }
    
    public soot.Body getBody(soot.SootMethod sootMethod, String phaseName){
        //System.out.println("getting method: "+sootMethod.getName()+" for class: "+sootMethod.getDeclaringClass());            
        soot.Body body = soot.jimple.Jimple.v().newBody(sootMethod);

        // this formal needed
        soot.RefType type = sootMethod.getDeclaringClass().getType();
        soot.Local thisLocal = soot.jimple.Jimple.v().newLocal("this", type);
        body.getLocals().add(thisLocal);

        soot.jimple.ThisRef thisRef = soot.jimple.Jimple.v().newThisRef(type);

        soot.jimple.Stmt thisStmt = soot.jimple.Jimple.v().newIdentityStmt(thisLocal, thisRef);
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
        /*soot.Type sootType = sootMethod.getParameterType(0);
        soot.Local formalLocal = soot.jimple.Jimple.v().newLocal("r0", sootType);
       
        body.getLocals().add(formalLocal);
        
        soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(sootType, 0);
        soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(formalLocal, paramRef);
        body.getUnits().add(stmt);*/
                   
        // invoke
        //System.out.println("super class of anon: "+sootMethod.getDeclaringClass().getSuperclass());
        //System.out.println("super class of anon meths: "+sootMethod.getDeclaringClass().getSuperclass().getMethods());
        //System.out.println("invoke type list: "+invokeTypeList);
        SootClass superClass = sootMethod.getDeclaringClass().getSuperclass();
        if ((superClass.getName().indexOf("$") != -1) && !soot.Modifier.isStatic(superClass.getModifiers())){
            invokeTypeList.add(0, superOuterType);
        }
        SootMethod callMethod = sootMethod.getDeclaringClass().getSuperclass().getMethod("<init>",  invokeTypeList, VoidType.v());
        if ((superClass.getName().indexOf("$") != -1) && !soot.Modifier.isStatic(superClass.getModifiers())){
            if (superOuterType.equals(thisOuterType)){
                invokeList.add(0, outerLocal);
            }
            else {
                invokeList.add(0, Util.getThisGivenOuter(superOuterType, new HashMap(), body, new LocalGenerator(body), outerLocal));
            }
        }
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(thisLocal, callMethod, invokeList);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        body.getUnits().add(invokeStmt);
        
        // field assign
        if (!inStaticMethod){
            //System.out.println("looking for field this$0 in "+sootMethod.getDeclaringClass().getName()+" with type: "+outerClassType);
            soot.SootField field = sootMethod.getDeclaringClass().getField("this$0", outerClassType);
            soot.jimple.InstanceFieldRef ref = soot.jimple.Jimple.v().newInstanceFieldRef(thisLocal, field);
            soot.jimple.AssignStmt assign = soot.jimple.Jimple.v().newAssignStmt(ref, outerLocal);
            body.getUnits().add(assign);
        }
        if (fields != null){
            Iterator finalsIt = paramsForFinals.iterator();
            Iterator fieldsIt = fields.iterator();
            while (finalsIt.hasNext() && fieldsIt.hasNext()){
            
                soot.Local pLocal = (soot.Local)finalsIt.next();
                soot.SootField pField = (soot.SootField)fieldsIt.next();
            
                soot.jimple.FieldRef pRef = soot.jimple.Jimple.v().newInstanceFieldRef(thisLocal, pField);
            
                soot.jimple.AssignStmt pAssign = soot.jimple.Jimple.v().newAssignStmt(pRef, pLocal);
                body.getUnits().add(pAssign);
 
            }
        }
        // return
        soot.jimple.ReturnVoidStmt retStmt = soot.jimple.Jimple.v().newReturnVoidStmt();
        body.getUnits().add(retStmt);
        //PackManager.v().getTransform("jb.ne").apply(body);
        
        PackManager.v().getPack("jj").apply(body);
    
        return body;
    }
    
    private soot.Type outerClassType;

    public soot.Type outerClassType(){
        return outerClassType;
    }
    
    public void outerClassType(soot.Type type){
        outerClassType = type;
    }
}
