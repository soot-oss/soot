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
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.pag.Node;

/**
 * Extracts the full context sensitive points-to result.
 * 
 * @author xiao
 *
 */
public class Obj_full_extractor 
	extends PtSensVisitor<IntervalContextVar>
{
	private List<IntervalContextVar> backupList = new ArrayList<IntervalContextVar>();
	private IntervalContextVar tmp_icv = new IntervalContextVar();
	
	@Override
	public boolean visit(Node var, long L, long R, int sm_int) 
	{
		List<IntervalContextVar> resList = tableView.get(var);
		
		if ( resList == null ) {
			// The first time this object is inserted
			resList = new ArrayList<IntervalContextVar>();
			tableView.put(var, resList);
		}
		else {
			// We search the list and merge the context sensitive objects
			backupList.clear();
			tmp_icv.L = L;
			tmp_icv.R = R;
			
			for ( IntervalContextVar old_cv : resList ) {
				if ( old_cv.contains(tmp_icv) ) {
					/*
					 * Becase we keep the intervals disjoint.
					 * It's impossible the passed in interval is contained in an interval and intersects with another interval.
					 * So we can directly return.
					 */
					return false;
				}
				if ( !tmp_icv.merge(old_cv) )
					backupList.add(old_cv);
			}
			
			// We switch the backup list with the original list
			List<IntervalContextVar> tmpList = backupList;
			backupList = resList;
			resList = tmpList;
			
			// Write back
			L = tmp_icv.L;
			R = tmp_icv.R;
		}
		
		IntervalContextVar icv = new IntervalContextVar( L, R, var );
		resList.add(icv);
		return true;
	}
}