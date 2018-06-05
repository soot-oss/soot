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

import soot.RefType;
import soot.Unit;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.JimpleToBafContext;

public class JNewExpr extends AbstractNewExpr implements ConvertToBaf {
  public JNewExpr(RefType type) {
    this.type = type;
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newNewInst(getBaseType());
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }

  public Object clone() {
    return new JNewExpr(type);
  }
}
