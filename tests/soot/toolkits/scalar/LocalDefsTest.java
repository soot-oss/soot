package soot.toolkits.scalar;

import static org.junit.Assert.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import soot.Local;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.toolkits.graph.DirectedGraph;

public class LocalDefsTest {	
	
	public static class TestLocal implements Local {
		private static final long serialVersionUID = -9205035160965128826L;
		
		private int number = -1;
		
		@Override
		public void setNumber(int number) {		
			this.number = number;
		}

		@Override
		public int getNumber() {
			return number;
		}
		
		@Override
		public List<ValueBox> getUseBoxes() {	
	    	throw new UnsupportedOperationException();
		}

		@Override
		public soot.Type getType() {
	    	throw new UnsupportedOperationException();
		}
		
	    public String toString() {
	    	return String.valueOf(number);
	    }
	    
		@Override
		public void toString(soot.UnitPrinter up) {
	    	throw new UnsupportedOperationException();
		}

		@Override
		public void apply(soot.util.Switch sw) {
	    	throw new UnsupportedOperationException();
		}

		@Override
		public boolean equivTo(Object o) {
	    	throw new UnsupportedOperationException();
		}

		@Override
		public int equivHashCode() {
	    	throw new UnsupportedOperationException();
		}

		@Override
		public String getName() {
			return String.valueOf(this);
		}

		@Override
		public void setName(String name) {		
	    	throw new UnsupportedOperationException();	
		}

		@Override
		public void setType(soot.Type t) {
	    	throw new UnsupportedOperationException();
		}
		
	    public Object clone() {
	    	throw new UnsupportedOperationException();
	    }		
	}
	
