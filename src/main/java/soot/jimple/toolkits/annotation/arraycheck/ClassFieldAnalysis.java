package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.toolkits.scalar.LocalDefs;

public class ClassFieldAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(ClassFieldAnalysis.class);

  public ClassFieldAnalysis(Singletons.Global g) {
  }

  public static ClassFieldAnalysis v() {
    return G.v().soot_jimple_toolkits_annotation_arraycheck_ClassFieldAnalysis();
  }

  private final boolean final_in = true;
  private final boolean private_in = true;

  /*
   * A map hold class object to other information
   *
   * SootClass --> FieldInfoTable
   */

  private final Map<SootClass, Hashtable<SootField, IntValueContainer>> classToFieldInfoMap
      = new HashMap<SootClass, Hashtable<SootField, IntValueContainer>>();

  protected void internalTransform(SootClass c) {
    if (classToFieldInfoMap.containsKey(c)) {
      return;
    }

    /* Summerize class information here. */
    Date start = new Date();
    if (Options.v().verbose()) {
      logger.debug("[] ClassFieldAnalysis started on : " + start + " for " + c.getPackageName() + c.getName());
    }

    Hashtable<SootField, IntValueContainer> fieldInfoTable = new Hashtable<SootField, IntValueContainer>();
    classToFieldInfoMap.put(c, fieldInfoTable);

    /*
     * Who is the candidate for analysis? Int, Array, field. Also it should be PRIVATE now.
     */
    HashSet<SootField> candidSet = new HashSet<SootField>();

    int arrayTypeFieldNum = 0;

    Iterator<SootField> fieldIt = c.getFields().iterator();
    while (fieldIt.hasNext()) {
      SootField field = fieldIt.next();
      int modifiers = field.getModifiers();

      Type type = field.getType();
      if (type instanceof ArrayType) {
        if ((final_in && ((modifiers & Modifier.FINAL) != 0)) || (private_in && ((modifiers & Modifier.PRIVATE) != 0))) {
          candidSet.add(field);
          arrayTypeFieldNum++;
        }
      }
    }

    if (arrayTypeFieldNum == 0) {
      if (Options.v().verbose()) {
        logger.debug("[] ClassFieldAnalysis finished with nothing");
      }
      return;
    }

    /* For FINAL field, it only needs to scan the <clinit> and <init> methods. */

    /*
     * For PRIVATE field, <clinit> is scanned to make sure that it is always assigned a value before other uses. And no other
     * assignment in other methods.
     */

    /*
     * The fastest way to determine the value of one field may get. Scan all method to get all definitions, and summerize the
     * final value. For PRIVATE STATIC field, if it is not always assigned value, it may count null pointer exception before
     * array exception
     */

    Iterator<SootMethod> methodIt = c.methodIterator();
    while (methodIt.hasNext()) {
      ScanMethod(methodIt.next(), candidSet, fieldInfoTable);
    }

    Date finish = new Date();
    if (Options.v().verbose()) {
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime % 60000) / 1000;
      logger.debug("[] ClassFieldAnalysis finished normally. " + "It took " + mins + " mins and " + secs + " secs.");
    }
  }

  public Object getFieldInfo(SootField field) {
    SootClass c = field.getDeclaringClass();

    Map<SootField, IntValueContainer> fieldInfoTable = classToFieldInfoMap.get(c);

    if (fieldInfoTable == null) {
      internalTransform(c);
      fieldInfoTable = classToFieldInfoMap.get(c);
    }

    return fieldInfoTable.get(field);
  }

  /*
   * method, to be scanned candidates, the candidate set of fields, fields with value TOP are moved out of the set.
   * fieldinfo, keep the field -> value.
   */

  public void ScanMethod(SootMethod method, Set<SootField> candidates, Hashtable<SootField, IntValueContainer> fieldinfo) {
    if (!method.isConcrete()) {
      return;
    }

    Body body = method.retrieveActiveBody();

    if (body == null) {
      return;
    }

    /* no array locals, then definitely it has no array type field references. */
    {
      boolean hasArrayLocal = false;

      Collection<Local> locals = body.getLocals();

      Iterator<Local> localIt = locals.iterator();
      while (localIt.hasNext()) {
        Local local = localIt.next();
        Type type = local.getType();

        if (type instanceof ArrayType) {
          hasArrayLocal = true;
          break;
        }
      }

      if (!hasArrayLocal) {
        return;
      }
    }

    /* only take care of the first dimension of array size */
    /* check the assignment of fields. */

    /* Linearly scan the method body, if it has field references in candidate set. */
    /*
     * Only a.f = ... needs consideration. this.f, or other.f are treated as same because we summerize the field as a class's
     * field.
     */

    HashMap<Stmt, SootField> stmtfield = new HashMap<Stmt, SootField>();

    {
      Iterator<Unit> unitIt = body.getUnits().iterator();
      while (unitIt.hasNext()) {
        Stmt stmt = (Stmt) unitIt.next();
        if (stmt.containsFieldRef()) {
          Value leftOp = ((AssignStmt) stmt).getLeftOp();
          if (leftOp instanceof FieldRef) {
            FieldRef fref = (FieldRef) leftOp;
            SootField field = fref.getField();

            if (candidates.contains(field)) {
              stmtfield.put(stmt, field);
            }
          }
        }
      }

      if (stmtfield.size() == 0) {
        return;
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[] ScanMethod for field started.");
    }

    /* build D/U web, find the value of each candidate */
    {
      LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(body);

      Set<Map.Entry<Stmt, SootField>> entries = stmtfield.entrySet();

      Iterator<Map.Entry<Stmt, SootField>> entryIt = entries.iterator();
      while (entryIt.hasNext()) {
        Map.Entry<Stmt, SootField> entry = entryIt.next();
        Stmt where = entry.getKey();
        SootField which = entry.getValue();

        IntValueContainer length = new IntValueContainer();

        // take out the right side of assign stmt
        Value rightOp = ((AssignStmt) where).getRightOp();

        if (rightOp instanceof Local) {
          // tracing down the defs of right side local.
          Local local = (Local) rightOp;
          DefinitionStmt usestmt = (DefinitionStmt) where;

          while (length.isBottom()) {
            List<Unit> defs = localDefs.getDefsOfAt(local, usestmt);
            if (defs.size() == 1) {
              usestmt = (DefinitionStmt) defs.get(0);

              if (Options.v().debug()) {
                logger.debug("        " + usestmt);
              }

              Value tmp_rhs = usestmt.getRightOp();
              if ((tmp_rhs instanceof NewArrayExpr) || (tmp_rhs instanceof NewMultiArrayExpr)) {
                Value size;

                if (tmp_rhs instanceof NewArrayExpr) {
                  size = ((NewArrayExpr) tmp_rhs).getSize();
                } else {
                  size = ((NewMultiArrayExpr) tmp_rhs).getSize(0);
                }

                if (size instanceof IntConstant) {
                  length.setValue(((IntConstant) size).value);
                } else if (size instanceof Local) {
                  local = (Local) size;

                  // defs = localDefs.getDefsOfAt((Local)size, (Unit)usestmt);

                  continue;
                } else {
                  length.setTop();
                }
              } else if (tmp_rhs instanceof IntConstant) {
                length.setValue(((IntConstant) tmp_rhs).value);
              } else if (tmp_rhs instanceof Local) {
                // defs = localDefs.getDefsOfAt((Local)tmp_rhs, usestmt);
                local = (Local) tmp_rhs;

                continue;
              } else {
                length.setTop();
              }
            } else {
              length.setTop();
            }
          }
        } else {
          /* it could be null */
          continue;
        }

        IntValueContainer oldv = fieldinfo.get(which);

        /* the length is top, set the field to top */
        if (length.isTop()) {
          if (oldv == null) {
            fieldinfo.put(which, length.dup());
          } else {
            oldv.setTop();
          }

          /* remove from the candidate set. */
          candidates.remove(which);
        } else if (length.isInteger()) {
          if (oldv == null) {
            fieldinfo.put(which, length.dup());
          } else {
            if (oldv.isInteger() && oldv.getValue() != length.getValue()) {
              oldv.setTop();
              candidates.remove(which);
            }
          }
        }
      }
    }

    if (Options.v().verbose()) {
      logger.debug("[] ScanMethod finished.");
    }
  }
}
