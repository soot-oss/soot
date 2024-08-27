package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.dexpler.instructions.FillArrayDataInstruction;
import soot.dexpler.typing.UntypedConstant;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.scalar.LocalDefs;

/**
 * If Dalvik bytecode can contain <code>fill-array-data</code> instructions that can fill an array with data elements we only
 * know the element size of.
 * 
 * Therefore when processing such instructions in {@link FillArrayDataInstruction} we don't know the exact type of the data
 * that is loaded. Because of (conditional) branches in the code, identifying the type is not always possible at that stage.
 * Instead {@link UntypedConstant} constants are used. These constants are processed by this transformer and get their final
 * type.
 * 
 *
 * @author Jan Peter Stotz
 *
 */
public class DexFillArrayDataTransformer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(DexFillArrayDataTransformer.class);

  private static final int MAX_RECURSION_DEPTH = 5;

  public static DexFillArrayDataTransformer v() {
    return new DexFillArrayDataTransformer();
  }

  protected void internalTransform(final Body body, String phaseName, Map<String, String> options) {
    final ExceptionalUnitGraph g = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, DalvikThrowAnalysis.v());
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(g);

    for (Iterator<Unit> unitIt = body.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (!(u instanceof AssignStmt)) {
        continue;
      }
      AssignStmt ass = (AssignStmt) u;
      Value rightOp = ass.getRightOp();
      if (rightOp instanceof UntypedConstant) {
        Value left = ass.getLeftOp();
        if (left instanceof ArrayRef) {
          ArrayRef leftArray = (ArrayRef) left;

          Local l = (Local) leftArray.getBase();
          List<Type> arrayTypes = new LinkedList<>();
          checkArrayDefinitions(l, ass, defs, arrayTypes, MAX_RECURSION_DEPTH);
          if (arrayTypes.isEmpty()) {
            throw new InternalError("Failed to determine the array type ");
          }
          if (arrayTypes.size() > 1) {
            arrayTypes = arrayTypes.stream().distinct().collect(Collectors.toList());
            if (arrayTypes.size() > 1) {
              logger.warn("Found multiple possible array types, using first ignoreing the others: {}", arrayTypes);
            }
          }

          // We found the array type, now convert the untyped constant value to it's final type
          Type elementType = arrayTypes.get(0);
          Value constant = ass.getRightOp();
          UntypedConstant untyped = (UntypedConstant) constant;
          ass.setRightOp(untyped.defineType(elementType));
        }
      }
    }
  }

  /**
   * Check the all available definitions of the current array to detect the array type and thus the type of the data loaded
   * by the array-fill-data instruction.
   * 
   * @param l
   *          local the array we are interested in is saved in
   * @param u
   *          unit we start our search
   * @param defs
   * @param arrayTypes
   *          result list containing the discovered array type(s)
   * @param maxDepth
   */
  private void checkArrayDefinitions(Local l, Unit u, LocalDefs defs, List<Type> arrayTypes, int maxDepth) {
    if (maxDepth <= 0) {
      // Avoid infinite recursion
      logger.warn("Recursion depth limit reached - aborting");
      return;
    }
    List<Unit> assDefs = defs.getDefsOfAt(l, u);
    for (Unit d : assDefs) {
      if (d instanceof AssignStmt) {
        AssignStmt arrayAssign = (AssignStmt) d;
        Value source = arrayAssign.getRightOp();
        if (source instanceof NewArrayExpr) {
          // array is assigned from a newly created array
          NewArrayExpr newArray = (NewArrayExpr) source;
          arrayTypes.add(newArray.getBaseType());
        } else if (source instanceof InvokeExpr) {
          // array is assigned from the return value of a function
          InvokeExpr invExpr = (InvokeExpr) source;
          Type aType = invExpr.getMethodRef().getReturnType();
          if (!(aType instanceof ArrayType)) {
            throw new InternalError("Failed to identify the array type. The identified method invocation "
                + "does not return an array type. Invocation: " + invExpr.getMethodRef());
          }
          arrayTypes.add(((ArrayType) aType).getArrayElementType());
        } else if (source instanceof Local) {
          // our array is defined by an assignment from another array => check the definition of that other array.
          Local newLocal = (Local) source; // local of the "other array"
          checkArrayDefinitions(newLocal, d, defs, arrayTypes, maxDepth - 1);
        } else {
          throw new InternalError("Unsupported array definition statement: " + d);
        }
      }
    }

  }
}
