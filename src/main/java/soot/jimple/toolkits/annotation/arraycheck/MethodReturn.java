package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import soot.Type;

class MethodReturn {
  private SootMethod m;

  public MethodReturn(SootMethod m) {
    this.m = m;
  }

  public SootMethod getMethod() {
    return m;
  }

  public Type getType() {
    return m.getReturnType();
  }

  public int hashCode() {
    return m.hashCode() + m.getParameterCount();
  }

  public boolean equals(Object other) {
    if (other instanceof MethodReturn) {
      return m.equals(((MethodReturn) other).getMethod());
    }

    return false;
  }

  public String toString() {
    return "[" + m.getSignature() + " : R]";
  }
}
