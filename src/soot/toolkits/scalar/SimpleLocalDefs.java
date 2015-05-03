/* Soot - a J*va Optimization Framework
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.scalar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.UnitGraph;
import static java.util.Collections.singletonList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

/**
 * Analysis that provides an implementation of the LocalDefs interface.
 */
public class SimpleLocalDefs implements LocalDefs {
	static class Entry {
		final List<Unit> units;
		final Local local;
		
		Entry(List<Unit> units, Local local) {
			this.units = units;
			this.local = local;
		}
	}
	
	static class FlowBitSet<T> extends BitSet {
		private static final long serialVersionUID = -8348696077189400377L;
		
		final private T[] universe;
		
		FlowBitSet (T[] universe) {
			super(universe.length);
			this.universe = universe;
		}
	    
	    List<T> asList(int fromIndex, int toIndex) {
	    	BitSet bits = this;
	    	if (fromIndex < 0 || toIndex > universe.length || toIndex < fromIndex)
	    		throw new IndexOutOfBoundsException();

	    	if (fromIndex == toIndex || fromIndex == toIndex - 1) {
	    		if (bits.get(fromIndex)) {
	    			return singletonList(universe[fromIndex]);
	    		}
	    		return emptyList();
	    	}
	    	
	    	int i = bits.nextSetBit(fromIndex);
	    	if (i < 0 || i >= toIndex)
	    		return emptyList();
	    	
	    	if (i == toIndex - 1) 
	    		return singletonList(universe[i]);
	    	
	        List<T> elements = new ArrayList<T>(toIndex-i);
	                		
			for (;;) {
				int endOfRun = Math.min(toIndex, bits.nextClearBit(i+1));
				do { elements.add(universe[i++]); }
				while (i < endOfRun);
				if (i >= toIndex)
					break;
				i = bits.nextSetBit(i+1);
				if (i < 0 || i >= toIndex)
					break;
			}
			return elements;
	    }
	}	
	
	abstract static class Assignment implements LocalDefs {		
		final Map<ValueBox, Entry> resultValueBoxes;
	
		Assignment(int size) {
			// never keep values longer than required
			resultValueBoxes = new WeakHashMap<ValueBox, Entry>((size * 7)/2 + 3);
		}

		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			for (ValueBox useBox : s.getUseBoxes()) {
				if (l == useBox.getValue()) {
					Entry e = resultValueBoxes.get(useBox);
					// check if local was exchanged!
					if (e != null && l == e.local) {
						return unmodifiableList(e.units);
					}
				}
			}
			return emptyList();
		}
		
		public List<Unit> getDefsOf(ValueBox valueBox) {
			Entry e = resultValueBoxes.get(valueBox);
			if (e == null) 
				return emptyList();
			
			return unmodifiableList(e.units);
		}
		
