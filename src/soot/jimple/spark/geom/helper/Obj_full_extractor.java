package soot.jimple.spark.geom.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.pag.Node;

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
