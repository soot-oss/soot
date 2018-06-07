package soot.jimple.spark.ondemand;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2007 Manu Sridharan
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.SootField;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.SparkField;

/**
 * for hacking around with stuff
 * 
 * @author manu
 * 
 */
public class ManualFieldCheckHeuristic implements FieldCheckHeuristic {

  private boolean allNotBothEnds = false;

  public boolean runNewPass() {
    if (!allNotBothEnds) {
      allNotBothEnds = true;
      return true;
    }
    return false;
  }

  private static final String[] importantTypes = new String[] {
      // "ca.mcgill.sable.util.ArrayList",
      // "ca.mcgill.sable.util.ArrayList$ArrayIterator",
      // "ca.mcgill.sable.util.AbstractList$AbstractListIterator",
      /* "ca.mcgill.sable.util.VectorList", */ "java.util.Vector", "java.util.Hashtable", "java.util.Hashtable$Entry",
      "java.util.Hashtable$Enumerator", "java.util.LinkedList", "java.util.LinkedList$Entry", "java.util.AbstractList$Itr",
      // "ca.mcgill.sable.util.HashMap", "ca.mcgill.sable.util.LinkedList",
      // "ca.mcgill.sable.util.LinkedList$LinkedListIterator",
      // "ca.mcgill.sable.util.LinkedList$Node",
      /* "ca.mcgill.sable.soot.TrustingMonotonicArraySet", */ "java.util.Vector$1", "java.util.ArrayList", };

  private static final String[] notBothEndsTypes = new String[] { "java.util.Hashtable$Entry",
      "java.util.LinkedList$Entry", /* "ca.mcgill.sable.util.LinkedList$Node" */ };

  public boolean validateMatchesForField(SparkField field) {
    if (field instanceof ArrayElement) {
      return true;
    }
    SootField sootField = (SootField) field;
    String fieldTypeStr = sootField.getDeclaringClass().getType().toString();
    for (String typeName : importantTypes) {
      if (fieldTypeStr.equals(typeName)) {
        return true;
      }
    }
    return false;
  }

  public boolean validFromBothEnds(SparkField field) {
    if (allNotBothEnds) {
      return false;
    }
    if (field instanceof SootField) {
      SootField sootField = (SootField) field;
      String fieldTypeStr = sootField.getDeclaringClass().getType().toString();
      for (String typeName : notBothEndsTypes) {
        if (fieldTypeStr.equals(typeName)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return "Manual annotations";
  }

}
