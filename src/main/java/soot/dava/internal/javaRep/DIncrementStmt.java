package soot.dava.internal.javaRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.UnitPrinter;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.internal.GAssignStmt;

public class DIncrementStmt extends GAssignStmt {
  public DIncrementStmt(Value variable, Value rvalue) {
    super(variable, rvalue);
  }

  public Object clone() {
    return new DIncrementStmt(Grimp.cloneIfNecessary(getLeftOp()), Grimp.cloneIfNecessary(getRightOp()));
  }

  public String toString() {
    return getLeftOpBox().getValue().toString() + "++";
  }

  public void toString(UnitPrinter up) {
    getLeftOpBox().toString(up);
    up.literal("++");
  }

}
