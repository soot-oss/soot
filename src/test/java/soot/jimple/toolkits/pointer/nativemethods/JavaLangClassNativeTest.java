package soot.jimple.toolkits.pointer.nativemethods;

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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import soot.G;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.pointer.representations.AbstractObject;
import soot.jimple.toolkits.pointer.representations.Environment;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeHelper;

public class JavaLangClassNativeTest {

  private static class RecordingHelper extends NativeHelper {
    final Map<ReferenceVariable, List<AbstractObject>> assigned = new IdentityHashMap<>();
    final ReferenceVariable elementVar = mock(ReferenceVariable.class);

    @Override
    protected void assignImpl(ReferenceVariable lhs, ReferenceVariable rhs) {
    }

    @Override
    protected void assignObjectToImpl(ReferenceVariable lhs, AbstractObject obj) {
      assigned.computeIfAbsent(lhs, k -> new ArrayList<>()).add(obj);
    }

    @Override
    protected void throwExceptionImpl(AbstractObject obj) {
    }

    @Override
    protected ReferenceVariable arrayElementOfImpl(ReferenceVariable base) {
      return elementVar;
    }

    @Override
    protected ReferenceVariable cloneObjectImpl(ReferenceVariable source) {
      return source;
    }

    @Override
    protected ReferenceVariable newInstanceOfImpl(ReferenceVariable cls) {
      return mock(ReferenceVariable.class);
    }

    @Override
    protected ReferenceVariable staticFieldImpl(String className, String fieldName) {
      return mock(ReferenceVariable.class);
    }

    @Override
    protected ReferenceVariable tempFieldImpl(String fieldsig) {
      return mock(ReferenceVariable.class);
    }

    @Override
    protected ReferenceVariable tempVariableImpl() {
      return mock(ReferenceVariable.class);
    }

    @Override
    protected ReferenceVariable tempLocalVariableImpl(SootMethod method) {
      return mock(ReferenceVariable.class);
    }
  }

  private static SootMethod methodWithSubSignature(String subSignature) {
    SootMethod method = mock(SootMethod.class);
    when(method.getSubSignature()).thenReturn(subSignature);
    return method;
  }

  @Test
  public void getDeclaredFields0PropagatesFieldObjects() {
    G.reset();
    Scene.v().loadNecessaryClasses();
    RecordingHelper helper = new RecordingHelper();
    ReferenceVariable returnVar = mock(ReferenceVariable.class);
    ReferenceVariable[] params = new ReferenceVariable[] { mock(ReferenceVariable.class) };

    new JavaLangClassNative(helper).simulateMethod(
        methodWithSubSignature("java.lang.reflect.Field[] getDeclaredFields0(boolean)"),
        mock(ReferenceVariable.class), returnVar, params);

    assertTrue("return value must point to the abstract Field[] object",
        helper.assigned.getOrDefault(returnVar, new ArrayList<>()).contains(Environment.v().getArrayFields()));
    assertTrue("array elements must point to the abstract Field object",
        helper.assigned.getOrDefault(helper.elementVar, new ArrayList<>())
            .contains(Environment.v().getFieldObject()));
  }
}
