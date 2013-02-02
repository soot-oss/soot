/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012 Richard Xiao
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
package soot.jimple.spark.geom.helper;

import java.util.List;
import soot.SootMethod;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.IVarAbstraction;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;

/**
 * This class provides the utilities to translate our numbering context representation to other commonly used representations.
 * Currently we only provide the translation to 1CFA.
 * 
 * @author xiao
 *
 */
public class ContextTranslator {
	
	/*
	 * The sets of the 1-cfa context sensitive pointers and objects.
	 */
	public static ZArrayNumberer<CallsiteContextVar> pts_1cfa_map = null;
	public static ZArrayNumberer<CallsiteContextVar> objs_1cfa_map = null;
	
	/**
	 * We map the geometric encoded result to the 1CFA form.
	 * @param ptsProvider
	 */
	public static void build_1cfa_map( GeomPointsTo ptsProvider )
	{
		if ( is_1cfa_built() ) return;
		
		pts_1cfa_map = new ZArrayNumberer<CallsiteContextVar>();
		objs_1cfa_map = new ZArrayNumberer<CallsiteContextVar>();
		
		// We first transform the pointers
		for ( IVarAbstraction pn : ptsProvider.pointers ) {
			if ( pn.willUpdate == false ) continue;
			Node v = pn.getWrappedNode();
			
			if ( v instanceof LocalVarNode ) {
				// Get the set of call edges for the enclosing function of the pointer
				LocalVarNode lvn = (LocalVarNode)v;
				SootMethod sm = lvn.getMethod();
				int sm_int = ptsProvider.getIDFromSootMethod(sm);
				List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
				
				for ( CgEdge ce : edges ) {
					CallsiteContextVar context_var = new CallsiteContextVar(ce, v);
					pts_1cfa_map.add(context_var);
				}
			}
			else {
				CallsiteContextVar context_var = new CallsiteContextVar(null, v);
				pts_1cfa_map.add(context_var);
			}
		}
		
		// we then transform the objects
		for ( IVarAbstraction pobj : ptsProvider.allocations ) {
			AllocNode obj = (AllocNode)pobj.getWrappedNode();
			SootMethod sm = obj.getMethod();
			
			if ( sm == null ) {
				CallsiteContextVar context_obj = new CallsiteContextVar(null, obj);
				objs_1cfa_map.add(context_obj);
			}
			else {
				int sm_int = ptsProvider.getIDFromSootMethod(sm);
				List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
				
				for ( CgEdge ce : edges ) {
					CallsiteContextVar context_obj = new CallsiteContextVar(ce, obj);
					objs_1cfa_map.add(context_obj);
				}
			}
		}
	}
	
	public static boolean is_1cfa_built()
	{
		return pts_1cfa_map != null && objs_1cfa_map != null;
	}
}
