/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.toolkits.scalar;

import soot.options.Options;
import soot.Timers;
import soot.Unit;
import soot.Local;
import soot.G;
import soot.Value;
import soot.ValueBox;

import java.util.List;

import soot.toolkits.graph.UnitGraph;

/**
 * Analysis that provides an implementation of the LiveLocals interface.
 */
public class SimpleLiveLocals implements LiveLocals {
	final FlowAnalysis<Unit, FlowSet<Local>> analysis;

	/**
	 * Computes the analysis given a UnitGraph computed from a method body. It
	 * is recommended that a ExceptionalUnitGraph (or similar) be provided for
	 * correct results in the case of exceptional control flow.
	 *
	 * @param graph a graph on which to compute the analysis.
	 * 
	 * @see ExceptionalUnitGraph
	 */
	public SimpleLiveLocals(UnitGraph graph) {
		if (Options.v().time())
			Timers.v().liveTimer.start();

		if (Options.v().verbose())
			G.v().out.println("[" + graph.getBody().getMethod().getName()
					+ "]     Constructing SimpleLiveLocals...");

		analysis = new Analysis(graph);

		if (Options.v().time())
			Timers.v().liveAnalysisTimer.start();

		analysis.doAnalysis();

		if (Options.v().time())
			Timers.v().liveAnalysisTimer.end();

		if (Options.v().time())
			Timers.v().liveTimer.end();
	}

	public List<Local> getLiveLocalsAfter(Unit s) {
		// ArraySparseSet returns a unbacked list of elements!
		return analysis.getFlowAfter(s).toList();
	}

	public List<Local> getLiveLocalsBefore(Unit s) {
		// ArraySparseSet returns a unbacked list of elements!
		return analysis.getFlowBefore(s).toList();
	}

	static class Analysis extends BackwardFlowAnalysis<Unit, FlowSet<Local>> {
		Analysis(UnitGraph g) {
			super(g);
		}

		@Override
		protected FlowSet<Local> newInitialFlow() {
			return new ArraySparseSet<Local>();
		}

		@Override
		protected void flowThrough(FlowSet<Local> in, Unit unit,
				FlowSet<Local> out) {
			in.copy(out);

			// Perform kill
			for (ValueBox box : unit.getDefBoxes()) {
				Value v = box.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					out.remove(l);
				}
			}

			// Perform generation
			for (ValueBox box : unit.getUseBoxes()) {
				Value v = box.getValue();
				if (v instanceof Local) {
					Local l = (Local) v;
					out.add(l);
				}
			}
		}

		@Override
		protected void mergeInto(Unit succNode, FlowSet<Local> inout,
				FlowSet<Local> in) {
			inout.union(in);
		}

		@Override
		protected void merge(FlowSet<Local> in1, FlowSet<Local> in2,
				FlowSet<Local> out) {
			in1.union(in2, out);
		}

		@Override
		protected void copy(FlowSet<Local> source, FlowSet<Local> dest) {
			source.copy(dest);
		}
	}
}
