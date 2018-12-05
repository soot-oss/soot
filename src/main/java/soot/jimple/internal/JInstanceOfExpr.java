package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import java.util.List;

import soot.Type;
import soot.Unit;
import soot.Value;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;

public class JInstanceOfExpr extends AbstractInstanceOfExpr implements ConvertToBaf {
  public JInstanceOfExpr(Value op, Type checkType) {
    super(Jimple.v().newImmediateBox(op), checkType);
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) (getOp())).convertToBaf(context, out);
    Unit u = Baf.v().newInstanceOfInst(getCheckType());
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }

  public Object clone() {
    return new JInstanceOfExpr(Jimple.cloneIfNecessary(getOp()), checkType);
  }

}
