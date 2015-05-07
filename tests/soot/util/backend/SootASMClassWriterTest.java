package soot.util.backend;

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
import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({Scene.class, UnknownType.class, RefType.class})
@RunWith(PowerMockRunner.class)
public class SootASMClassWriterTest {
	
	private Scene scene;

	private SootClass sc1;
	private SootClass sc2;
	private SootClass object;
	private SootClass commonSuperClass;
	
	private RefType type1;
	private RefType type2;
	
	SootASMClassWriter cw;
	
	
	@Before
	public void setUp() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{		
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
		
		when(type1.merge(type2, scene)).thenCallRealMethod();
		
		commonSuperClass = mockClass("C");
		when(commonSuperClass.getSuperclass()).thenReturn(object);
		
		
		cw = mock(SootASMClassWriter.class);
		when(cw.getCommonSuperClass("A", "B")).thenCallRealMethod();
	}

	@Test
	public void testGetCommonSuperClassNormal() {
		when(sc1.getSuperclass()).thenReturn(commonSuperClass);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);
		
		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}
	
	@Test
	public void testGetCommonSuperClassTransitive() {
		SootClass sc11 = mockClass("AA");
		SootClass sc21 = mockClass("BB");
		
		when(sc11.getSuperclass()).thenReturn(commonSuperClass);
		when(sc21.getSuperclass()).thenReturn(commonSuperClass);
		
		when(sc1.getSuperclass()).thenReturn(sc11);
		when(sc2.getSuperclass()).thenReturn(sc21);
		
		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}
	
	@Test
	public void testGetCommonSuperClassPhantomClass() {
		SootClass sc11 = mockClass("AA");
		when(sc11.isPhantomClass()).thenReturn(true);
		when(sc11.hasSuperclass()).thenReturn(false);
		when(sc11.getSuperclass()).thenReturn(null);
		
		when(sc1.getSuperclass()).thenReturn(sc11);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);
		
		assertEquals("java/lang/Object", cw.getCommonSuperClass("A", "B"));
	}
	
	@Test
	public void testGetCommonSuperClassTransitivePhantomClass() {
		SootClass sc = mockClass("CC");
		when(sc.isPhantomClass()).thenReturn(true);
		when(sc.hasSuperclass()).thenReturn(false);
		when(sc.getSuperclass()).thenReturn(null);
		
		when(sc1.getSuperclass()).thenReturn(commonSuperClass);
		when(sc2.getSuperclass()).thenReturn(commonSuperClass);
		when(commonSuperClass.getSuperclass()).thenReturn(sc);
		
		assertEquals("C", cw.getCommonSuperClass("A", "B"));
	}
	
	private SootClass mockClass(String name){
		SootClass sc = mock(SootClass.class);
		RefType type = mock(RefType.class);
		
		when(sc.getName()).thenReturn(name);
		when(sc.getType()).thenReturn(type);
		when(sc.hasSuperclass()).thenReturn(true);
		when(scene.getSootClass(name)).thenReturn(sc);
		when(RefType.v(name)).thenReturn(type);
		Whitebox.setInternalState(type, "className", name);
		when(type.getClassName()).thenCallRealMethod();
		
		return sc;
	}
}
