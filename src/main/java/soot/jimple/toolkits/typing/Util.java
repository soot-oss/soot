package soot.jimple.toolkits.typing;

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

import soot.Body;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.util.Chain;

public class Util {

  /**
   * A new "normal" statement cannot be inserted in the middle of special "identity statements" (a = @parameter or b = @this
   * in Jimple).
   *
   * This method returns the last "identity statement" of the method.
   *
   * @param b
   * @param s
   * @return
   */
  public static Unit findLastIdentityUnit(Body b, Stmt s) {
    final Chain<Unit> units = b.getUnits();
    Unit u2 = s;
    for (Unit u1 = u2; u1 instanceof IdentityStmt;) {
      u2 = u1;
      u1 = units.getSuccOf(u1);
    }
    return u2;
  }

  /**
   * Returns the first statement after all the "identity statements".
   *
   * @param b
   * @param s
   * @return
   */
  public static Unit findFirstNonIdentityUnit(Body b, Stmt s) {
    final Chain<Unit> units = b.getUnits();
    Unit u1 = s;
    while (u1 instanceof IdentityStmt) {
      u1 = units.getSuccOf(u1);
    }
    return u1;
  }

  private Util() {
  }
}
