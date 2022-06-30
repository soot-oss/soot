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

import soot.AbstractTrap;
import soot.SootClass;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Jimple;

public class JTrap extends AbstractTrap {

  public JTrap(SootClass exception, Unit beginStmt, Unit endStmt, Unit handlerStmt) {
    super(exception, Jimple.v().newStmtBox(beginStmt), Jimple.v().newStmtBox(endStmt), Jimple.v().newStmtBox(handlerStmt));
  }

  public JTrap(SootClass exception, UnitBox beginStmt, UnitBox endStmt, UnitBox handlerStmt) {
    super(exception, beginStmt, endStmt, handlerStmt);
  }

  @Override
  public Object clone() {
    return new JTrap(exception, getBeginUnit(), getEndUnit(), getHandlerUnit());
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder("Trap :");
    buf.append("\nbegin  : ").append(getBeginUnit());
    buf.append("\nend    : ").append(getEndUnit());
    buf.append("\nhandler: ").append(getHandlerUnit());
    return buf.toString();
  }
}
