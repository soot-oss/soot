package soot.baf;

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

import soot.G;
import soot.Singletons;
import soot.Type;
import soot.util.Switch;

public class DoubleWordType extends Type {
  public DoubleWordType(Singletons.Global g) {
  }

  public static DoubleWordType v() {
    return G.v().soot_baf_DoubleWordType();
  }

  public boolean equals(Object t) {
    return this == t;
  }

  public int hashCode() {
    return 0xA247839F;
  }

  public String toString() {
    return "dword";
  }

  public void apply(Switch sw) {
    throw new RuntimeException("invalid switch case");
  }
}
