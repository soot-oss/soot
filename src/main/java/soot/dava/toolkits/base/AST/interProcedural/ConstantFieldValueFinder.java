package soot.dava.toolkits.base.AST.interProcedural;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.dava.DavaBody;
import soot.dava.DecompilationException;
import soot.dava.internal.AST.ASTNode;
import soot.dava.toolkits.base.AST.traversals.AllDefinitionsFinder;
import soot.jimple.DefinitionStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NumericConstant;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.util.Chain;

/*
 * Deemed important because of obfuscation techniques which add crazy
 * control flow under some condition which is never executed because
 * it uses some field which is always false!!
 *
 * Goal:
 *    Prove that a field is never assigned a value or that if it is assigned a value
 *    we can statically tell this value
 *
 *
 */
public class ConstantFieldValueFinder {
  public final boolean DEBUG = false;

  public static String combiner = "_$p$g_";

  private final HashMap<String, SootField> classNameFieldNameToSootFieldMapping = new HashMap<>();
  private final HashMap<String, ArrayList<Value>> fieldToValues = new HashMap<>();
  private final HashMap<String, Object> primTypeFieldValueToUse = new HashMap<>();
  private final Chain<SootClass> appClasses;

  public ConstantFieldValueFinder(Chain<SootClass> classes) {
    appClasses = classes;
    debug("ConstantFieldValueFinder -- applyAnalyses", "computing Method Summaries");
    computeFieldToValuesAssignedList();
    valuesForPrimTypeFields();
  }

  /*
   * The hashMap returned contains a mapping of class + combiner + field ----> Double/Float/Long/Integer if there is no
   * mapping for a particular field then that means we couldnt detect a constant value for it
   */
  public HashMap<String, Object> getFieldsWithConstantValues() {
    return primTypeFieldValueToUse;
  }

  public HashMap<String, SootField> getClassNameFieldNameToSootFieldMapping() {
    return classNameFieldNameToSootFieldMapping;
  }

  /*
   * This method gives values to all the fields in all the classes if they can be determined statically We only care about
   * fields which have primitive types
   */
  private void valuesForPrimTypeFields() {
    // go through all the classes
    for (SootClass s : appClasses) {
      debug("\nvaluesforPrimTypeFields", "Processing class " + s.getName());

      String declaringClass = s.getName();
      // all fields of the class
      for (SootField f : s.getFields()) {
        Type fieldType = f.getType();
        if (!(fieldType instanceof PrimType)) {
          continue;
        }

        String combined = declaringClass + combiner + f.getName();
        classNameFieldNameToSootFieldMapping.put(combined, f);

        Object value = null;

        // check for constant value tags
        if (fieldType instanceof DoubleType) {
          DoubleConstantValueTag t = (DoubleConstantValueTag) f.getTag(DoubleConstantValueTag.NAME);
          if (t != null) {
            value = t.getDoubleValue();
          }
        } else if (fieldType instanceof FloatType) {
          FloatConstantValueTag t = (FloatConstantValueTag) f.getTag(FloatConstantValueTag.NAME);
          if (t != null) {
            value = t.getFloatValue();
          }
        } else if (fieldType instanceof LongType) {
          LongConstantValueTag t = (LongConstantValueTag) f.getTag(LongConstantValueTag.NAME);
          if (t != null) {
            value = t.getLongValue();
          }
        } else if (fieldType instanceof CharType) {
          IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
          if (t != null) {
            value = t.getIntValue();
          }
        } else if (fieldType instanceof BooleanType) {
          IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
          if (t != null) {
            value = (t.getIntValue() != 0);
          }
        } else if (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) {
          IntegerConstantValueTag t = (IntegerConstantValueTag) f.getTag(IntegerConstantValueTag.NAME);
          if (t != null) {
            value = t.getIntValue();
          }
        }

        // if there was a constant value tag we have its value now
        if (value != null) {
          debug("TAGGED value found for field: " + combined);
          primTypeFieldValueToUse.put(combined, value);

          // continue with next field
          continue;
        }

        // see if the field was never assigned in which case it gets default values
        ArrayList<Value> values = fieldToValues.get(combined);
        if (values == null) {
          // no value list found is good

          // add default value to primTypeFieldValueToUse hashmap

          if (fieldType instanceof DoubleType) {
            value = 0.0d;
          } else if (fieldType instanceof FloatType) {
            value = 0.0f;
          } else if (fieldType instanceof LongType) {
            value = 0L;
          } else if (fieldType instanceof BooleanType) {
            value = false;
          } else if (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType
              || fieldType instanceof CharType) {
            value = 0;
          } else {
            throw new DecompilationException("Unknown primitive type...please report to developer");
          }

          primTypeFieldValueToUse.put(combined, value);
          debug("DEFAULT value for field: " + combined);

          // continue with next field
          continue;
        }

        // haven't got a tag with value and havent use default since SOME method did define the field atleast once

        // there was some value assigned!!!!!!!!!
        debug("CHECKING USER ASSIGNED VALUES FOR: " + combined);

        // check if they are all constants and that too the same constant
        NumericConstant tempConstant = null;
        for (Value val : values) {
          if (!(val instanceof NumericConstant)) {
            tempConstant = null;
            debug("Not numeric constant hence giving up");
            break;
          }

          if (tempConstant == null) {
            tempConstant = (NumericConstant) val;
          } else {
            // check that this value is the same as previous
            if (!tempConstant.equals(val)) {
              tempConstant = null;
              break;
            }
          }
        }
        if (tempConstant == null) {
          // continue with next field cant do anything about this one
          continue;
        }

        // agreed on a unique constant value

        /*
         * Since these are fields are we are doing CONTEXT INSENSITIVE WE need to make sure that the agreed unique constant
         * value is the default value
         *
         * I KNOW IT SUCKS BUT HEY WHAT CAN I DO!!!
         */

        if (tempConstant instanceof LongConstant) {
          long tempVal = ((LongConstant) tempConstant).value;
          if (Long.compare(tempVal, 0L) == 0) {
            primTypeFieldValueToUse.put(combined, tempVal);
          } else {
            debug("Not assigning the agreed value since that is not the default value for " + combined);
          }
        } else if (tempConstant instanceof DoubleConstant) {
          double tempVal = ((DoubleConstant) tempConstant).value;
          if (Double.compare(tempVal, 0.0d) == 0) {
            primTypeFieldValueToUse.put(combined, tempVal);
          } else {
            debug("Not assigning the agreed value since that is not the default value for " + combined);
          }

        } else if (tempConstant instanceof FloatConstant) {
          float tempVal = ((FloatConstant) tempConstant).value;
          if (Float.compare(tempVal, 0.0f) == 0) {
            primTypeFieldValueToUse.put(combined, tempVal);
          } else {
            debug("Not assigning the agreed value since that is not the default value for " + combined);
          }

        } else if (tempConstant instanceof IntConstant) {
          int tempVal = ((IntConstant) tempConstant).value;
          if (Integer.compare(tempVal, 0) == 0) {
            SootField tempField = classNameFieldNameToSootFieldMapping.get(combined);
            if (tempField.getType() instanceof BooleanType) {
              primTypeFieldValueToUse.put(combined, false);
              // System.out.println("puttingvalue false for"+combined);
            } else {
              primTypeFieldValueToUse.put(combined, tempVal);
              // System.out.println("puttingvalue 0 for"+combined);
            }
          } else {
            debug("Not assigning the agreed value since that is not the default value for " + combined);
          }

        } else {
          throw new DecompilationException("Un handled Numberic Constant....report to programmer");
        }
      }
    } // all classes
  }

