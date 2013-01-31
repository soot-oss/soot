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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.Scene;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.geomPA.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.ZArrayNumberer;
import soot.jimple.spark.pag.Node;

/**
 * Translate the numbered context to callsite context.
 * @author xiao
 *
 */
public class Obj_1cfa_extractor extends PtSensVisitor
{
	public Set<CallsiteContextVar> outList = new HashSet<CallsiteContextVar>();
	private CallsiteContextVar cobj = new CallsiteContextVar();
	
	private ZArrayNumberer<CallsiteContextVar> all_objs = ContextTranslator.objs_1cfa_map;
	private GeomPointsTo ptsProvider = (GeomPointsTo)Scene.v().getPointsToAnalysis();
	
	@Override
	public void prepare()
	{
		outList.clear();
	}
	
	@Override
	public boolean visit(Node var, long L, long R, int sm_int) 
	{
		cobj.var = var;
		List<CgEdge> edges = ptsProvider.getCallEdgesInto(sm_int);
		
		if ( edges != null ) {
			for ( CgEdge e : edges ) {
				// We compute the context range for this call edge
				long rangeL = e.map_offset;
				long rangeR = rangeL + ptsProvider.max_context_size_block[e.s];
				
				// We compute if [rangeL, rangeR) intersects with [objL, objR) 
				if ( L < rangeR && rangeL < R ) {
					cobj.context = e;
					outList.add( all_objs.searchFor(cobj) );
				}
			}
		}
		else {
			cobj.context = null;
			outList.add( all_objs.searchFor(cobj) );
			return false;
		}
		
		return true;
	}

	@Override
	public void finish() 
	{
		// nothing to do
	}
}