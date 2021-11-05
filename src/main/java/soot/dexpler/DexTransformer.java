// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.NullType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.Stmt;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

public abstract class DexTransformer extends BodyTransformer {

  /**
   * Collect definitions of l in body including the definitions of aliases of l.
   * 
   * In this context an alias is a local that propagates its value to l.
   * 
   * @param l
   *          the local whose definitions are to collect
   * @param localDefs
   *          the LocalDefs object
   * @param body
   *          the body that contains the local
   */
  protected List<Unit> collectDefinitionsWithAliases(Local l, LocalDefs localDefs, LocalUses localUses, Body body) {
    Set<Local> seenLocals = new HashSet<Local>();
    List<Local> newLocals = new ArrayList<Local>();
    List<Unit> defs = new ArrayList<Unit>();
    newLocals.add(l);
    seenLocals.add(l);

    while (!newLocals.isEmpty()) {
      Local local = newLocals.remove(0);
      for (Unit u : collectDefinitions(local, localDefs)) {
        if (u instanceof AssignStmt) {
          Value r = ((AssignStmt) u).getRightOp();
          if (r instanceof Local && seenLocals.add((Local) r)) {
            newLocals.add((Local) r);
          }
        }
        defs.add(u);
        //
        List<UnitValueBoxPair> usesOf = localUses.getUsesOf(u);
        for (UnitValueBoxPair pair : usesOf) {
          Unit unit = pair.getUnit();
          if (unit instanceof AssignStmt) {
            AssignStmt assignStmt = ((AssignStmt) unit);
            Value right = assignStmt.getRightOp();
            Value left = assignStmt.getLeftOp();
            if (right == local && left instanceof Local && seenLocals.add((Local) left)) {
              newLocals.add((Local) left);
            }
          }
        }
        //
      }
    }
    return defs;
  }

  /**
   * Convenience method that collects all definitions of l.
   * 
   * @param l
   *          the local whose definitions are to collect
   * @param localDefs
   *          the LocalDefs object
   * @param body
   *          the body that contains the local
   */
  private List<Unit> collectDefinitions(Local l, LocalDefs localDefs) {
    return localDefs.getDefsOf(l);
  }

  protected Type findArrayType(LocalDefs localDefs, Stmt arrayStmt, int depth, Set<Unit> alreadyVisitedDefs) {
    ArrayRef aRef = null;
    if (arrayStmt.containsArrayRef()) {
      aRef = arrayStmt.getArrayRef();
    }
    Local aBase = null;

    if (null == aRef) {
      if (arrayStmt instanceof AssignStmt) {
        AssignStmt stmt = (AssignStmt) arrayStmt;
        aBase = (Local) stmt.getRightOp();
      } else {
        throw new RuntimeException("ERROR: not an assign statement: " + arrayStmt);
      }
    } else {
      aBase = (Local) aRef.getBase();
    }

    List<Unit> defsOfaBaseList = localDefs.getDefsOfAt(aBase, arrayStmt);
    if (defsOfaBaseList == null || defsOfaBaseList.isEmpty()) {
      throw new RuntimeException("ERROR: no def statement found for array base local " + arrayStmt);
    }

    // We should find an answer only by processing the first item of the
    // list
    Type aType = null;
    int nullDefCount = 0;
    for (Unit baseDef : defsOfaBaseList) {
      if (alreadyVisitedDefs.contains(baseDef)) {
        continue;
      }
      Set<Unit> newVisitedDefs = new HashSet<Unit>(alreadyVisitedDefs);
      newVisitedDefs.add(baseDef);

      // baseDef is either an assignment statement or an identity
      // statement
      if (baseDef instanceof AssignStmt) {
        AssignStmt stmt = (AssignStmt) baseDef;
        Value r = stmt.getRightOp();
        if (r instanceof FieldRef) {
          Type t = ((FieldRef) r).getFieldRef().type();
          if (t instanceof ArrayType) {
            ArrayType at = (ArrayType) t;
            t = at.getArrayElementType();
          }
          if (depth == 0) {
            aType = t;
            break;
          } else {
            return t;
          }
        } else if (r instanceof ArrayRef) {
          ArrayRef ar = (ArrayRef) r;
          if (ar.getType().toString().equals(".unknown") || ar.getType().toString().equals("unknown")) { // ||
            // ar.getType())
            // {
            Type t = findArrayType(localDefs, stmt, ++depth, newVisitedDefs); // TODO: which type should be
            // returned?
            if (t instanceof ArrayType) {
              ArrayType at = (ArrayType) t;
              t = at.getArrayElementType();
            }
            if (depth == 0) {
              aType = t;
              break;
            } else {
              return t;
            }
          } else {
            ArrayType at = (ArrayType) stmt.getRightOp().getType();
            Type t = at.getArrayElementType();
            if (depth == 0) {
              aType = t;
              break;
            } else {
              return t;
            }
          }
        } else if (r instanceof NewExpr) {
          NewExpr expr = (NewExpr) r;
          Type t = expr.getBaseType();
          if (depth == 0) {
            aType = t;
            break;
          } else {
            return t;
          }
        } else if (r instanceof NewArrayExpr) {
          NewArrayExpr expr = (NewArrayExpr) r;
          Type t = expr.getBaseType();
          if (depth == 0) {
            aType = t;
            break;
          } else {
            return t;
          }
        } else if (r instanceof CastExpr) {
          Type t = (((CastExpr) r).getCastType());
          if (t instanceof ArrayType) {
            ArrayType at = (ArrayType) t;
            t = at.getArrayElementType();
          }
          if (depth == 0) {
            aType = t;
            break;
          } else {
            return t;
          }
        } else if (r instanceof InvokeExpr) {
          Type t = ((InvokeExpr) r).getMethodRef().returnType();
          if (t instanceof ArrayType) {
            ArrayType at = (ArrayType) t;
            t = at.getArrayElementType();
          }
          if (depth == 0) {
            aType = t;
            break;
          } else {
            return t;
          }
          // introduces alias. We look whether there is any type
          // information associated with the alias.
        } else if (r instanceof Local) {
          Type t = findArrayType(localDefs, stmt, ++depth, newVisitedDefs);
          if (depth == 0) {
            aType = t;
            // break;
          } else {
            // return t;
            aType = t;
          }
        } else if (r instanceof Constant) {
          // If the right side is a null constant, we might have a
          // case of broken code, e.g.,
          // a = null;
          // a[12] = 42;
          nullDefCount++;
        } else {
          throw new RuntimeException(String.format("ERROR: def statement not possible! Statement: %s, right side: %s",
              stmt.toString(), r.getClass().getName()));
        }

      } else if (baseDef instanceof IdentityStmt) {
        IdentityStmt stmt = (IdentityStmt) baseDef;
        ArrayType at = (ArrayType) stmt.getRightOp().getType();
        Type t = at.getArrayElementType();
        if (depth == 0) {
          aType = t;
          break;
        } else {
          return t;
        }
      } else {
        throw new RuntimeException("ERROR: base local def must be AssignStmt or IdentityStmt! " + baseDef);
      }

      if (aType != null) {
        break;
      }
    } // loop

    if (depth == 0 && aType == null) {
      if (nullDefCount == defsOfaBaseList.size()) {
        return NullType.v();
      } else {
        throw new RuntimeException("ERROR: could not find type of array from statement '" + arrayStmt + "'");
      }
    } else {
      return aType;
    }
  }

}
