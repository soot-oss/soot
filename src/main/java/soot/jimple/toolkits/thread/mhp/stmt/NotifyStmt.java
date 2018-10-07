
package soot.jimple.toolkits.thread.mhp.stmt;

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

import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class NotifyStmt extends JPegStmt

{

  public NotifyStmt(String obj, String ca, Unit un, UnitGraph ug, SootMethod sm) {
    this.object = obj;
    this.name = "notify";
    this.caller = ca;
    this.unit = un;
    this.unitGraph = ug;
    this.sootMethod = sm;
  }

}
