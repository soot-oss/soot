package soot.jimple;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

/**
 * @author Andreas Dann created on 06.02.19
 * @author Manuel Benz 27.2.19
 */

public class PolymorphicDispatch {

  public void unambiguousMethod() throws Throwable {
    MethodHandle methodHandle = MethodHandles.lookup().findVirtual(PolymorphicDispatch.class, "someMethod", null);
    Object ob = methodHandle.invoke();
    System.out.println(ob);
  }

  public void ambiguousMethod() throws Throwable {
    MethodHandle methodHandle = MethodHandles.lookup().findVirtual(PolymorphicDispatch.class, "someMethod", null);
    // call on sig 1
    Object ob = methodHandle.invoke();
    System.out.println(ob);

    // call on sig 2
    int res = (int) methodHandle.invoke(1);
    System.out.println(res);
  }
}