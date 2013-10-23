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

import java.util.ArrayList;
import java.util.List;
import soot.Scene;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.Node;

/**
 * Translate the numbered context to 1-CFA callsite context.
 * @author xiao
 *
 */
public class Obj_1cfa_extractor 
	extends PtSensVisitor<CallsiteContextVar>
{
	private ZArrayNumberer<CallsiteContextVar> all_objs;
	private CallsiteContextVar cobj = new CallsiteContextVar();
	private GeomPointsTo ptsProvider = null;
	
	public Obj_1cfa_extractor()
	{
		ptsProvider = (GeomPointsTo)Scene.v().getPointsToAnalysis();
		
		if ( !ContextTranslator.is_1cfa_built() ) {
			ContextTranslator.build_1cfa_map(ptsProvider);
		}
		
		all_objs = ContextTranslator.objs_1cfa_map;
	}
	
	@Override
	public boolean visit(Node var, long L, long R, int sm_int) 
	{
		List<CallsiteContextVar> resList = tableView.get(var);
		if ( resList == null ) {
			resList = new ArrayList<CallsiteContextVar>();
			tableView.put(var, resList);
		}
	
		cobj.var = var;
		List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
		CallsiteContextVar new_ccv = null;
		
		if ( edges != null ) {
			for ( CgEdge e : edges ) {
				// We compute the context range for this call edge
				long rangeL = e.map_offset;
				long rangeR = rangeL + ptsProvider.max_context_size_block[e.s];
				
				// We compute if [rangeL, rangeR) intersects with [L, R) 
				if ( L < rangeR && rangeL < R ) {
					cobj.context = e;
					new_ccv = all_objs.searchFor(cobj);
					
				}
			}
		}
		else {
			cobj.context = null;
			new_ccv = all_objs.searchFor(cobj);
		}
		
		if ( resList.contains(new_ccv) ) return false;
		resList.add( new_ccv );
		return true;
	}
}