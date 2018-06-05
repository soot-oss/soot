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

import soot.Value;
import soot.grimp.Grimp;
import soot.jimple.internal.JAssignStmt;

public class GAssignStmt extends JAssignStmt {
  public GAssignStmt(Value variable, Value rvalue) {
    super(Grimp.v().newVariableBox(variable), Grimp.v().newRValueBox(rvalue));
  }

  public Object clone() {
    return new GAssignStmt(Grimp.cloneIfNecessary(getLeftOp()), Grimp.cloneIfNecessary(getRightOp()));
  }
}
