package soot.util.backend;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.UnknownType;

@PrepareForTest({ Scene.class, UnknownType.class, RefType.class })
@RunWith(PowerMockRunner.class)
public class SootASMClassWriterTest {

	private Scene scene;

	private SootClass sc1;
	private SootClass sc2;
	private SootClass object;
	private SootClass commonSuperClass;

	private RefType type1;
	private RefType type2;
	private RefType objectType;

	SootASMClassWriter cw;

	@Before
	public void setUp()
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		mockStatic(Scene.class);
		mockStatic(RefType.class);
		mockStatic(UnknownType.class);

		scene = mock(Scene.class);
		when(Scene.v()).thenReturn(scene);

		UnknownType unknown = mock(UnknownType.class);
		when(UnknownType.v()).thenReturn(unknown);

		sc1 = mockClass("A");
		sc2 = mockClass("B");

		type1 = RefType.v("A");
		type2 = RefType.v("B");

		object = mockClass("java.lang.Object");

		objectType = mock(RefType.class);
		when(object.getType()).thenReturn(objectType);
		when(Scene.v().getObjectType()).thenReturn(objectType);
		when(objectType.getSootClass()).thenReturn(object);
		when(objectType.getClassName()).thenReturn("java.lang.Object");

		when(type1.merge(type2, scene)).thenCallRealMethod();

		commonSuperClass = mockClass("C");
		commonSuperClass.setResolvingLevel(SootClass.HIERARCHY);

		when(commonSuperClass.getSuperclass()).thenReturn(object);
		when(commonSuperClass.getSuperclassUnsafe()).thenReturn(object);

		cw = mock(SootASMClassWriter.class);
		when(cw.getCommonSuperClass("A", "B")).thenCallRealMethod();
	}

	@Test
	public void testGetCommonSuperClassNormal() {
		when(sc1.getSuperclass()).thenReturn(commonSuperClass);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);

		when(sc1.getSuperclassUnsafe()).thenReturn(commonSuperClass);
		when(sc2.getSuperclassUnsafe()).thenReturn(commonSuperClass);

		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}

	@Test
	public void testGetCommonSuperClassTransitive() {
		SootClass sc11 = mockClass("AA");
		SootClass sc21 = mockClass("BB");

		when(sc11.getSuperclass()).thenReturn(commonSuperClass);
		when(sc21.getSuperclass()).thenReturn(commonSuperClass);

		when(sc11.getSuperclassUnsafe()).thenReturn(commonSuperClass);
		when(sc21.getSuperclassUnsafe()).thenReturn(commonSuperClass);

		when(sc1.getSuperclass()).thenReturn(sc11);
		when(sc2.getSuperclass()).thenReturn(sc21);

		when(sc1.getSuperclassUnsafe()).thenReturn(sc11);
		when(sc2.getSuperclassUnsafe()).thenReturn(sc21);

		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}

	@Test
	public void testGetCommonSuperClassPhantomClass() {
		SootClass sc11 = mockClass("AA");
		when(sc11.isPhantomClass()).thenReturn(true);

		when(sc11.hasSuperclass()).thenReturn(false);
		when(sc11.getSuperclass()).thenReturn(null);
		when(sc11.getSuperclassUnsafe()).thenReturn(null);

		when(sc1.getSuperclass()).thenReturn(sc11);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);

		when(sc1.getSuperclassUnsafe()).thenReturn(sc11);
		when(sc2.getSuperclassUnsafe()).thenReturn(commonSuperClass);

		assertEquals("java/lang/Object", cw.getCommonSuperClass("A", "B"));
	}

	@Test
	public void testGetCommonSuperClassTransitivePhantomClass() {
		SootClass sc = mockClass("CC");
		when(sc.isPhantomClass()).thenReturn(true);

		when(sc.hasSuperclass()).thenReturn(false);
		when(sc.getSuperclass()).thenReturn(null);
		when(sc.getSuperclassUnsafe()).thenReturn(null);

		when(sc1.getSuperclass()).thenReturn(commonSuperClass);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);
		when(commonSuperClass.getSuperclass()).thenReturn(sc);

		when(sc1.getSuperclassUnsafe()).thenReturn(commonSuperClass);
		when(sc2.getSuperclassUnsafe()).thenReturn(commonSuperClass);
		when(commonSuperClass.getSuperclassUnsafe()).thenReturn(sc);

		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}

	private SootClass mockClass(String name) {
		SootClass sc = mock(SootClass.class);
		RefType type = mock(RefType.class);

		when(sc.getName()).thenReturn(name);
		when(sc.getType()).thenReturn(type);
		when(sc.hasSuperclass()).thenReturn(true);
		when(sc.resolvingLevel()).thenReturn(SootClass.HIERARCHY);
		when(scene.getSootClass(name)).thenReturn(sc);
		when(RefType.v(name)).thenReturn(type);
		Whitebox.setInternalState(type, "className", name);
		when(type.getClassName()).thenCallRealMethod();

		return sc;
	}
}
