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

import soot.Local;
import soot.SootMethod;

class MethodLocal {
  private final SootMethod m;
  private final Local l;

  public MethodLocal(SootMethod method, Local local) {
    m = method;
    l = local;
  }

  public SootMethod getMethod() {
    return m;
  }

  public Local getLocal() {
    return l;
  }

  public int hashCode() {
    return m.hashCode() + l.hashCode();
  }

  public boolean equals(Object other) {
    if (other instanceof MethodLocal) {
      MethodLocal another = (MethodLocal) other;
      return m.equals(another.getMethod()) && l.equals(another.getLocal());
    }

    return false;
  }

  public String toString() {
    return "[" + m.getSignature() + " : " + l.toString() + "]";
  }
}
