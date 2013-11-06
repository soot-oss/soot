/* Soot - a J*va Optimization Framework
 * Copyright (C) 2012, 2013 Richard Xiao
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
package soot.jimple.spark.geom.dataMgr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.print.attribute.standard.Finishings;

import soot.Scene;
import soot.jimple.spark.geom.dataRep.CallsiteContextVar;
import soot.jimple.spark.geom.dataRep.CgEdge;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.helper.ContextTranslator;
import soot.jimple.spark.geom.utils.ZArrayNumberer;
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
	private Set<CallsiteContextVar> added_objs = null;
	
	
	public Obj_1cfa_extractor()
	{
		if ( !ContextTranslator.is_1cfa_built() ) {
			ContextTranslator.build_1cfa_map(ptsProvider);
		}
		
		all_objs = ContextTranslator.objs_1cfa_map;
		added_objs = new HashSet<CallsiteContextVar>();
	}
	
	@Override
	public void finish()
	{
		added_objs.clear();
		super.finish();
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
		boolean added = false;
		
		if ( edges != null ) {
			for ( CgEdge e : edges ) {
				// We compute the context range for this call edge
				long rangeL = e.map_offset;
				long rangeR = rangeL + ptsProvider.max_context_size_block[e.s];
				
				// We compute if [rangeL, rangeR) intersects with [L, R) 
				if ( L < rangeR && rangeL < R ) {
					cobj.context = e;
					added = added || addToResultSet(resList);
				}
			}
		}
		else {
			cobj.context = null;
			added = added || addToResultSet(resList);
		}
		
		return added;
	}
	
	private boolean addToResultSet(List<CallsiteContextVar> resList)
	{
		CallsiteContextVar new_ccv = all_objs.searchFor(cobj);
		
		if ( new_ccv != null ) {
			if ( !added_objs.contains(new_ccv) ) {
				resList.add(new_ccv);
				added_objs.add(new_ccv);
				return true;
			}
		}
		
		return false;
	}
}