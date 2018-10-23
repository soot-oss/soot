package soot.dexpler.instructions;

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
import java.util.function.Supplier;

/**
 * Compile by using "$JAVA_HOME/bin/javac -cp $JAVA_HOME/lib/jrt-fs.jar *; dx --dex --min-sdk-version=26
 * --output=dexBytecodeTarget.dex ../../../ "
 *
 */
public class DexBytecodeTarget {

  void invokePolymorphicTarget(MethodHandle handle) throws Throwable {
    handle.invoke("foo", "bar");
  }

  void invokeCustomTarget() throws Throwable {
    Supplier<String> someLambda = () -> "foo";
  }

}
