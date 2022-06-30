package soot.grimp.internal;

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

import java.util.ArrayList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.IntConstant;
import soot.jimple.internal.JLookupSwitchStmt;

public class GLookupSwitchStmt extends JLookupSwitchStmt {

  public GLookupSwitchStmt(Value key, List<IntConstant> lookupValues, List<? extends Unit> targets, Unit defaultTarget) {
    super(Grimp.v().newExprBox(key), lookupValues, getTargetBoxesArray(targets, Grimp.v()::newStmtBox),
        Grimp.v().newStmtBox(defaultTarget));
  }

  @Override
  public Object clone() {
    List<IntConstant> clonedLookupValues = new ArrayList<IntConstant>(lookupValues.size());
    for (IntConstant c : lookupValues) {
      clonedLookupValues.add(IntConstant.v(c.value));
    }
    return new GLookupSwitchStmt(Grimp.cloneIfNecessary(getKey()), clonedLookupValues, getTargets(), getDefaultTarget());
  }
}
