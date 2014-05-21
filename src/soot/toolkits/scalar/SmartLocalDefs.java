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
import java.util.BitSet;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.DirectedGraph;
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

	protected static class StaticSingleAssignment implements LocalDefs {
		private final Map<Local, List<Unit>> localToDefs;

		protected StaticSingleAssignment(List<Unit>[] units, Local[] locals) {
			assert units.length == locals.length;

			localToDefs = new IdentityHashMap<Local, List<Unit>>(units.length);

			for (int i = 0; i < units.length; i++) {
				List<Unit> list = units[i];
				if (!list.isEmpty()) {
					assert list.size() == 1;
					list = singletonList(list.get(0));
					localToDefs.put(locals[i], list);
				}
			}
		}

		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			List<Unit> r = localToDefs.get(l);
			if (r == null) {
				r = emptyList();
			}
			return r;
		}

		public List<Unit> getDefsOfAt(ValueBox valueBox) {
			Value v = valueBox.getValue();
			if (v instanceof Local) {
				List<Unit> r = localToDefs.get(v);
				if (r != null) {
					return r;
				}
			}
			return emptyList();
		}
	}

	protected class DefaultAssignment implements LocalDefs {
		private final Map<ValueBox, List<Unit>> resultValueBoxes;

		protected DefaultAssignment(List<Unit>[] unitList) {
			// most of the units are like "a := b + c", 
			// or a invoke like "a := b.foo()"
			resultValueBoxes = new IdentityHashMap<ValueBox, List<Unit>>(g.size() * 3 + 1);

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
								resultValueBoxes.put(useBox, list);
							}
						} else {
							int j = 0;
							BitSet reaching = reachingAnalysis.getFlowBefore(s);
							for (int i = to; (i = reaching
									.previousSetBit(i - 1)) >= from;) {
								buffer[j++] = units[i];
							}

							if (j > 0) {
								List<Unit> list = (j == 1) 
									? singletonList(buffer[0])
									: unmodifiableList(asList(copyOf(buffer, j)))
									;
									
								resultValueBoxes.put(useBox, list);
							}
						}
					}
				}
			}
		}

		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			for (ValueBox useBox : s.getUseBoxes()) {
				if (l == useBox.getValue()) {
					return getDefsOfAt(useBox);
				}
			}
			return emptyList();
		}

		public List<Unit> getDefsOfAt(ValueBox valueBox) {
			List<Unit> r = resultValueBoxes.get(valueBox);
			if (r == null) {
				r = emptyList();
			}
			return r;
		}
	}

	private class LocalDefsAnalysis extends ForwardFlowAnalysis<Unit, BitSet> {
		private LocalDefsAnalysis() {
			super(g);
			doAnalysis();
		}

		@Override
		protected void flowThrough(BitSet in, Unit unit, BitSet out) {
			// copy everything that is live
			out.clear();
			if (!in.isEmpty()) {
				for (Local l : liveLocals.getLiveLocalsAfter(unit)) {
					int i = l.getNumber();
					int j = i + 1;
					out.set(localRange[i], localRange[j]);
				}
				out.and(in);
			}

			if (unit.getDefBoxes().isEmpty()) {
				return;
			}

			// reassign all definitions
			Integer idx = indexOfUnit.get(unit);
			if (idx != null) {
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

		@Override
		protected BitSet entryInitialFlow() {
			return newInitialFlow();
		}

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
	private LocalDefs localDefs;

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

		// reassign local numbers
		int[] oldNumbers = new int[n];
		for (int i = 0; i < n; i++) {
			oldNumbers[i] = locals[i].getNumber();
			locals[i].setNumber(i);
		}

		init(locals);

		// restore local numbering
		for (int i = 0; i < n; i++) {
			locals[i].setNumber(oldNumbers[i]);
		}

		if (Options.v().time())
			Timers.v().defsTimer.end();
	}

	@SuppressWarnings("unchecked")
	private void init(Local[] locals) {
		indexOfUnit = new IdentityHashMap<Unit, Integer>(g.size());
		units = new Unit[g.size()];
		localRange = new int[n + 1];

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

		localDefs = (localRange[n] == 0) 
			? new StaticSingleAssignment(unitList, locals) 
			: new DefaultAssignment(unitList)
			;
	}

	@Override
	public List<Unit> getDefsOfAt(Local l, Unit s) {
		return localDefs.getDefsOfAt(l, s);
	}
}
