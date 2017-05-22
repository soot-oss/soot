package soot;


import org.junit.Assert;
import org.junit.Test;

public class RefTypeTest {
	
	@Test
	public void testMerge() {
		G.reset();
		
		Scene.v().loadNecessaryClasses();
		
		SootClass sc1 = new SootClass("Class1");
		SootClass sc2 = new SootClass("Class2");
		SootClass sc3 = new SootClass("Class3");
		SootClass sc4 = new SootClass("Class4");
		SootClass sc5 = new SootClass("Class5");
		
		Scene.v().addClass(sc1);
		Scene.v().addClass(sc2);
		Scene.v().addClass(sc3);
		Scene.v().addClass(sc4);
		Scene.v().addClass(sc5);
		
		sc1.setSuperclass(Scene.v().getObjectType().getSootClass());
		sc2.setSuperclass(sc1);
		sc3.setSuperclass(sc2);
		sc4.setSuperclass(sc2);
		sc5.setSuperclass(sc4);
		
		Type tpMerged = sc5.getType().merge(sc3.getType(), Scene.v());
		Assert.assertEquals("Class2", ((RefType) tpMerged).getClassName()); 
	}
	
}