	public static class TestBox extends soot.tagkit.AbstractHost 
			implements ValueBox {
		private static final long serialVersionUID = 1L;
		Value value;
		
		public TestBox(Local local) {
			value = local;
		}
		
	    public String toString() {
	    	return value.toString();
	    }
	    
		@Override
		public boolean canContainValue(Value value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setValue(Value value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Value getValue() {
			return value;
		}

		@Override
		public void toString(soot.UnitPrinter up) {
			throw new UnsupportedOperationException();
		}		
	}
	
	public static class TestUnit extends soot.AbstractUnit {
		private static final long serialVersionUID = 621613259853563173L;
		
		final List<ValueBox> def;
		final List<ValueBox> use;
		
		public TestUnit(TestLocal defs, TestLocal ... uses) {
			if ( defs == null) {
				def = emptyList();
			} else {
				def = singletonList((ValueBox) new TestBox(defs));
			}
			
			List<ValueBox> u = new ArrayList<ValueBox>();
			for ( TestLocal l : uses ) {
				u.add(new TestBox(l));
			}

			use = unmodifiableList(u);
		}
		
	    final List<Unit> preds = new ArrayList<Unit>();
	    final List<Unit> succs = new ArrayList<Unit>();

	    public TestUnit addSucc(TestUnit u) {
	    	if (!succs.contains(u))
	    		succs.add(u);
	    	if (!u.preds.contains(this))
	    		u.preds.add(this);
	        return this;
	    }    
	    
	    public TestUnit addSuccs(TestUnit... s) {
	    	for (TestUnit u : s) addSucc(u);
	    	return this;
	    }	    

	    public TestUnit addPreds(TestUnit... p) {
	    	for (TestUnit u : p) addPred(u);
	    	return this;
	    }
	    
	    public TestUnit addPred(TestUnit u) {
	    	if (!preds.contains(u))
	    		preds.add(u);
	    	if (!u.succs.contains(this))
	    		u.succs.add(this);
	        return this;
	    }		

	    public String toString() {
	    	if (def.isEmpty()) {
	    		return "f" + use;
	    	}	    	
	    	return def + " := f" + use;
	    }
	    
		@Override
		public List<ValueBox> getUseBoxes() {
			return use;
		}

		@Override
		public List<ValueBox> getDefBoxes() {
			return def;
		}

		@Override
		public boolean fallsThrough() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean branches() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void toString(soot.UnitPrinter up) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object clone() {
			throw new UnsupportedOperationException();
		}
	}
	
	public static class TestGraph implements DirectedGraph<Unit> {		

		private final List<Unit> h = new ArrayList<Unit>();
		private final List<Unit> t = new ArrayList<Unit>();
		private final List<Unit> a = new ArrayList<Unit>();
		
		public TestGraph (Collection<? extends TestUnit> c ) {
			a.addAll(c);
			for ( TestUnit u : c) {
				if (u.preds.isEmpty())
					h.add(u);
				if (u.succs.isEmpty())
					t.add(u);
			}
		}
		
		@Override
		public String toString() {
			return Arrays.toString(a.toArray(new Unit[a.size()]));
		}		
		
		@Override
		public List<Unit> getHeads() {
			return h;
		}

		@Override
		public List<Unit> getTails() {
			return t;
		}

		@Override
		public List<Unit> getPredsOf(Unit s) {
			return ((TestUnit)s).preds;
		}

		@Override
		public List<Unit> getSuccsOf(Unit s) {
			return ((TestUnit)s).succs;
		}

		@Override
		public int size() {
			return a.size();
		}

		@Override
		public Iterator<Unit> iterator() {
			return a.iterator();
		}		
	}
			
	private static LocalDefs newLocalDefs(Collection<? extends TestUnit> nodes) {
		DirectedGraph<Unit> g = new TestGraph(nodes);
		LiveLocals live = new SimpleLiveLocals(g){};

		assertFalse(g.getHeads().isEmpty());
		
		return new SmartLocalDefs(g, live, locals){};
	}		
	
	private static TestUnit newTestUnit(Collection<TestUnit> nodes,
			TestLocal defs, TestLocal ... uses) {
		TestUnit u = new TestUnit(defs, uses);
		nodes.add(u);
		return u;
	}
	
	private static void createChain(TestUnit ... units) {
		for (int i=1; i < units.length;i++) {
			units[i].addPred(units[i-1]);
		}
	}

	private static TestLocal[] locals;

	private Collection<TestUnit> nodes;
	
	static public <T> void assertSetEquals(Collection<? extends T> expected, Collection<? extends T> actual) {
		assertEquals(new HashSet<T>(expected), new HashSet<T>(actual));
	}
	
	
	@BeforeClass
	public static void setUp() {
		locals = new TestLocal[16];
		for(int i = 0; i < locals.length; i++) {
			(locals[i] = new TestLocal()).setNumber(i*19);
		}		
	}

	@Before
	public void initNodes() {
		nodes = new ArrayList<TestUnit>();
	}

	@After
	public void postConditionLocalNumbers() {
		for(int i = 0; i < locals.length; i++) {
			org.junit.Assert.assertEquals(i*19, locals[i].getNumber());
		}		
	}
	
	
	/**
	 * n:1
	 */
	@Test
	public void testSingleWaySink() {
		TestUnit sink1 = newTestUnit(nodes, null, locals);		
		TestUnit[] units = new TestUnit[locals.length];		
		for(int i = 0; i < locals.length; i++) {
			units[i] = newTestUnit(nodes, locals[i]).addSucc(sink1);
		}
				
		LocalDefs defs = newLocalDefs(nodes);
		
		for (int i = 0; i < locals.length; i++) {
			for (TestUnit u : units) {
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));
			}						

			assertSetEquals(asList(units[i]), defs.getDefsOfAt(locals[i], sink1));
		}	
	}
	
	
	@Test
	public void testSingleWaySink2() {	
		TestUnit sink1 = newTestUnit(nodes, null, locals);
		TestUnit[] units = new TestUnit[locals.length];		
		for(int i = 0; i < units.length; i++) {
			units[i] = newTestUnit(nodes, locals[i]).addSucc(sink1);
		}
		
		TestUnit sink2 = newTestUnit(nodes, null, locals).addPred(sink1);
				
		LocalDefs defs = newLocalDefs(nodes);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));

			assertSetEquals(asList(units[i]), defs.getDefsOfAt(locals[i], sink1));
			assertSetEquals(asList(units[i]), defs.getDefsOfAt(locals[i], sink2));
		}
	}
	
	/**
	 * n:1 2
	 */
	@Test
	public void testDoubleWaySink() {
		TestUnit sink1 = newTestUnit(nodes, null, locals);		
		TestUnit[] units1 = new TestUnit[locals.length];
		TestUnit[] units2 = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units1[i] = newTestUnit(nodes, locals[i]).addSucc(sink1);
			units2[i] = newTestUnit(nodes, locals[i]).addSucc(sink1);
		}
				
		LocalDefs defs = newLocalDefs(nodes);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units1)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));
			
			for (TestUnit u : units2)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));

			assertSetEquals(asList(units1[i], units2[i]), defs.getDefsOfAt(locals[i], sink1));
		}
	}
	
	/**
	 * n:1 2
	 */
	@Test
	public void testDoubleWayLoopSink() {
		TestUnit sink = newTestUnit(nodes, null, locals);		
		TestUnit[] units1 = new TestUnit[locals.length];
		TestUnit[] units2 = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units1[i] = newTestUnit(nodes, locals[i]).addSucc(sink);
			units2[i] = newTestUnit(nodes, locals[i]).addSucc(sink).addPred(sink);
		}				
		LocalDefs defs = newLocalDefs(nodes);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units1)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));
			
			for (TestUnit u : units2)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));

			assertSetEquals(asList(units1[i], units2[i]), defs.getDefsOfAt(locals[i], sink));
		}
	}

	
	@Test
	public void testDoubleRingFlow() {	
		TestUnit[] units = new TestUnit[6];
		for(int i = 0; i < units.length; i++) {
			units[i] = newTestUnit(nodes, locals[i], locals);
		}
		newTestUnit(nodes, null).addSuccs(units);
		
		createChain(units[0], units[1], units[0]);
		createChain(units[2], units[3], units[2]);
		createChain(units[2], units[4], units[1]);
		createChain(units[0], units[5], units[3]);

		LocalDefs defs = newLocalDefs(nodes);
		
		for(int i = 0; i < units.length; i++) {
			for (TestUnit u : units)
				assertSetEquals(asList(units[i]), 
						defs.getDefsOfAt(locals[i], u));
		}
		
		for(int i = units.length; i < locals.length; i++) {
			for (TestUnit u : units)
				assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], u));
		}
		
	}

	@Test
	public void testPartialUndefined() {	
		TestUnit u1 = newTestUnit(nodes, locals[0], locals[0]);
		TestUnit u2 = newTestUnit(nodes, null, locals[0]);
		TestUnit u3 = newTestUnit(nodes, null).addPred(u1).addPred(u2);
		TestUnit u4 = newTestUnit(nodes, null, locals).addPred(u3);
		TestUnit u5 = newTestUnit(nodes, locals[0], locals[0]).addPred(u4);
		
		LocalDefs defs = newLocalDefs(nodes);

		assertSetEquals(emptyList(), defs.getDefsOfAt(locals[0], u1));
		assertSetEquals(emptyList(), defs.getDefsOfAt(locals[0], u2));
		assertSetEquals(asList(u1), defs.getDefsOfAt(locals[0], u4));
		assertSetEquals(asList(u1), defs.getDefsOfAt(locals[0], u5));
		
	}

	@Test
	public void testNotInitializedLoopCounter() {
		TestUnit u1 = newTestUnit(nodes, null);	
		TestUnit u2 = newTestUnit(nodes, null).addPred(u1);
		TestUnit u3 = newTestUnit(nodes, locals[0], locals[0]).addPred(u2).addSucc(u2);
		TestUnit u4 = newTestUnit(nodes, null, locals[0]).addPred(u3);	

		LocalDefs defs = newLocalDefs(nodes);

		assertSetEquals(emptyList(), defs.getDefsOfAt(locals[0], u1));
		assertSetEquals(emptyList(), defs.getDefsOfAt(locals[0], u2));
		assertSetEquals(asList(u3), defs.getDefsOfAt(locals[0], u3));
		assertSetEquals(asList(u3), defs.getDefsOfAt(locals[0], u4));
	}
	
	@Test
	public void testComplexFlow() {
		TestUnit[] units = new TestUnit[locals.length*2];
		
		for(int i = 0; i < units.length; i++) {
			units[i] = newTestUnit(nodes, locals[i%locals.length]);
		}
		for (TestUnit u : units) {
			u.addSuccs(units);
		}
		TestUnit h = newTestUnit(nodes, null, locals).addSuccs(units);	
		TestUnit t = newTestUnit(nodes, null, locals).addPreds(units);	

		LocalDefs defs = newLocalDefs(nodes);
		
		for(int i = 0; i < locals.length; i++) {
			assertSetEquals(emptyList(), defs.getDefsOfAt(locals[i], h));
			assertSetEquals(asList(units[i], units[i+locals.length]), defs.getDefsOfAt(locals[i], t));
		}
		
	}

	
}
