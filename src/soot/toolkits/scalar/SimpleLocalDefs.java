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
	static private class StaticSingleAssignment implements LocalDefs {
		final Map<Local, Integer> locals;
		final List<Unit>[] units;
		StaticSingleAssignment(Map<Local,Integer> internalLocal, List<Unit>[] unitList) {
			assert internalLocal.size() <= unitList.length;
			
			// do not recreate the mapping
			locals = internalLocal;
			units = unitList;
		}

		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			Integer lno = locals.get(l);
			if (lno == null)
				return emptyList();
			
			return unmodifiableList(units[lno]);
		}
	}

	static private class FlowAssignment extends ForwardFlowAnalysis<Unit, FlowAssignment.FlowBitSet> implements LocalDefs {
		class FlowBitSet extends BitSet {
			private static final long serialVersionUID = -8348696077189400377L;
			
			FlowBitSet () {
				super(universe.length);
			}
		    
		    List<Unit> asList(int fromIndex, int toIndex) {
		    	BitSet bits = this;
		    	if (fromIndex < 0 || toIndex > universe.length || toIndex < fromIndex)
		    		throw new IndexOutOfBoundsException();

		    	if (fromIndex == toIndex) {
		    		return emptyList();
		    	}
		    	
		    	if (fromIndex == toIndex - 1) {
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
		    	
		        List<Unit> elements = new ArrayList<Unit>(toIndex-i);
		                		
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
		
		final Map<Local, Integer> locals;
		final List<Unit>[] unitList;
		final int[] localRange;
		final Unit[] universe;
		
		private Map<Unit, Integer> indexOfUnit;
		FlowAssignment(DirectedGraph<Unit> graph, int locals, Map<Local,Integer> internalLocal, List<Unit>[] unitList, int units, boolean omitSSA) {
			super(graph);
			this.locals = internalLocal;
			this.unitList = unitList;
			
			universe = new Unit[units];
			indexOfUnit = new HashMap<Unit, Integer>((units*2)/3+7);
			
			localRange = new int[locals + 1];		
			localRange[0] = 0;			
			for (int j = 0, i = 0; i < locals; i++) {
				if (unitList[i].size() >= 2 || omitSSA) {
					for (Unit u : unitList[i]) {
						indexOfUnit.put(universe[j] = u, j);
						j++;
					}
				}
				localRange[i + 1] = j;
			}
			assert localRange[locals] == units;
			
			doAnalysis();
			
			indexOfUnit.clear();
			indexOfUnit = null;
		}
		
		@Override
		public List<Unit> getDefsOfAt(Local l, Unit s) {
			Integer lno = locals.get(l);
			if (lno == null)
				return emptyList();
			
			int from = localRange[lno];
			int to = localRange[lno + 1];
			assert from <= to;
			
			if (from == to) {
				assert unitList[lno] != null;
				return unmodifiableList(unitList[lno]);
			}
			
			return getFlowBefore(s).asList(from, to);
		}

		@Override
		protected boolean omissible(Unit u) {
			// avoids temporary creation of iterators (more like micro-tuning)
			if (u.getDefBoxes().isEmpty())
				return true;
			for (ValueBox vb : u.getDefBoxes()) {
				Value v = vb.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					int lno = l.getNumber();					
					return (localRange[lno] == localRange[lno + 1]);
				}
			}			
			return true;
		}
		
		@Override
		protected void flowThrough(FlowBitSet in, Unit unit, FlowBitSet out) {
			copy(in, out);
			
			Integer i = indexOfUnit.get(unit);
			if (i == null)
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
			out.set(i);
		}

		@Override
		protected void mergeInto(Unit succNode, FlowBitSet inout, FlowBitSet in) {
			inout.or(in);
		}

		@Override
		protected void copy(FlowBitSet source, FlowBitSet dest) {
			if (dest == source)
				return;
			dest.clear();
			dest.or(source);
		}

		@Override
		protected FlowBitSet newInitialFlow() {
			return new FlowBitSet();
		}

		@Override
		protected void merge(FlowBitSet in1, FlowBitSet in2, FlowBitSet out) {
			throw new UnsupportedOperationException("should never be called");
		}
	}
	
	private LocalDefs def;

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

		final int N = locals.length;
		
		// reassign local numbers
		int[] oldNumbers = new int[N];
		for (int i = 0; i < N; i++) {
			oldNumbers[i] = locals[i].getNumber();
			locals[i].setNumber(i);
		}

		init(graph, locals, omitSSA);

		// restore local numbering
		for (int i = 0; i < N; i++) {
			locals[i].setNumber(oldNumbers[i]);
		}
				
		if (Options.v().time())
			Timers.v().defsTimer.end();
	}

	private void init(DirectedGraph<Unit> graph, Local[] locals, boolean omitSSA) {
		int N = locals.length;
		Map<Local,Integer> internalLocal = new HashMap<Local, Integer>((N*3)/2+7);		
		
		@SuppressWarnings("unchecked")
		List<Unit>[] unitList = (List<Unit>[]) new List[N];

		Arrays.fill(unitList, emptyList());
		
		boolean doFlowAnalsis = omitSSA;
				
		int units = 0;
		
		// collect all def points
		for (Unit unit : graph) {
			for (ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					int lno = l.getNumber();
					
					switch (unitList[lno].size()) {
					case 0:
						unitList[lno] = singletonList(unit);
						internalLocal.put(l, lno);
						if (omitSSA)
							units++;
						break;
					case 1:
						if (!omitSSA)
							units++;
						unitList[lno] = new ArrayList<Unit>(unitList[lno]);
						doFlowAnalsis = true;
						// fallthrough
					default:
						unitList[lno].add(unit);
						units++;
						break;
					}					
				}
			}
		}
		
		if (doFlowAnalsis) {
			def = new FlowAssignment(graph, N, internalLocal, unitList, units, omitSSA);
		} else {
			def = new StaticSingleAssignment(internalLocal, unitList);
		}		
	}

	@Override
	public List<Unit> getDefsOfAt(Local l, Unit s) {
		return def.getDefsOfAt(l, s);
	}
}