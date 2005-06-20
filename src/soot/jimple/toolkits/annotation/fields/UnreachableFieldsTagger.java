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

package soot.jimple.toolkits.annotation.fields;
import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.util.queue.*;

/** A scene transformer that adds tags to unused fields. */
public class UnreachableFieldsTagger extends SceneTransformer
{ 
    public UnreachableFieldsTagger (Singletons.Global g) {}
    public static UnreachableFieldsTagger v() { return G.v().soot_jimple_toolkits_annotation_fields_UnreachableFieldsTagger();}

    protected void internalTransform(String phaseName, Map options){

        // make list of all fields
        ArrayList fieldList = new ArrayList();
        
        Iterator getClassesIt = Scene.v().getApplicationClasses().iterator();
        while (getClassesIt.hasNext()) {
            SootClass appClass = (SootClass)getClassesIt.next();
            //System.out.println("class to check: "+appClass); 
            Iterator getFieldsIt = appClass.getFields().iterator();
            while (getFieldsIt.hasNext()) {
                SootField field = (SootField)getFieldsIt.next();
                //System.out.println("adding field: "+field);
                fieldList.add(field);
            }
        }
        
        // from all bodies get all use boxes and eliminate used fields
        getClassesIt = Scene.v().getApplicationClasses().iterator();
        while (getClassesIt.hasNext()) {
            SootClass appClass = (SootClass)getClassesIt.next();
            Iterator mIt = appClass.getMethods().iterator();
            while (mIt.hasNext()) {
                SootMethod sm = (SootMethod)mIt.next();
                //System.out.println("checking method: "+sm.getName());
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

                        if (fieldList.contains(f)) {
                            int index = fieldList.indexOf(f);
                            fieldList.remove(index);
                            //System.out.println("removed field: "+f);
                        }
                    
                    }
                }
            
            }
        }
        
        // tag unused fields
        Iterator unusedIt = fieldList.iterator();
        while (unusedIt.hasNext()) {
            SootField unusedField = (SootField)unusedIt.next();
            unusedField.addTag(new StringTag("Field "+unusedField.getName()+" is not used!", "Unreachable Fields"));
            unusedField.addTag(new ColorTag(ColorTag.RED, true, "Unreachable Fields"));   
            //System.out.println("tagged field: "+unusedField);

        }
    }

}


