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

package soot.javaToJimple;

import soot.*;
import java.util.*;

public class AnonInitBodyBuilder extends JimpleBodyBuilder {

    public soot.jimple.JimpleBody createBody(soot.SootMethod sootMethod){
        
        body = soot.jimple.Jimple.v().newBody(sootMethod);
       
        lg = new LocalGenerator(body);

        AnonClassInitMethodSource acims = (AnonClassInitMethodSource) body.getMethod().getSource();
        ArrayList fields = acims.getFinalsList();
        boolean inStaticMethod = acims.inStaticMethod();
        boolean isSubType = acims.isSubType();
        soot.Type superOuterType = acims.superOuterType();
        soot.Type thisOuterType = acims.thisOuterType();
        ArrayList fieldInits = acims.getFieldInits();
        soot.Type outerClassType = acims.outerClassType();
        polyglot.types.ClassType polyglotType = acims.polyglotType();
        polyglot.types.ClassType anonType = acims.anonType();
        
        boolean hasOuterRef = ((AnonClassInitMethodSource)body.getMethod().getSource()).hasOuterRef();
        boolean hasQualifier = ((AnonClassInitMethodSource)body.getMethod().getSource()).hasQualifier();
        
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
        
        int startFinals = numParams - numFinals;
        ArrayList paramsForFinals = new ArrayList();

        soot.Local outerLocal = null;
        soot.Local qualifierLocal = null;
       
        // param
        Iterator fIt = sootMethod.getParameterTypes().iterator();
        int counter = 0;
        while (fIt.hasNext()){
            soot.Type fType = (soot.Type)fIt.next();
            soot.Local local = soot.jimple.Jimple.v().newLocal("r"+counter, fType);
            body.getLocals().add(local);
            soot.jimple.ParameterRef paramRef = soot.jimple.Jimple.v().newParameterRef(fType, counter);
            
            soot.jimple.Stmt stmt = soot.jimple.Jimple.v().newIdentityStmt(local, paramRef);

            
            int realArgs = 0;
            if ((hasOuterRef) && (counter == 0)){
                // in a non static method the first param is the outer ref
                outerLocal = local;
                realArgs = 1;
                stmt.addTag(new soot.tagkit.EnclosingTag());
            }
            if ((hasOuterRef) && (hasQualifier) && (counter == 1)){
                // here second param is qualifier if there is one
                qualifierLocal = local;
                realArgs = 2;
                invokeList.add(qualifierLocal);
                stmt.addTag(new soot.tagkit.QualifyingTag());
            }
            else if ((!hasOuterRef) && (hasQualifier) && (counter == 0)){
                qualifierLocal = local;
                realArgs = 1;
                invokeList.add(qualifierLocal);
                stmt.addTag(new soot.tagkit.QualifyingTag());
            }
            
            if ((counter >= realArgs) && (counter < startFinals)){
                invokeTypeList.add(fType);
                invokeList.add(local);
            }
            else if (counter >= startFinals) {
                paramsForFinals.add(local);
            }
            body.getUnits().add(stmt);
            counter++;
        }
        SootClass superClass = sootMethod.getDeclaringClass().getSuperclass();

        //ArrayList needsRef = needsOuterClassRef(polyglotOuterType);//soot.javaToJimple.InitialResolver.v().getHasOuterRefInInit();
        if (needsOuterClassRef(polyglotType)){
        //if ((needsRef != null) && (needsRef.contains(superClass.getType())) ){
            invokeTypeList.add(0, superOuterType);
        }
        SootMethodRef callMethod = Scene.v().makeMethodRef( sootMethod.getDeclaringClass().getSuperclass(), "<init>",  invokeTypeList, VoidType.v(), false);
        if ((!hasQualifier) && (needsOuterClassRef(polyglotType))){// && (needsRef.contains(superClass.getType()))){
            if (isSubType){
                invokeList.add(0, outerLocal);
            }
            else {
                invokeList.add(0, Util.getThisGivenOuter(superOuterType, new HashMap(), body, new LocalGenerator(body), outerLocal));
            }
        }
        soot.jimple.InvokeExpr invoke = soot.jimple.Jimple.v().newSpecialInvokeExpr(specialThisLocal, callMethod, invokeList);

        soot.jimple.Stmt invokeStmt = soot.jimple.Jimple.v().newInvokeStmt(invoke);
        body.getUnits().add(invokeStmt);
       
        //System.out.println("polyglotType: "+polyglotType+" needs ref: "+needsOuterClassRef(polyglotType));
        
        // field assign
        if (!inStaticMethod && needsOuterClassRef(anonType)){
            soot.SootFieldRef field = Scene.v().makeFieldRef( sootMethod.getDeclaringClass(), "this$0", outerClassType, false);
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
            
                soot.jimple.FieldRef pRef = soot.jimple.Jimple.v().newInstanceFieldRef(specialThisLocal, pField.makeRef());
            
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
        
    
        return body;
    }
}
