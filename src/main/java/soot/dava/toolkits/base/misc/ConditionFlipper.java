package soot.dava.toolkits.base.misc;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import soot.grimp.internal.GEqExpr;
import soot.grimp.internal.GGeExpr;
import soot.grimp.internal.GGtExpr;
import soot.grimp.internal.GLeExpr;
import soot.grimp.internal.GLtExpr;
import soot.grimp.internal.GNeExpr;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;

public class ConditionFlipper {
  public static ConditionExpr flip(ConditionExpr ce) {
    if (ce instanceof EqExpr) {
      return new GNeExpr(ce.getOp1(), ce.getOp2());
    }

    if (ce instanceof NeExpr) {
      return new GEqExpr(ce.getOp1(), ce.getOp2());
    }

    if (ce instanceof GtExpr) {
      return new GLeExpr(ce.getOp1(), ce.getOp2());
    }

    if (ce instanceof LtExpr) {
      return new GGeExpr(ce.getOp1(), ce.getOp2());
    }

    if (ce instanceof GeExpr) {
      return new GLtExpr(ce.getOp1(), ce.getOp2());
    }

    if (ce instanceof LeExpr) {
      return new GGtExpr(ce.getOp1(), ce.getOp2());
    }

    return null;
  }
}
