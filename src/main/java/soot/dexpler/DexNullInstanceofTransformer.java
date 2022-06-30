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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;

/**
 * Transformer that swaps
 * 
 * a = 0 instanceof _class_;
 * 
 * with
 * 
 * a = false
 * 
 * @author Steven Arzt
 *
 */
public class DexNullInstanceofTransformer extends BodyTransformer {

  public static DexNullInstanceofTransformer v() {
    return new DexNullInstanceofTransformer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Iterator<Unit> unitIt = b.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (u instanceof AssignStmt) {
        AssignStmt assignStmt = (AssignStmt) u;
        if (assignStmt.getRightOp() instanceof InstanceOfExpr) {
          InstanceOfExpr iof = (InstanceOfExpr) assignStmt.getRightOp();

          // If the operand of the "instanceof" expression is null or
          // the zero constant, we replace the whole operation with
          // its outcome "false"
          if (iof.getOp() == NullConstant.v()) {
            assignStmt.setRightOp(IntConstant.v(0));
          }
          if (iof.getOp() instanceof IntConstant && ((IntConstant) iof.getOp()).value == 0) {
            assignStmt.setRightOp(IntConstant.v(0));
          }
        }
      }
    }
  }

}
