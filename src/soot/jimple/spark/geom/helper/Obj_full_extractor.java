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
import java.util.Collections;
import java.util.List;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.pag.Node;

/**
 * Extracts the contexts of object o that are related to pointer p if p points to o.
 * 
 * @author xiao
 *
 */
public class Obj_full_extractor extends PtSensVisitor
{
	public List<IntervalContextVar> icvList = new ArrayList<IntervalContextVar>();
	private List<IntervalContextVar> backupList = new ArrayList<IntervalContextVar>();
	
	@Override
	public void prepare()
	{
		icvList.clear();
	}
	
	@Override
	public boolean visit(Node var, long L, long R, int sm_int) 
	{
		// We first use [L, R) update the intervals already in the list.
		// Every round we collect the intervals for the same (p, o).
		// Therefore, we merge the intervals that intersect.
		for ( IntervalContextVar icv : icvList ) {
			if ( L <= icv.L ) {
				if ( R >= icv.L ) {
					icv.L = L;
					if ( R > icv.R ) icv.R = R;
					return true;
				}
			}
			else {
				if ( L <= icv.R ) {
					if ( R > icv.R ) icv.R = R;
					return true;
				}
			}
		}
		
		IntervalContextVar cvar = new IntervalContextVar( L, R, var );
		icvList.add(cvar);
		
		return true;
	}

	@Override
	public void finish() 
	{
		if ( icvList.size() == 0 ) return;
		
		// We generate disjoint intervals.
		Collections.sort(icvList);
		IntervalContextVar cur = icvList.get(0);
		for ( int i = 1; i < icvList.size(); ++i ) {
			IntervalContextVar icv = icvList.get(i);
			if ( icv.L <= cur.R )
				cur.R = icv.R;
			else {
				backupList.add(cur);
				cur = icv;
			}
		}
		backupList.add(cur);
		
		icvList.clear();
		
		// swap
		List<IntervalContextVar> temp = icvList;
		icvList = backupList;
		backupList = temp;
	}
}
