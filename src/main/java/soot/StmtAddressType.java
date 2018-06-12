package soot;

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

import soot.util.Switch;

/**
 * Soot representation of the Java type for a statement address. Implemented as a singleton.
 */
@SuppressWarnings("serial")
public class StmtAddressType extends Type {
  public StmtAddressType(Singletons.Global g) {
  }

  public static StmtAddressType v() {
    return G.v().soot_StmtAddressType();
  }

  public boolean equals(Object t) {
    return this == t;
  }

  public int hashCode() {
    return 0x74F368D1;
  }

  public String toString() {
    return "address";
  }

  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseStmtAddressType(this);
  }
}
