package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.Unit;
import soot.baf.Baf;
import soot.baf.InstSwitch;
import soot.baf.JSRInst;
import soot.util.Switch;

public class BJSRInst extends AbstractBranchInst implements JSRInst {
  public BJSRInst(Unit target) {
    super(Baf.v().newInstBox(target));
  }

  public Object clone() {
    return new BJSRInst(getTarget());
  }

  public int getInMachineCount() {
    return 0;
  }

  public boolean branches() {
    return true;
  }

  public int getInCount() {
    return 0;
  }

  public int getOutCount() {
    return 1;
  }

  public int getOutMachineCount() {
    return 1;
  }

  public String getName() {
    return "jsr";
  }

  public void apply(Switch sw) {
    ((InstSwitch) sw).caseJSRInst(this);
  }

  public boolean fallsThrough() {
    return false;
  }
}