  /*
   * Go through all the methods in the application and make a mapping of className+methodName ---> values assigned There can
   * obviously be more than one value assigned to each field
   */
  private void computeFieldToValuesAssignedList() {
    // go through all the classes
    for (SootClass s : appClasses) {
      debug("\ncomputeMethodSummaries", "Processing class " + s.getName());

      // go though all the methods
      for (Iterator<SootMethod> methodIt = s.methodIterator(); methodIt.hasNext();) {
        SootMethod m = methodIt.next();
        if (!m.hasActiveBody()) {
          continue;
        }

        DavaBody body = (DavaBody) m.getActiveBody();
        ASTNode AST = (ASTNode) body.getUnits().getFirst();

        // find all definitions in the program
        AllDefinitionsFinder defFinder = new AllDefinitionsFinder();
        AST.apply(defFinder);

        // go through each definition
        for (DefinitionStmt stmt : defFinder.getAllDefs()) {
          // debug("DefinitionStmt")
          Value left = stmt.getLeftOp();

          /*
           * Only care if we have fieldRef on the left
           */
          if (!(left instanceof FieldRef)) {
            continue;
          }

          // we know definition is to a field
          debug("computeMethodSummaries method: " + m.getName(), "Field ref is: " + left);
          // Information we want to store is class of field and name of field and the right op

          FieldRef ref = (FieldRef) left;
          SootField field = ref.getField();

          /*
           * Only care about fields with primtype
           */
          if (!(field.getType() instanceof PrimType)) {
            continue;
          }

          String fieldName = field.getName();
          String declaringClass = field.getDeclaringClass().getName();

          debug("\tField Name: " + fieldName);
          debug("\tField DeclaringClass: " + declaringClass);

          // get the valueList for this class+field combo
          String combined = declaringClass + combiner + fieldName;
          ArrayList<Value> valueList = fieldToValues.get(combined);
          if (valueList == null) {
            // no value of this field was yet assigned
            valueList = new ArrayList<>();
            fieldToValues.put(combined, valueList);
          }

          valueList.add(stmt.getRightOp());
        } // going through all the definitions
      } // going through methods of class s
    }
  }

  public void printConstantValueFields() {
    System.out.println("\n\n Printing Constant Value Fields (method: printConstantValueFields)");

    for (String combined : primTypeFieldValueToUse.keySet()) {
      int temp = combined.indexOf(combiner, 0);
      if (temp > 0) {
        System.out.println("Class: " + combined.substring(0, temp) + " Field: "
            + combined.substring(temp + combiner.length()) + " Value: " + primTypeFieldValueToUse.get(combined));
      }
    }
  }

  public void debug(String methodName, String debug) {
    if (DEBUG) {
      System.out.println(methodName + "    DEBUG: " + debug);
    }
  }

  public void debug(String debug) {
    if (DEBUG) {
      System.out.println("DEBUG: " + debug);
    }
  }
}
