package soot.jimple.toolkits.thread.mhp;

import soot.util.*;
import java.util.*;


// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30


public class TopologicalSorter
{
	Chain chain;
	PegGraph pg;
	LinkedList<Object> sorter = new LinkedList<Object>();
	List<Object> visited = new ArrayList<Object>();
	public TopologicalSorter(Chain chain, PegGraph pg){
		this.chain = chain;
		this.pg = pg;
		go();
//		printSeq(sorter);
	}
	
	private void go(){
		Iterator it = chain.iterator();
		while (it.hasNext()){
			Object node = it.next();
			dfsVisit(node);
		}
	}
	
	private void dfsVisit(Object m){
		if( visited.contains( m ) ) return;
		visited.add( m );
		Iterator targetsIt = pg.getSuccsOf(m).iterator();
		while (targetsIt.hasNext()){
			Object target = targetsIt.next();	
			dfsVisit(target);
		}
		sorter.addFirst(m);
	}
	public List<Object> sorter(){return sorter;}
}
