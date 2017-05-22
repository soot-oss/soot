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

import java.util.List;

import soot.Body;
import soot.Local;
import soot.Unit;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs.FlowAnalysisMode;

/**
 *   Provides an interface for querying for the definitions of a Local
 *   at a given Unit in a method.
 */
public interface LocalDefs
{
	static final public class Factory {
		private Factory() {}
		
		/**
		 * Creates a new LocalDefs analysis based on a {@code ExceptionalUnitGraph}
		 * 
		 * @see soot.toolkits.graph.ExceptionalUnitGraph#ExceptionalUnitGraph(Body)
		 * @see soot.validation.UsesValidator
		 * @param body
		 * @return a new LocalDefs instance
		 */
		public static LocalDefs newLocalDefs(Body body) {
			return newLocalDefs(body, false);
		}
		
		/**
		 * Creates a new LocalDefs analysis based on a {@code ExceptionalUnitGraph}
		 * If you don't trust the input you should set <code>expectUndefined</code>
		 * to <code>true</code>
		 * 
		 * @see soot.toolkits.graph.ExceptionalUnitGraph#ExceptionalUnitGraph(Body)
		 * @param body
		 * @param expectUndefinedUses if you expect uses of locals that are undefined
		 * @return a new LocalDefs instance
		 */
		public static LocalDefs newLocalDefs(Body body, boolean expectUndefined) {
			return newLocalDefs(new ExceptionalUnitGraph(body), expectUndefined);
		}
		
		/**
		 * Creates a new LocalDefs analysis based on a given {@code UnitGraph}
		 * 
		 * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
		 * @param graph the graph to work with
		 * @return a new LocalDefs instance
		 */
		public static LocalDefs newLocalDefs(UnitGraph graph) {
			return newLocalDefs(graph, false);
		}
		
		/**
		 * Creates a new LocalDefs analysis based on a given {@code UnitGraph}.
		 * If you don't trust the input you should set <code>expectUndefined</code>
		 * to <code>true</code>
		 * 
		 * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
		 * @see soot.validation.UsesValidator
		 * @param graph the graph to work with
		 * @param expectUndefined if you expect uses of locals that are undefined
		 * @return a new LocalDefs instance
		 */
		public static LocalDefs newLocalDefs(UnitGraph graph, boolean expectUndefined) {
			//return new SmartLocalDefs(graph, LiveLocals.Factory.newLiveLocals(graph)); 
			return new SimpleLocalDefs(graph, expectUndefined ? FlowAnalysisMode.OmitSSA
					: FlowAnalysisMode.Automatic);
		}
		
		/**
		 * Creates a new LocalDefs analysis based on a given {@code UnitGraph}.
		 * This analysis will be flow-insensitive, i.e., for a given local, it
		 * will always give all statements that ever write to that local regardless
		 * of potential redefinitions in between.
		 * 
		 * @see soot.toolkits.graph.UnitGraph#UnitGraph(Body)
		 * @see soot.validation.UsesValidator
		 * @param graph the graph to work with
		 * @return a new LocalDefs instance
		 */
		public static LocalDefs newLocalDefsFlowInsensitive(UnitGraph graph) {
			//return new SmartLocalDefs(graph, LiveLocals.Factory.newLiveLocals(graph)); 
			return new SimpleLocalDefs(graph, FlowAnalysisMode.FlowInsensitive);
		}
	}
	
    /**
     *   Returns the definition sites for a Local at a certain
     *   point (Unit) in a method. 
     *
     *	 You can assume this method never returns {@code null}.
     *
     *   @param l the Local in question.
     *   @param s  a unit that specifies the method context (location) 
     *             to query for the definitions of the Local. 
     *   @return a list of Units where the local is defined in the current
     *            method context. If there are no uses an empty list will returned.
     */
    public List<Unit> getDefsOfAt(Local l, Unit s);
    
    /**
     *   Returns the definition sites for a Local merged over all points
     *   in a method.
     *
     *	 You can assume this method never returns {@code null}.
     *
     *   @param l the Local in question.
     *   @return a list of Units where the local is defined in the current
     *            method context. If there are no uses an empty list will returned.
     */
    public List<Unit> getDefsOf(Local l);
    
}
