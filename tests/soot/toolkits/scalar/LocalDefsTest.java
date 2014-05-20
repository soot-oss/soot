package soot.toolkits.scalar;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import soot.AbstractUnit;
import soot.Local;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.tagkit.AbstractHost;
import soot.toolkits.graph.DirectedGraph;
import soot.util.Switch;

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
		public Type getType() {
	    	throw new UnsupportedOperationException();
		}
		
	    public String toString() {
	    	return String.valueOf(number);
	    }
	    
		@Override
		public void toString(UnitPrinter up) {
	    	throw new UnsupportedOperationException();
		}

		@Override
		public void apply(Switch sw) {
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
		public void setType(Type t) {
	    	throw new UnsupportedOperationException();
		}
		
	    public Object clone() {
	    	throw new UnsupportedOperationException();
	    }		
	}
	
	public static class TestBox extends AbstractHost implements ValueBox {
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
		public void toString(UnitPrinter up) {
			throw new UnsupportedOperationException();
		}		
	}
		
	public static class TestUnit extends AbstractUnit {
		private static final long serialVersionUID = 621613259853563173L;
		
		final List<ValueBox> def;
		final List<ValueBox> use;
		
		public TestUnit(TestLocal defs, TestLocal ... uses) {
			if ( defs == null) {
				def = Collections.emptyList();
			} else {
				def = Collections.<ValueBox>singletonList(new TestBox(defs));
			}
			
			List<ValueBox> u = new ArrayList<ValueBox>();
			for ( TestLocal l : uses ) {
				u.add(new TestBox(l));
			}

			use = Collections.<ValueBox>unmodifiableList(u);
		}
		
	    final List<Unit> preds = new ArrayList<Unit>();
	    final List<Unit> succs = new ArrayList<Unit>();

	    public TestUnit addSucc(TestUnit succ) {
	    	if (!succ.preds.contains(this))
	    		succ.preds.add(this);
	    	if (!this.succs.contains(succ))
	    		this.succs.add(succ);
	        return this;
	    }
	    
	    public TestUnit addPred(TestUnit pred) {
	        pred.succs.add(this);
	        this.preds.add(pred);
	        return this;
	    }	
		
	    public String toString() {
	    	return def + ":= " + use;
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
		public void toString(UnitPrinter up) {
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
			
	private static class SmartLocalDefsTest extends SmartLocalDefs {
		protected SmartLocalDefsTest(Collection<? extends TestUnit> nodes, final Local[] locals) {
			super(new TestGraph(nodes), new LiveLocals() {			
				final List<Local> localList = Arrays.<Local>asList(locals);
				@Override
				public List<Local> getLiveLocalsBefore(Unit s) {
					return localList;
				}			
				@Override
				public List<Local> getLiveLocalsAfter(Unit s) {
					return localList;
				}
			}, locals);
		}		
	}

	private static TestLocal[] locals;
	
	@BeforeClass
	public static void setUp() {
		locals = new TestLocal[16];
		for(int i = 0; i < locals.length; i++) {
			locals[i] = new TestLocal();
			locals[i].setNumber(i*19);
		}
	}


	@After
	public void postConditionLocalNumbers() {
		for(int i = 0; i < locals.length; i++) {
			assertEquals(i*19, locals[i].getNumber());
		}		
	}
	
	
	/**
	 * n:1
	 */
	@Test
	public void test1() {
		Collection<TestUnit> nodes = new ArrayList<TestUnit>();
		TestUnit sink1 = new TestUnit(null, locals);		
		TestUnit[] units = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units[i] = new TestUnit(locals[i]);
			units[i].addSucc(sink1);
		}
		Collections.addAll(nodes, units);
		nodes.add(sink1);
				
		LocalDefs defs = new SmartLocalDefsTest(nodes, locals);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);
			
			assertArrayEquals(
					new TestUnit[]{units[i]}, 
					defs.getDefsOfAt(locals[i], sink1).toArray());
		}	
	}
	
	/**
	 * n:1 2
	 */
	@Test
	public void test2() {
		Collection<TestUnit> nodes = new ArrayList<TestUnit>();
		TestUnit sink1 = new TestUnit(null, locals);		
		TestUnit[] units1 = new TestUnit[locals.length];
		TestUnit[] units2 = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units1[i] = new TestUnit(locals[i]);
			units1[i].addSucc(sink1);
			units2[i] = new TestUnit(locals[i]);
			units2[i].addSucc(sink1);
		}
		Collections.addAll(nodes, units1);
		Collections.addAll(nodes, units2);
		nodes.add(sink1);
				
		LocalDefs defs = new SmartLocalDefsTest(nodes, locals);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units1)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);
			for (TestUnit u : units2)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);

			Set<Unit> expected = new HashSet<Unit>();
			Collections.addAll(expected, units1[i], units2[i]);
			Set<Unit> actual = new HashSet<Unit>(defs.getDefsOfAt(locals[i], sink1));
			assertEquals(expected, actual);
		}
	}
	
	/**
	 * n:1 2
	 */
	@Test
	public void test3() {
		Collection<TestUnit> nodes = new ArrayList<TestUnit>();
		TestUnit sink1 = new TestUnit(null, locals);		
		TestUnit[] units1 = new TestUnit[locals.length];
		TestUnit[] units2 = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units1[i] = new TestUnit(locals[i]);
			units1[i].addSucc(sink1);
			units2[i] = new TestUnit(locals[i]);
			units2[i].addPred(sink1);
			units2[i].addSucc(sink1);
		}
		Collections.addAll(nodes, units1);
		Collections.addAll(nodes, units2);
		nodes.add(sink1);
				
		LocalDefs defs = new SmartLocalDefsTest(nodes, locals);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units1)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);
			for (TestUnit u : units2)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);

			Set<Unit> expected = new HashSet<Unit>();
			Collections.addAll(expected, units1[i], units2[i]);
			Set<Unit> actual = new HashSet<Unit>(defs.getDefsOfAt(locals[i], sink1));
			assertEquals(expected, actual);
		}
	}
	
	@Test
	public void test4() {
		Collection<TestUnit> nodes = new ArrayList<TestUnit>();		
		TestUnit sink1 = new TestUnit(null, locals);			
		TestUnit sink2 = new TestUnit(null, locals);
		sink1.addSucc(sink2);
		TestUnit[] units1 = new TestUnit[locals.length];
		
		for(int i = 0; i < locals.length; i++) {
			units1[i] = new TestUnit(locals[i]);
			units1[i].addSucc(sink1);
		}
		Collections.addAll(nodes, units1);
		Collections.addAll(nodes, sink1, sink2);
				
		LocalDefs defs = new SmartLocalDefsTest(nodes, locals);
		
		for(int i = 0; i < locals.length; i++) {
			for (TestUnit u : units1)
				assertArrayEquals(
						new TestUnit[0], 
						defs.getDefsOfAt(locals[i], u).toArray()
					);

			Set<Unit> expected = new HashSet<Unit>();
			Collections.addAll(expected, units1[i]);
			Set<Unit> actual1 = new HashSet<Unit>(defs.getDefsOfAt(locals[i], sink1));
			assertEquals(expected, actual1);
			Set<Unit> actual2 = new HashSet<Unit>(defs.getDefsOfAt(locals[i], sink2));
			assertEquals(expected, actual2);
		}
	}
	
}
