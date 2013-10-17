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

import soot.PointsToSet;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.PointsToSetInternal;

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

	@Override
	public boolean hasIntersection(PtSensVisitor other) 
	{
		if ( !(other instanceof Obj_full_extractor) )
			return false;
		
		Obj_full_extractor o = (Obj_full_extractor)other;
		
		for ( IntervalContextVar icv1 : o.icvList ) {
			Node obj1 = icv1.var;
			long L1 = icv1.L;
			long R1 = icv1.R;
			
			for ( IntervalContextVar icv2 : icvList ) {
				Node obj2 = icv2.var;
				if ( obj1 != obj2 ) continue;
				
				long L2 = icv2.L;
				long R2 = icv2.R;
				if ( L2 >= L1 && L2 < R1 ) return true;
				if ( L1 >= L2 && L2 < R2 ) return true;
			}
		}
		
		return false;
	}

	@Override
	public PointsToSet toSparkCompatiableResult(VarNode vn) 
	{
		PointsToSetInternal ptset = vn.makeP2Set();
		
		for ( IntervalContextVar icv : icvList ) {
			ptset.add(icv.var);
		}
		
		return ptset;
	}

	@Override
	public void debugPrint() 
	{
		for ( IntervalContextVar icv : icvList ) {
			Node obj = icv.var;
			long L = icv.L;
			long R = icv.R;
			System.out.printf("\t<%s, %d, %d>\n", obj.toString(), L, R);
		}
	}
}