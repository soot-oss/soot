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

package soot.jimple.toolkits.annotation.qualifiers;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.util.queue.*;
import soot.jimple.toolkits.callgraph.*;

/** a scene transformer that add tags to indicate the tightest qualifies 
 * possible for fields and methods (ie: private, protected or public)
 */
public class TightestQualifiersTagger extends SceneTransformer {
    
    public TightestQualifiersTagger(Singletons.Global g) {}
    public static TightestQualifiersTagger v() { return G.v().soot_jimple_toolkits_annotation_qualifiers_TightestQualifiersTagger();}

    public final static int RESULT_PUBLIC = 0;
    public final static int RESULT_PACKAGE = 1;
    public final static int RESULT_PROTECTED = 2;
    public final static int RESULT_PRIVATE = 3;
    
    private HashMap methodResultsMap = new HashMap();
    private HashMap fieldResultsMap = new HashMap();
    private MethodToContexts methodToContexts;

    protected void internalTransform(String phaseName, Map options){
    
        handleMethods();
        handleFields();
    }

    private void handleMethods() {
        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext()){
            SootClass appClass = (SootClass)classesIt.next();
            Iterator methsIt = appClass.getMethods().iterator();
            while (methsIt.hasNext()){
                SootMethod sm = (SootMethod)methsIt.next();
                // for now if its unreachable do nothing
                if (!Scene.v().getReachableMethods().contains(sm)) continue;
                analyzeMethod(sm);
            }
        }

