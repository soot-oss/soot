/* Soot - a J*va Optimization Framework
 * Copyright (C) 2013 Richard Xiao
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
import java.util.List;
import soot.jimple.spark.geom.dataRep.SimpleInterval;

/**
 * Manage context intervals.
 * 
 * @author xiao
 *
 */
public class ContextsCollector 
{
	public List<SimpleInterval> bars;
	
	protected List<SimpleInterval> backupList;
	protected SimpleInterval tmp_si;
	
	/*
	 * We are creating a performance-precision tunable container.
	 * When there are more than nBudget in the container, we merge them and create a super containing interval.
	 * nBudget = -1 means the intervals are never merged.
	 */
	protected int nBudget = -1;
	
	public ContextsCollector() 
	{ 
		bars = new ArrayList<SimpleInterval>();
		backupList = new ArrayList<SimpleInterval>();
		tmp_si = new SimpleInterval();
	}
	
	public int getBudget()
	{
		return nBudget;
	}
	
	public int setBudget(int n)
	{
		int original = nBudget;
		nBudget = n;
		return original;
	}
	
	public boolean insert(long L, long R)
	{
		backupList.clear();
		
		// We search the list and merge the intersected intervals
		tmp_si.L = L;
		tmp_si.R = R;
		long minL = L;
		long maxR = R;

		for (SimpleInterval old_si : bars) {
			if (old_si.contains(tmp_si)) {
				// We keep the context intervals disjoint
				return false;
			}
			if (!tmp_si.merge(old_si)) {
				if ( old_si.L < minL ) minL = old_si.L;
				if ( old_si.R > maxR ) maxR = old_si.R;
				backupList.add(old_si);
			}
		}

		// We switch the backup list with the original list
		List<SimpleInterval> tmpList = backupList;
		backupList = bars;
		bars = tmpList;
		SimpleInterval new_si = new SimpleInterval( tmp_si );
		bars.add( new_si );
		
		// Merge the intervals
		if ( nBudget != -1 && bars.size() > nBudget ) {
			bars.clear();
			new_si.L = minL;
			new_si.R = maxR;
			bars.add(new_si);
		}
		
		return true;
	}
	
	public void clear()
	{
		bars.clear();
	}
}
