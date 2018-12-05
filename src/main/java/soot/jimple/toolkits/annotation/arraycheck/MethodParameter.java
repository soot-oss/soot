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

class MethodParameter {
  private SootMethod m;
  private int param;

  public MethodParameter(SootMethod m, int i) {
    this.m = m;
    this.param = i;
  }

  public Type getType() {
    return m.getParameterType(param);
  }

  public int hashCode() {
    return m.hashCode() + param;
  }

  public SootMethod getMethod() {
    return m;
  }

  public int getIndex() {
    return param;
  }

  public boolean equals(Object other) {
    if (other instanceof MethodParameter) {
      MethodParameter another = (MethodParameter) other;

      return (m.equals(another.getMethod()) && param == another.getIndex());
    }

    return false;
  }

  public String toString() {
    return "[" + m.getSignature() + " : P" + param + "]";
  }
}
