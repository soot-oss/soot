package soot.coffi;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 Clark Verbrugge
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
import soot.util.Switch;

class FutureStmt extends soot.jimple.internal.AbstractStmt {
  public Object object;

  public FutureStmt(Object object) {
    this.object = object;
  }

  public FutureStmt() {
  }

  public String toString() {
    return "<futurestmt>";
  }

  public void toString(UnitPrinter up) {
    up.literal("<futurestmt>");
  }

  public void apply(Switch sw) {
    ((soot.jimple.StmtSwitch) sw).defaultCase(this);
  }

  public boolean fallsThrough() {
    throw new RuntimeException();
  }

  public boolean branches() {
    throw new RuntimeException();
  }

  public Object clone() {
    throw new RuntimeException();
  }

}