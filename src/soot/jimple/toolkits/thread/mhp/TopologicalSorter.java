package soot.jimple.toolkits.thread.mhp;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.tagkit.*;


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
	LinkedList sorter = new LinkedList();
	List visited = new ArrayList();
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
	public List sorter(){return sorter;}
	
	private void printSeq(List sequentNodes){
		System.out.println("topo sorter:");
		Iterator it = sequentNodes.iterator();
		while (it.hasNext()){
			
			Object o = it.next();
			if (o instanceof JPegStmt){
				Tag tag = (Tag)((JPegStmt)o).getTags().get(0);
				System.out.println(tag + " " + o );
			}
			else
				System.out.println(o);
		}
		
		System.out.println("end topo sorter:");
	}
}
