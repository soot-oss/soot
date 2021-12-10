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

import soot.UnitPrinter;
import soot.jimple.BreakpointStmt;
import soot.jimple.Jimple;
import soot.jimple.StmtSwitch;
import soot.util.Switch;

public class JBreakpointStmt extends AbstractStmt implements BreakpointStmt {

  public JBreakpointStmt() {
  }

  @Override
  public String toString() {
    return Jimple.BREAKPOINT;
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(Jimple.BREAKPOINT);
  }

  @Override
  public void apply(Switch sw) {
    ((StmtSwitch) sw).caseBreakpointStmt(this);
  }

  @Override
  public Object clone() {
    return new JBreakpointStmt();
  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }
}