        Iterator methStatIt = methodResultsMap.keySet().iterator();
        while (methStatIt.hasNext()) {
            SootMethod meth = (SootMethod)methStatIt.next();
            int result = ((Integer)methodResultsMap.get(meth)).intValue();
            String sRes = "Public";
            if (result == RESULT_PUBLIC){
                sRes = "Public";
            }
            else if (result == RESULT_PROTECTED){
                sRes = "Protected";
            }
            else if (result == RESULT_PACKAGE){
                sRes = "Package";
            }
            else if (result == RESULT_PRIVATE){
                sRes = "Private";
            }
            
            String actual = null;
            if (Modifier.isPublic(meth.getModifiers())){
                actual = "Public";
            }
            else if (Modifier.isProtected(meth.getModifiers())){
                actual = "Protected";
            }
            else if (Modifier.isPrivate(meth.getModifiers())){
                actual = "Private";
            }
            else {    
                actual = "Package";
            }
            
            //System.out.println("Method: "+meth.getName()+" has "+actual+" level access, can have: "+sRes+" level access.");
        
            if (!sRes.equals(actual)) {
                if (meth.getName().equals("<init>")){
                    meth.addTag(new StringTag("Constructor: "+meth.getDeclaringClass().getName()+" has "+actual+" level access, can have: "+sRes+" level access.", "Tightest Qualifiers"));
                }
                else {
                    meth.addTag(new StringTag("Method: "+meth.getName()+" has "+actual+" level access, can have: "+sRes+" level access.", "Tightest Qualifiers"));
                }
                meth.addTag(new ColorTag(255, 10, 0, true, "Tightest Qualifiers"));
            }
        }
    }
    

    private void analyzeMethod(SootMethod sm){
       
        CallGraph cg = Scene.v().getCallGraph();

        //Iterator eIt = Scene.v().getEntryPoints().iterator();
        //while (eIt.hasNext()){
        //    System.out.println(eIt.next());
        //}
        
        if( methodToContexts == null ) {
            methodToContexts = new MethodToContexts( Scene.v().getReachableMethods().listener() );
        }
        
        for( Iterator momcIt = methodToContexts.get(sm).iterator(); momcIt.hasNext(); ) {
            final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
            Iterator callerEdges = cg.edgesInto(momc);
            while (callerEdges.hasNext()){
                Edge callEdge = (Edge)callerEdges.next();
                if (!callEdge.isExplicit()) continue;
                SootMethod methodCaller = callEdge.src();
                //System.out.println("Caller edge type: "+Edge.kindToString(callEdge.kind()));
                SootClass callingClass = methodCaller.getDeclaringClass();
                // public methods
                if (Modifier.isPublic(sm.getModifiers())){
                    analyzePublicMethod(sm, callingClass); 
                }
                // protected methods
                else if (Modifier.isProtected(sm.getModifiers())){
                    analyzeProtectedMethod(sm, callingClass); 
                }
                // private methods - do nothing
                else if (Modifier.isPrivate(sm.getModifiers())){
                }
                // package level methods
                else {
                    analyzePackageMethod(sm, callingClass);
                }
                
            }
        }
        
    }

    private boolean analyzeProtectedMethod(SootMethod sm, SootClass callingClass){
        SootClass methodClass = sm.getDeclaringClass();
        
        //System.out.println("protected method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class: "+callingClass.getName());

        boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);
        
        if (!insidePackageAccess && subClassAccess) {
            methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
            return true;
        }
        else if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sm);
            return false;
        }
        else {
            updateToPrivate(sm);
            return false;
        }    
    }
        
    private boolean analyzePackageMethod(SootMethod sm, SootClass callingClass){
        SootClass methodClass = sm.getDeclaringClass();

        //System.out.println("package method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class: "+callingClass.getName());
        boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);
        
        if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sm);
            return true;
        }
        else {
            updateToPrivate(sm);
            return false;
        }
    }
    
    private boolean analyzePublicMethod(SootMethod sm, SootClass callingClass){
        
        SootClass methodClass = sm.getDeclaringClass();
        
        //System.out.println("public method: "+sm.getName()+" in class: "+methodClass.getName()+" calling class: "+callingClass.getName());
           
        boolean insidePackageAccess = isCallSamePackage(callingClass, methodClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, methodClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, methodClass);
                
        if (!insidePackageAccess && !subClassAccess){
            methodResultsMap.put(sm, new Integer(RESULT_PUBLIC));
            return true;
        }
        else if (!insidePackageAccess && subClassAccess) {
            updateToProtected(sm);
            return false;
        }
        else if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sm);
            return false;
        }
        else {
            updateToPrivate(sm);
            return false;
        }
                
    }

    private void updateToProtected(SootMethod sm){
        if (!methodResultsMap.containsKey(sm)){
            methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
        }
        else {
            if (((Integer)methodResultsMap.get(sm)).intValue() != RESULT_PUBLIC){
                methodResultsMap.put(sm, new Integer(RESULT_PROTECTED));
            }
        }
    }
    
    private void updateToPackage(SootMethod sm){
        if (!methodResultsMap.containsKey(sm)){
            methodResultsMap.put(sm, new Integer(RESULT_PACKAGE));
        }
        else {
            if (((Integer)methodResultsMap.get(sm)).intValue() == RESULT_PRIVATE){
                methodResultsMap.put(sm, new Integer(RESULT_PACKAGE));
            }
        }
    }
    
    private void updateToPrivate(SootMethod sm){
        if (!methodResultsMap.containsKey(sm)) {
            methodResultsMap.put(sm, new Integer(RESULT_PRIVATE));
        }
    }
    
    private boolean isCallClassMethodClass(SootClass call, SootClass check){
        if (call.equals(check)) return true;
        return false;
    }

    private boolean isCallClassSubClass(SootClass call, SootClass check){
        if (!call.hasSuperclass()) return false;
        if (call.getSuperclass().equals(check)) return true;
        return false;
    }

    private boolean isCallSamePackage(SootClass call, SootClass check){
        if (call.getPackageName().equals(check.getPackageName())) return true;
        return false;
    }

    private void handleFields(){
        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext()){
            SootClass appClass = (SootClass)classesIt.next();
            Iterator fieldsIt = appClass.getFields().iterator();
            while (fieldsIt.hasNext()){
                SootField sf = (SootField)fieldsIt.next();
                analyzeField(sf);
            }
        }
        
        Iterator fieldStatIt = fieldResultsMap.keySet().iterator();
        while (fieldStatIt.hasNext()) {
            SootField f = (SootField)fieldStatIt.next();
            int result = ((Integer)fieldResultsMap.get(f)).intValue();
            String sRes = "Public";
            if (result == RESULT_PUBLIC){
                sRes = "Public";
            }
            else if (result == RESULT_PROTECTED){
                sRes = "Protected";
            }
            else if (result == RESULT_PACKAGE){
                sRes = "Package";
            }
            else if (result == RESULT_PRIVATE){
                sRes = "Private";
            }
            
            String actual = null;
            if (Modifier.isPublic(f.getModifiers())){
                //System.out.println("Field: "+f.getName()+" is public");
                actual = "Public";
            }
            else if (Modifier.isProtected(f.getModifiers())){
                actual = "Protected";
            }
            else if (Modifier.isPrivate(f.getModifiers())){
                actual = "Private";
            }
            else {    
                actual = "Package";
            }
            
            //System.out.println("Field: "+f.getName()+" has "+actual+" level access, can have: "+sRes+" level access.");
        
            if (!sRes.equals(actual)){
                f.addTag(new StringTag("Field: "+f.getName()+" has "+actual+" level access, can have: "+sRes+" level access.", "Tightest Qualifiers"));
                f.addTag(new ColorTag(255, 10, 0, true, "Tightest Qualifiers"));
            }
        }
    }
    
    private void analyzeField(SootField sf){
       
        // from all bodies get all use boxes and eliminate used fields
        Iterator classesIt = Scene.v().getApplicationClasses().iterator();
        while (classesIt.hasNext()) {
            SootClass appClass = (SootClass)classesIt.next();
            Iterator mIt = appClass.getMethods().iterator();
            while (mIt.hasNext()) {
                SootMethod sm = (SootMethod)mIt.next();
                if (!sm.hasActiveBody()) continue;
                if (!Scene.v().getReachableMethods().contains(sm)) continue;
                Body b = sm.getActiveBody();

                Iterator usesIt = b.getUseBoxes().iterator();
                while (usesIt.hasNext()) {
                    ValueBox vBox = (ValueBox)usesIt.next();
                    Value v = vBox.getValue();
                    if (v instanceof FieldRef) {
                        FieldRef fieldRef = (FieldRef)v;
                        SootField f = fieldRef.getField();
                        if (f.equals(sf)) {
                            if (Modifier.isPublic(sf.getModifiers())) {
                                if (analyzePublicField(sf, appClass)) return;
                            }
                            else if (Modifier.isProtected(sf.getModifiers())) {
                                analyzeProtectedField(sf, appClass);
                            }
                            else if(Modifier.isPrivate(sf.getModifiers())) {
                            }
                            else {
                                analyzePackageField(sf, appClass);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean analyzePublicField(SootField sf, SootClass callingClass){
        SootClass fieldClass = sf.getDeclaringClass();
        
           
        boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);
                
        if (!insidePackageAccess && !subClassAccess){
            fieldResultsMap.put(sf, new Integer(RESULT_PUBLIC));
            return true;
        }
        else if (!insidePackageAccess && subClassAccess) {
            updateToProtected(sf);
            return false;
        }
        else if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sf);
            return false;
        }
        else {
            updateToPrivate(sf);
            return false;
        }
        
    }

    private boolean analyzeProtectedField(SootField sf, SootClass callingClass){
        SootClass fieldClass = sf.getDeclaringClass();

        boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);
        
        if (!insidePackageAccess && subClassAccess) {
            fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
            return true;
        }
        else if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sf);
            return false;
        }
        else {
            updateToPrivate(sf);
            return false;
        }    
    }

    private boolean analyzePackageField(SootField sf, SootClass callingClass){
        SootClass fieldClass = sf.getDeclaringClass();

        boolean insidePackageAccess = isCallSamePackage(callingClass, fieldClass);
        boolean subClassAccess = isCallClassSubClass(callingClass, fieldClass);
        boolean sameClassAccess = isCallClassMethodClass(callingClass, fieldClass);
        
        if (insidePackageAccess && !sameClassAccess) {
            updateToPackage(sf);
            return true;
        }
        else {
            updateToPrivate(sf);
            return false;
        }
    }
    
    private void updateToProtected(SootField sf){
        if (!fieldResultsMap.containsKey(sf)){
            fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
        }
        else {
            if (((Integer)fieldResultsMap.get(sf)).intValue() != RESULT_PUBLIC){
                fieldResultsMap.put(sf, new Integer(RESULT_PROTECTED));
            }
        }
    }
    
    private void updateToPackage(SootField sf){
        if (!fieldResultsMap.containsKey(sf)){
            fieldResultsMap.put(sf, new Integer(RESULT_PACKAGE));
        }
        else {
            if (((Integer)fieldResultsMap.get(sf)).intValue() == RESULT_PRIVATE){
                fieldResultsMap.put(sf, new Integer(RESULT_PACKAGE));
            }
        }
    }
    
    private void updateToPrivate(SootField sf){
        if (!fieldResultsMap.containsKey(sf)) {
            fieldResultsMap.put(sf, new Integer(RESULT_PRIVATE));
        }
    }
}
