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

import soot.G;
import soot.NullType;
import soot.Singletons;
import soot.Type;
import soot.util.Switch;

public class NullConstant extends Constant {
  public NullConstant(Singletons.Global g) {
  }

  public static NullConstant v() {
    return G.v().soot_jimple_NullConstant();
  }

  public boolean equals(Object c) {
    return c == G.v().soot_jimple_NullConstant();
  }

  public int hashCode() {
    return 982;
  }

  public String toString() {
    return Jimple.NULL;
  }

  public Type getType() {
    return NullType.v();
  }

  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseNullConstant(this);
  }
}
