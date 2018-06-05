package soot;

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
