package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.Local;

public class LocalStmtPair {
  Local local;
  Stmt stmt;

  public LocalStmtPair(Local local, Stmt stmt) {
    this.local = local;
    this.stmt = stmt;
  }

  public boolean equals(Object other) {
    if (other instanceof LocalStmtPair && ((LocalStmtPair) other).local == this.local
        && ((LocalStmtPair) other).stmt == this.stmt) {
      return true;
    } else {
      return false;
    }
  }

  public int hashCode() {
    return local.hashCode() * 101 + stmt.hashCode() + 17;
  }

}
