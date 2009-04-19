/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
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
package soot.jimple.spark.ondemand;

import java.util.HashSet;
import java.util.Set;


import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.jimple.spark.internal.TypeManager;
import soot.jimple.spark.ondemand.genericutil.Util;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.SparkField;

public class InnerTypesIncrementalHeuristic implements FieldCheckHeuristic {

    private final TypeManager manager;

    private final Set<RefType> typesToCheck = new HashSet<RefType>();

    private String newTypeOnQuery = null;

    private final Set<RefType> bothEndsTypes = new HashSet<RefType>();

    private final Set<RefType> notBothEndsTypes = new HashSet<RefType>();

    private int numPasses = 0;

    private final int passesInDirection;

    private boolean allNotBothEnds = false;

    public InnerTypesIncrementalHeuristic(TypeManager manager, int maxPasses) {
        this.manager = manager;
        this.passesInDirection = maxPasses / 2;
    }

    public boolean runNewPass() {
        numPasses++;
        if (numPasses == passesInDirection) {
            return switchToNotBothEnds();
        } else {
            if (newTypeOnQuery != null) {
                String topLevelTypeStr = Util.topLevelTypeString(newTypeOnQuery);
                boolean added;
                if(Scene.v().containsType(topLevelTypeStr)) {
                    RefType refType = Scene.v().getRefType(topLevelTypeStr);
                    added = typesToCheck.add(refType);
                } else {
                	added = false;
                }
                newTypeOnQuery = null;
                return added;
            } else {
                return switchToNotBothEnds();
            }
        }
    }

    private boolean switchToNotBothEnds() {
        if (!allNotBothEnds) {
            numPasses = 0;
            allNotBothEnds = true;
            newTypeOnQuery = null;
            typesToCheck.clear();
            return true;
        } else {
            return false;
        }
    }

    public boolean validateMatchesForField(SparkField field) {
        if (field instanceof ArrayElement) {
            return true;
        }
        SootField sootField = (SootField) field;
        RefType declaringType = sootField.getDeclaringClass().getType();
        String declaringTypeStr = declaringType.toString();
        String topLevel = Util.topLevelTypeString(declaringTypeStr);
        RefType refType;
        if(Scene.v().containsType(topLevel)) {
            refType = Scene.v().getRefType(topLevel);
        } else {
        	refType=null;
        }
        for (RefType checkedType : typesToCheck) {
            if (manager.castNeverFails(checkedType, refType)) {
                // System.err.println("validate " + declaringTypeStr);
                return true;
            }
        }
        if (newTypeOnQuery == null) {
            newTypeOnQuery = declaringTypeStr;
        }
        return false;
    }

    public boolean validFromBothEnds(SparkField field) {
        if (allNotBothEnds) {
            return false;
        }
        if (field instanceof ArrayElement) {
            return true;
        }
        SootField sootField = (SootField) field;
        RefType declaringType = sootField.getDeclaringClass().getType();
        if (bothEndsTypes.contains(declaringType)) {
            return true;
        } else if (notBothEndsTypes.contains(declaringType)) {
            return false;
        } else {
            if (SootUtil.hasRecursiveField(declaringType.getSootClass())) {
                notBothEndsTypes.add(declaringType);
                return false;
            } else {
                bothEndsTypes.add(declaringType);
                return true;
            }
        }
    }

    @Override
    public String toString() {
        return typesToCheck.toString();
    }
}