		void putResult(ValueBox useBox, Local local, List<Unit> list) {
			if (list.isEmpty())
				return;			
			// one of the most expensive steps :|
			resultValueBoxes.put(useBox, new Entry(list, local));
		}
	}
	
	class StaticSingleAssignment extends Assignment {
		StaticSingleAssignment(List<Unit>[] unitList) {	
			// most of the units are like "a := b + c", 
			// or a invoke like "a := b.foo()"
			super(g.size());
			assert localRange[N] == 0;
			
			for (Unit s : g) {
				for (ValueBox useBox : s.getUseBoxes()) {
					Value v = useBox.getValue();
					if (v instanceof Local) {
						Local l = (Local) v;
						int lno = l.getNumber();

						putResult(useBox, l, unitList[lno]);
					}
				}
			}
		}
	}

	class FlowAssignment extends Assignment {				
		FlowAssignment(List<Unit>[] unitList) {
			// most of the units are like "a := b + c", 
			// or a invoke like "a := b.foo()"
			super(g.size());

			LocalDefsAnalysis defs = new LocalDefsAnalysis();

			for (Unit s : g) {
				for (ValueBox useBox : s.getUseBoxes()) {
					Value v = useBox.getValue();
					if (v instanceof Local) {
						Local l = (Local) v;
						int lno = l.getNumber();

						int from = localRange[lno];
						int to = localRange[lno + 1];
						assert from <= to;
						
						if (from == to) {
							putResult(useBox, l, unitList[lno]);
						} else {							
							putResult(useBox, l, defs.getFlowBefore(s).asList(from, to));
						}				
					}
				}
			}
		}
	}

	private class LocalDefsAnalysis extends ForwardFlowAnalysis<Unit, FlowBitSet<Unit>> {
		private LocalDefsAnalysis() {
			super(g);
			doAnalysis();
		}
				
		@Override
		protected void flowThrough(FlowBitSet<Unit> in, Unit unit, FlowBitSet<Unit> out) {
			out.or(in);
			
			Integer idx = indexOfUnit.get(unit);
			if (idx == null) 
				return;
			
			if (!in.isEmpty()) {	
				// reassign all definitions
				for (ValueBox vb : unit.getDefBoxes()) {
					Value v = vb.getValue();
					if (v instanceof Local) {
						Local l = (Local) v;
						int lno = l.getNumber();
	
						int from = localRange[lno];
						int to = localRange[1+lno];
						assert from <= to;
	
						if (from < to) {
							out.clear(from, to);
						}
					}
				}	
			}
			out.set(idx);
		}

		@Override
		protected void mergeInto(Unit succNode, FlowBitSet<Unit> inout, FlowBitSet<Unit> in) {
			inout.or(in);
		}

		@Override
		protected void copy(FlowBitSet<Unit> source, FlowBitSet<Unit> dest) {
			dest.clear();
			dest.or(source);
		}

		@Override
		protected FlowBitSet<Unit> newInitialFlow() {
			return new FlowBitSet<Unit>(universe);
		}

		@Override
		protected void merge(FlowBitSet<Unit> in1, FlowBitSet<Unit> in2, FlowBitSet<Unit> out) {
			throw new UnsupportedOperationException("should never be called");
		}
	}

	final private DirectedGraph<Unit> g;
	final int N;

	private int[] localRange;

	private Unit[] universe;
	private Map<Unit, Integer> indexOfUnit;

	private Assignment assignment;

	public SimpleLocalDefs(UnitGraph graph) {
		this(graph, false);
	}
	
	public SimpleLocalDefs(UnitGraph graph, boolean omitSSA) {
		this(graph, graph.getBody().getLocals(), omitSSA);
	}

	SimpleLocalDefs(DirectedGraph<Unit> graph, Collection<Local> locals, boolean omitSSA) {
		this(graph, locals.toArray(new Local[locals.size()]), omitSSA);
	}

	SimpleLocalDefs(DirectedGraph<Unit> graph, Local[] locals, boolean omitSSA) {
		if (Options.v().time())
			Timers.v().defsTimer.start();

		this.g = graph;
		this.N = locals.length;
		
		// reassign local numbers
		int[] oldNumbers = new int[N];
		for (int i = 0; i < N; i++) {
			oldNumbers[i] = locals[i].getNumber();
			locals[i].setNumber(i);
		}

		init(omitSSA);

		// restore local numbering
		for (int i = 0; i < N; i++) {
			locals[i].setNumber(oldNumbers[i]);
		}
		
		// GC help
		localRange = null;
		
		indexOfUnit.clear();
		indexOfUnit = null;
		
		Arrays.fill(universe, null);
		universe = null;
		
		if (Options.v().time())
			Timers.v().defsTimer.end();
	}

	private void init(boolean omitSSA) {
		indexOfUnit = new HashMap<Unit, Integer>(g.size());
		universe = new Unit[g.size()];
		localRange = new int[N + 1];
		
		@SuppressWarnings("unchecked")
		List<Unit>[] unitList = (List<Unit>[]) new List[N];

		for (int i = 0; i < N; i++) {
			unitList[i] = new ArrayList<Unit>();
		}

		boolean doFlowAnalsis = omitSSA;
				
		// collect all live def points
		for (Unit unit : g) {
			for (ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					int lno = l.getNumber();

					doFlowAnalsis |= !unitList[lno].isEmpty();
					unitList[lno].add(unit);
				}
			}
		}
		
		if (doFlowAnalsis) {
			localRange[0] = 0;
			for (int j = 0, i = 0; i < N; i++) {
				if (unitList[i].size() >= 2 || omitSSA) {
					for (Unit u : unitList[i]) {
						indexOfUnit.put(universe[j] = u, j);
						j++;
					}
				}
				localRange[i + 1] = j;
			}
			assignment = new FlowAssignment(unitList);
		} else {
			Arrays.fill(localRange, 0);
			assignment = new StaticSingleAssignment(unitList);
		}		
	}

	@Override
	public List<Unit> getDefsOfAt(Local l, Unit s) {
		return assignment.getDefsOfAt(l, s);
	}
	
	
	public Collection<Unit> getDefsOf(ValueBox valueBox) {
		return assignment.getDefsOf(valueBox);
	}
}