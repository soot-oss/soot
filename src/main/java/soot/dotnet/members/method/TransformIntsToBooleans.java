package soot.dotnet.members.method;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.BooleanConstant;
import soot.BooleanType;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.IntConstant;

public class TransformIntsToBooleans extends BodyTransformer {

  public TransformIntsToBooleans(Singletons.Global g) {
  }

  public static TransformIntsToBooleans v() {
    return G.v().soot_dotnet_members_method_TransformIntsToBooleans();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> opts) {
    for (Unit u : b.getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt assign = (AssignStmt) u;
        Value rop = assign.getRightOp();
        if (rop instanceof CastExpr) {
          CastExpr cast = (CastExpr) rop;
          if (cast.getType() instanceof BooleanType) {
            if (cast.getOp() instanceof IntConstant) {
              IntConstant ic = (IntConstant) cast.getOp();
              assign.setRightOp(BooleanConstant.v(ic.value == 1));
            }
          }
        }
      }
    }
  }

}
