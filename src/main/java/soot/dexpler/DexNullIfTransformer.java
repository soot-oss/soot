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
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;

/**
 * Transformer that swaps
 * 
 * if null == 0 goto x
 * 
 * with
 * 
 * if null == null goto x
 * 
 * In dex they are the same thing. If we do not do that, soot might assume that the condition is not met and optimize the
 * wrong branch away.
 * 
 * @author Marc Miltenberger
 *
 */
public class DexNullIfTransformer extends BodyTransformer {

  public static DexNullIfTransformer v() {
    return new DexNullIfTransformer();
  }

  private boolean hasModifiedBody;

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    NullConstant nullC = NullConstant.v();
    for (Iterator<Unit> unitIt = b.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      if (u instanceof IfStmt) {
        IfStmt ifStmt = (IfStmt) u;
        Value o = ifStmt.getCondition();
        if (o instanceof BinopExpr) {
          BinopExpr bop = (BinopExpr) o;
          Value l = bop.getOp1();
          Value r = bop.getOp2();
          if (isNull(l) && isNull(r)) {
            if (l instanceof NullConstant || r instanceof NullConstant) {
              bop.setOp1(nullC);
              bop.setOp2(nullC);
              hasModifiedBody = true;
            }
          }
        }
      }
    }
  }

  private boolean isNull(Value l) {
    if ((l instanceof NullConstant) || (l instanceof IntConstant && ((IntConstant) l).value == 0)) {
      return true;
    }
    return false;
  }

  public boolean hasModifiedBody() {
    return hasModifiedBody;
  }

}
