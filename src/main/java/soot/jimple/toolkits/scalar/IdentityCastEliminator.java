package soot.jimple.toolkits.scalar;

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
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Type;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;

/**
 * Transformer that removes unnecessary identity casts such as
 * 
 * $i3 = (int) $i3
 * 
 * when $i3 is already of type "int".
 * 
 * @author Steven Arzt
 *
 */
public class IdentityCastEliminator extends BodyTransformer {

  public IdentityCastEliminator(Singletons.Global g) {
  }

  public static IdentityCastEliminator v() {
    return G.v().soot_jimple_toolkits_scalar_IdentityCastEliminator();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Iterator<Unit> unitIt = b.getUnits().iterator(); unitIt.hasNext();) {
      Unit curUnit = unitIt.next();
      if (curUnit instanceof AssignStmt) {
        AssignStmt assignStmt = (AssignStmt) curUnit;
        if (assignStmt.getLeftOp() instanceof Local && assignStmt.getRightOp() instanceof CastExpr) {
          CastExpr ce = (CastExpr) assignStmt.getRightOp();

          Type orgType = ce.getOp().getType();
          Type newType = ce.getCastType();

          // If this a cast such as a = (X) a, we can remove the whole line.
          // Otherwise, if only the types match, we can replace the typecast
          // with a normal assignment.
          if (orgType == newType) {
            if (assignStmt.getLeftOp() == ce.getOp()) {
              unitIt.remove();
            } else {
              assignStmt.setRightOp(ce.getOp());
            }
          }
        }
      }
    }
  }

}
