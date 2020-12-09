package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Manuel Benz
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

/** @author Manuel Benz at 20.01.20 */
public class PropagateLineNumberTag {
  public static class A {
    public A() {}
  }

  public void nullAssignment() {
    PropagateLineNumberTag.A b = new PropagateLineNumberTag.A();
    PropagateLineNumberTag.A a = null;
    A z = foo(a);
  }

  public void transitiveNullAssignment() {
    PropagateLineNumberTag.A b = new PropagateLineNumberTag.A();
    PropagateLineNumberTag.A a = null;
    PropagateLineNumberTag.A c = a;
    A z = foo(a);
    A y = foo(c);
  }

  private static A foo(A param) {
    return param;
  }
}
