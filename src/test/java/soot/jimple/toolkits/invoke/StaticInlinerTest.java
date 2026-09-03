package soot.jimple.toolkits.invoke;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Mustafa Senoglu
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

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Test;

import soot.G;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.jimple.Jimple;

public class StaticInlinerTest {

  @Test
  public void bodyResolutionCanAddApplicationClass() throws Exception {
    G.reset();

    SootClass initialClass = new SootClass("InitialClass", Modifier.PUBLIC);
    SootMethod method = new SootMethod("method", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    initialClass.addMethod(method);
    Scene.v().addClass(initialClass);
    initialClass.setApplicationClass();

    method.setSource((resolvedMethod, phaseName) -> {
      SootClass resolvedClass = new SootClass("ResolvedClass", Modifier.PUBLIC);
      Scene.v().addClass(resolvedClass);
      resolvedClass.setApplicationClass();
      return Jimple.v().newBody(resolvedMethod);
    });

    Method computeSizes = StaticInliner.class.getDeclaredMethod("computeAverageMethodSizeAndSaveOriginalSizes");
    computeSizes.setAccessible(true);
    computeSizes.invoke(StaticInliner.v());
  }
}
