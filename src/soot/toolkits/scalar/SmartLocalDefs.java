/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Ondrej Lhotak
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

import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.StronglyConnectedComponentsFast;
import soot.toolkits.graph.UnitGraph;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.singletonList;
import static java.util.Collections.emptyList;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOf;

/**
 * Analysis that provides an implementation of the LocalDefs interface.
 */
public class SmartLocalDefs implements LocalDefs {
	
	abstract static protected class Assignment implements LocalDefs {
		protected static class Entry {
			List<Unit> units;
			Local local;
			
			Entry(List<Unit> units, Local local) {
				this.units = units;
				this.local = local;
			}
		}
		
		protected final Map<ValueBox, Entry> resultValueBoxes;
	
		protected Assignment(int size) {
			resultValueBoxes = new HashMap<ValueBox, Entry>(size);
		}

		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			for (ValueBox useBox : s.getUseBoxes()) {
				if (l == useBox.getValue()) {
					Entry e = resultValueBoxes.get(useBox);
					// check if local was exchanged!
					if (e != null && l == e.local) {
						return e.units;
					}
				}
			}
			return emptyList();
		}
		
		public List<Unit> getDefsOf(ValueBox valueBox) {
			Entry e = resultValueBoxes.get(valueBox);
			if (e == null) {
				return emptyList();
			}
			return e.units;
		}
	}
	
	protected class StaticSingleAssignment extends Assignment {
		protected StaticSingleAssignment(List<Unit>[] unitList) {	
			// most of the units are like "a := b + c", 
			// or a invoke like "a := b.foo()"
			super(g.size() * 3 + 1);
			assert localRange[n] == 0;
			
			for (Unit s : g) {
				for (ValueBox useBox : s.getUseBoxes()) {
					Value v = useBox.getValue();
					if (v instanceof Local) {
						Local l = (Local) v;
						int lno = l.getNumber();

						List<Unit> list = unitList[lno];
						if (!list.isEmpty()) {
							list = singletonList(list.get(0));
							resultValueBoxes.put(useBox, new Entry(list, l));	
						}					
					}
				}
			}
		}
	}

	protected class DefaultAssignment extends Assignment {
		protected DefaultAssignment(List<Unit>[] unitList) {
			// most of the units are like "a := b + c", 
			// or a invoke like "a := b.foo()"
			super(g.size() * 3 + 1);

			LocalDefsAnalysis reachingAnalysis = new LocalDefsAnalysis();
			Unit[] buffer = new Unit[localRange[n]];

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
							List<Unit> list = unitList[lno];
							if (!list.isEmpty()) {
								list = singletonList(list.get(0));
								resultValueBoxes.put(useBox, new Entry(list, l));
							}	
						} else {
							int j = 0;
							BitSet reaching = reachingAnalysis.getFlowBefore(s);
							for (int i = reaching.nextSetBit(from); (i < to) && (i >= 0); i = reaching.nextSetBit(i+1)) {
								buffer[j++] = units[i];
							}

							if (j > 0) {
								List<Unit> list = (j == 1) 
									? singletonList(buffer[0])
									: unmodifiableList(asList(copyOf(buffer, j)))
									;									
								resultValueBoxes.put(useBox, new Entry(list, l));
							}		
						}				
					}
				}
			}
		}
	}

	private class LocalDefsAnalysis extends ForwardFlowAnalysis<Unit, BitSet> {
		private LocalDefsAnalysis() {
			super(g);
			doAnalysis();
		}
				
		@Override
		protected void mergeFilter(Unit unit, BitSet inout) {
			BitSet mask = newInitialFlow();
			for (Local l : liveLocals.getLiveLocalsBefore(unit)) {
				int i = l.getNumber();
				int j = i + 1;
				mask.set(localRange[i], localRange[j]);
			}
			inout.and(mask);	
		}
		
		@Override
		protected void flowThrough(BitSet in, Unit unit, BitSet out) {
			// copy everything that is live
			out.clear();
			
			if (in.isEmpty()) {		
				initFlowAfter(unit, out);
				return;
			}			
			/*
			for (Local l : liveLocals.getLiveLocalsAfter(unit)) {
				int i = l.getNumber();
				int j = i + 1;
				out.set(localRange[i], localRange[j]);
			}
			out.and(in);
			*/
			out.or(in);
			
			Integer idx = indexOfUnit.get(unit);
			if (idx != null) {
				// reassign all definitions
				for (ValueBox vb : unit.getDefBoxes()) {
					Value v = vb.getValue();
					if (v instanceof Local) {
						int lno = ((Local) v).getNumber();
	
						int from = localRange[lno];
						int to = localRange[lno + 1];
						assert from <= to;
	
						if (from < to) {
							out.clear(from, to);
							out.set(idx);
						}
					}
				}
			}			
		}

		@Override
		protected void initFlowAfter(Unit unit, BitSet flow) {
			Integer idx = indexOfUnit.get(unit);
			if (idx != null) {
				flow.set(idx);
			}
		}

		@Override
		protected void mergeInto(Unit succNode, BitSet inout, BitSet in) {
			inout.or(in);
		}

		@Override
		protected void copy(BitSet source, BitSet dest) {
			dest.clear();
			dest.or(source);
		}

		@Override
		protected BitSet newInitialFlow() {
			return new BitSet(localRange[n]);
		}
/*
		@Override
		protected BitSet entryInitialFlow() {
			return newInitialFlow();
		}
*/
		@Override
		protected void merge(BitSet in1, BitSet in2, BitSet out) {
			throw new RuntimeException("should never be called");
		}
	}

	final private DirectedGraph<Unit> g;
	final int n;

	private int[] localRange;

	private Unit[] units;
	private Map<Unit, Integer> indexOfUnit;

	private LiveLocals liveLocals;
	private Assignment assignment;

	public SmartLocalDefs(UnitGraph graph, LiveLocals live) {
		this(graph, live, graph.getBody().getLocals());
	}

	protected SmartLocalDefs(DirectedGraph<Unit> graph, LiveLocals live, Collection<Local> locals) {
		this(graph, live, locals.toArray(new Local[locals.size()]));
	}

	protected SmartLocalDefs(DirectedGraph<Unit> graph, LiveLocals live, Local... locals) {
		if (Options.v().time())
			Timers.v().defsTimer.start();

		this.g = graph;
		this.n = locals.length;
		this.liveLocals = live;

		/*
		List<List<Unit>> scc = (new StronglyConnectedComponentsFast<Unit>(graph)).getTrueComponents();
		if ( !scc.isEmpty() ) {
			System.out.println(graph.size());	
			System.out.println("==============================");				
			for (List<Unit> comp : scc){		
				System.out.println(comp.size());// +" // "+live.getLiveLocalsBefore(comp.get(0))+" // "+live.getLiveLocalsBefore(comp.get(0)).size()+" of "+locals.length);
			}
			System.out.println();
		}*/


		// reassign local numbers
		int[] oldNumbers = new int[n];
		for (int i = 0; i < n; i++) {
			oldNumbers[i] = locals[i].getNumber();
			locals[i].setNumber(i);
		}

		init();

		// restore local numbering
		for (int i = 0; i < n; i++) {
			locals[i].setNumber(oldNumbers[i]);
		}
		
		// GC help
		localRange = null;
		liveLocals = null;
		
		indexOfUnit.clear();
		indexOfUnit = null;
		
		Arrays.fill(units, null);
		units = null;
		
		if (Options.v().time())
			Timers.v().defsTimer.end();
	}

	private void init() {
		indexOfUnit = new HashMap<Unit, Integer>(g.size());
		units = new Unit[g.size()];
		localRange = new int[n + 1];
		
		@SuppressWarnings("unchecked")
		List<Unit>[] unitList = (List<Unit>[]) new List[n];

		for (int i = 0; i < n; i++) {
			unitList[i] = new ArrayList<Unit>();
		}

		// collect all live def points
		for (Unit unit : g) {
			for (ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					int lno = l.getNumber();

					// only add local if it is used
					if (liveLocals.getLiveLocalsAfter(unit).contains(l)) {
						unitList[lno].add(unit);
					}
				}
			}
		}
		
		// if a variable reaches at least one head node, it can be undefined
		BitSet undefinedLocals = new BitSet(n);
		for (Unit unit : g.getHeads()) {
			for (Local l : liveLocals.getLiveLocalsBefore(unit)) {
				undefinedLocals.set(l.getNumber());
			}
		}

		boolean isDefaultAssignment = !undefinedLocals.isEmpty();
		
		if (!isDefaultAssignment) {
			for (List<Unit> l : unitList) {
				if (l.size() >= 2) {
					isDefaultAssignment = true;
					break;
				}
			}
		}

		if (isDefaultAssignment) {
			localRange[0] = 0;
			for (int j = 0, i = 0; i < n; i++) {
				if (unitList[i].size() >= 2 || undefinedLocals.get(i)) {
					for (Unit u : unitList[i]) {
						indexOfUnit.put(units[j] = u, j);
						j++;
					}
				}
				localRange[i + 1] = j;
			}
			assignment = new DefaultAssignment(unitList);
		} else {
			Arrays.fill(localRange, 0);
			assignment = new StaticSingleAssignment(unitList);
		}		
	}

	@Override
	public List<Unit> getDefsOfAt(Local l, Unit s) {
		return assignment.getDefsOfAt(l, s);
	}
	
	
	public List<Unit> getDefsOf(ValueBox valueBox) {
		return assignment.getDefsOf(valueBox);
	}
}
