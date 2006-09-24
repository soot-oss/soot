/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
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

package soot.dava.internal.asg;

import soot.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.toolkits.graph.*;

public class AugmentedStmtGraph implements DirectedGraph
{
    private HashMap binding, original2clone;
    private IterableSet aug_list, stmt_list;
    private List bheads, btails, cheads, ctails;


    public AugmentedStmtGraph( AugmentedStmtGraph other)
    {
	this();

	HashMap old2new = new HashMap();

	Iterator it = other.aug_list.iterator();
	while (it.hasNext()) {
	    AugmentedStmt oas = (AugmentedStmt) it.next();
	    Stmt s = oas.get_Stmt();

	    AugmentedStmt nas = new AugmentedStmt( s);
	    aug_list.add( nas);
	    stmt_list.add( s);
	    binding.put( s, nas);

	    old2new.put( oas, nas);
	}

	
	it = other.aug_list.iterator();
	while (it.hasNext()) {
	    AugmentedStmt oas = (AugmentedStmt) it.next();
	    AugmentedStmt nas = (AugmentedStmt) old2new.get( oas);

	    Iterator pit = oas.bpreds.iterator();
	    while (pit.hasNext())
		nas.bpreds.add( old2new.get( pit.next()));
	    if (nas.bpreds.isEmpty())
		bheads.add( nas);

	    pit = oas.cpreds.iterator();
	    while (pit.hasNext())
		nas.cpreds.add( old2new.get( pit.next()));
	    if (nas.cpreds.isEmpty())
		cheads.add( nas);

	    Iterator sit = oas.bsuccs.iterator();
	    while (sit.hasNext())
		nas.bsuccs.add( old2new.get( sit.next()));
	    if (nas.bsuccs.isEmpty())
		btails.add( nas);

	    sit = oas.csuccs.iterator();
	    while (sit.hasNext())
		nas.csuccs.add( old2new.get( sit.next()));
	    if (nas.csuccs.isEmpty())
		ctails.add( nas);
	}

	find_Dominators();
    }
   
    public AugmentedStmtGraph( BriefUnitGraph bug, TrapUnitGraph cug)
    {
	this();

	Dava.v().log( "AugmentedStmtGraph::AugmentedStmtGraph() - cug.size() = " + cug.size());

	// make the augmented statements
	Iterator it = cug.iterator();
	while (it.hasNext()) {
	    Stmt s = (Stmt) it.next();
	    add_StmtBinding( s, new AugmentedStmt( s));
	}

	// make the list of augmented statements in pseudo topological order!
        it = (new PseudoTopologicalOrderer()).newList( cug, false ).iterator();
	while (it.hasNext()) {
	    Stmt s = (Stmt) it.next();
	    aug_list.add( get_AugStmt( s));
	    stmt_list.add( s);
	}

	// now that we've got all the augmented statements, mirror the statement graph
	it = aug_list.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();
	    
	    mirror_PredsSuccs( as, bug);
	    mirror_PredsSuccs( as, cug);
	}

	find_Dominators();
    }

    public AugmentedStmtGraph()
    {
        binding  = new HashMap();
	original2clone = new HashMap();
	aug_list = new IterableSet();
	stmt_list = new IterableSet();

	bheads = new LinkedList();
	btails = new LinkedList();
	cheads = new LinkedList();
	ctails = new LinkedList();
    }

    public void add_AugmentedStmt( AugmentedStmt as)
    {
	Stmt s = as.get_Stmt();

	aug_list.add( as);
	stmt_list.add( s);
	
	add_StmtBinding( s, as);

	if (as.bpreds.isEmpty())
	    bheads.add( as);
	
	if (as.cpreds.isEmpty())
	    cheads.add( as);
	
	if (as.bsuccs.isEmpty())
	    btails.add( as);
	
	if (as.csuccs.isEmpty())
	    ctails.add( as);

	check_List( as.bpreds, btails);
	check_List( as.bsuccs, bheads);
	check_List( as.cpreds, ctails);
	check_List( as.csuccs, cheads);
    }

    public boolean contains( Object o)
    {
	return aug_list.contains( o);
    }

    public AugmentedStmt get_CloneOf( AugmentedStmt as)
    {
	return (AugmentedStmt) original2clone.get( as);
    }

    public int size()
    {
	return aug_list.size();
    }

    private void check_List( List psList, List htList)
    {
	Iterator it = psList.iterator();
	while (it.hasNext()) {
	    Object o = it.next();

	    if (htList.contains( o))
		htList.remove(o);
	}
    }


    public void calculate_Reachability( AugmentedStmt source, HashSet blockers, AugmentedStmt dominator)
    {
	if (blockers == null)
	    throw new RuntimeException( "Tried to call AugmentedStmtGraph:calculate_Reachability() with null blockers.");

	if (source == null)
	    return;

	LinkedList worklist = new LinkedList();
	HashSet touchSet = new HashSet();
	
	worklist.addLast( source);
	touchSet.add( source);
	
	while (worklist.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) worklist.removeFirst();
	    
	    Iterator sit = as.csuccs.iterator();
	    while (sit.hasNext()) {
		AugmentedStmt sas = (AugmentedStmt) sit.next();
		
		if ((touchSet.contains( sas)) || (sas.get_Dominators().contains( dominator) == false))
		    continue;
		
		touchSet.add( sas);
		
		IterableSet reachers = sas.get_Reachers();
		
		if (reachers.contains( source) == false)
		    reachers.add( source);
		
		if (blockers.contains( sas) == false)
		    worklist.addLast( sas);
	    }
	}
    }

    public void calculate_Reachability( Collection sources, HashSet blockers, AugmentedStmt dominator)
    {
	Iterator srcIt = sources.iterator();
	while (srcIt.hasNext())
	    calculate_Reachability( (AugmentedStmt) srcIt.next(), blockers, dominator);
    }

    public void calculate_Reachability( AugmentedStmt source, AugmentedStmt blocker, AugmentedStmt dominator)
    {
	HashSet h = new HashSet();

	h.add( blocker);

	calculate_Reachability( source, h, dominator);
    }
    
    public void calculate_Reachability( Collection sources, AugmentedStmt blocker, AugmentedStmt dominator)
    {
	HashSet h = new HashSet();
	
	h.add( blocker);
	
	calculate_Reachability( sources, h, dominator);
    }

    public void calculate_Reachability( AugmentedStmt source, AugmentedStmt dominator)
    {
	calculate_Reachability( source, new HashSet(), dominator);
    }

    public void calculate_Reachability( Collection sources, AugmentedStmt dominator)
    {
	calculate_Reachability( sources, new HashSet(), dominator);
    }

    public void calculate_Reachability( AugmentedStmt source)
    {
	calculate_Reachability( source, null);
    }

    public void calculate_Reachability( Collection sources)
    {
	calculate_Reachability( sources, null);
    }

    public void add_StmtBinding( Stmt s, AugmentedStmt as)
    {
	binding.put( s, as);
    }

    public AugmentedStmt get_AugStmt( Stmt s)
    {
	AugmentedStmt as = (AugmentedStmt) binding.get( s);
	if (as == null)
	    throw new RuntimeException( "Could not find augmented statement for: " + s.toString());

	return as;
    }


    // now put in the methods to satisfy the DirectedGraph interface

    public List getHeads()
    {
	return cheads;
    }

    public List getTails()
    {
	return ctails;
    }

    public Iterator iterator()
    {
	return aug_list.iterator();
    }

    public List getPredsOf( Object s)
    {
	if (s instanceof AugmentedStmt)
	    return ((AugmentedStmt) s).cpreds;
	else if (s instanceof Stmt)
	    return get_AugStmt((Stmt) s).cpreds;
	else
	    throw new RuntimeException( "Object " + s + " class: " + s.getClass() + " not a member of this AugmentedStmtGraph");
    }

    public List getSuccsOf( Object s)
    {
	if (s instanceof AugmentedStmt)
	    return ((AugmentedStmt) s).csuccs;
	else if (s instanceof Stmt)
	    return get_AugStmt((Stmt) s).csuccs;
	else
	    throw new RuntimeException( "Object " + s + " class: " + s.getClass() + " not a member of this AugmentedStmtGraph");
    }
    
    // end of methods satisfying DirectedGraph

    public List get_BriefHeads()
    {
	return bheads;
    }

    public List get_BriefTails()
    {
	return btails;
    }

    public IterableSet get_ChainView()
    {
	IterableSet c = new IterableSet();

	c.addAll( aug_list);
	return c;
    }

    public Object clone()
    {
	return new AugmentedStmtGraph( this);
    }

    public boolean remove_AugmentedStmt( AugmentedStmt toRemove)
    {
	if (aug_list.contains( toRemove) == false)
	    return false;
	
	Iterator pit = toRemove.bpreds.iterator();
	while (pit.hasNext()) {
	    AugmentedStmt pas = (AugmentedStmt) pit.next();
	    if (pas.bsuccs.contains( toRemove))
		pas.bsuccs.remove( toRemove);
	}

	pit = toRemove.cpreds.iterator();
	while (pit.hasNext()) {
	    AugmentedStmt pas = (AugmentedStmt) pit.next();
	    if (pas.csuccs.contains( toRemove))
		pas.csuccs.remove( toRemove);
	}

	Iterator sit = toRemove.bsuccs.iterator();
	while (sit.hasNext()) {
	    AugmentedStmt sas = (AugmentedStmt) sit.next();
	    if (sas.bpreds.contains( toRemove))
		sas.bpreds.remove( toRemove);
	}

	sit = toRemove.csuccs.iterator();
	while (sit.hasNext()) {
	    AugmentedStmt sas = (AugmentedStmt) sit.next();
	    if (sas.cpreds.contains( toRemove))
		sas.cpreds.remove( toRemove);
	}

	aug_list.remove( toRemove);
	stmt_list.remove( toRemove.get_Stmt());

	if (bheads.contains( toRemove))
	    bheads.remove( toRemove);
	if (btails.contains( toRemove))
	    btails.remove( toRemove);
	if (cheads.contains( toRemove))
	    cheads.remove( toRemove);
	if (ctails.contains( toRemove))
	    ctails.remove( toRemove);
	
	binding.remove( toRemove.get_Stmt());

	return true;

	// NOTE: we do *NOT* touch the underlying unit graphs.
    }

    public String toString()
    {
	StringBuffer b = new StringBuffer();
	String cr = "\n";

	b.append( "AugmentedStmtGraph (size: " + size() + " stmts)" + cr);

	Iterator it = aug_list.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();

	    b.append( "| .---" + cr + "| | AugmentedStmt " + as.toString() + cr + "| |" + cr + "| |  preds:");

	    Iterator pit = as.cpreds.iterator();
	    while (pit.hasNext()) {
		AugmentedStmt pas = (AugmentedStmt) pit.next();

		b.append( " " + pas.toString());
	    }

	    b.append( cr + "| |" + cr + "| |  succs:");
	    Iterator sit = as.csuccs.iterator();
	    while (sit.hasNext()) {
		AugmentedStmt sas = (AugmentedStmt) sit.next();

		b.append( " " + sas.toString());
	    }

	    b.append( cr + "| |" + cr + "| |  doms:");
	    Iterator dit = as.get_Dominators().iterator();
	    while (dit.hasNext()) {
		AugmentedStmt das = (AugmentedStmt) dit.next();

		b.append( " " + das.toString());
	    }

	    b.append( cr + "| `---" + cr);
	}

	b.append( "-" + cr);
	return b.toString();
    }


    private void mirror_PredsSuccs( AugmentedStmt as, UnitGraph ug)
    {
	Stmt s = as.get_Stmt();

	LinkedList 
	    preds = new LinkedList(),
	    succs = new LinkedList();
	
	// mirror the predecessors
	Iterator pit = ug.getPredsOf( s).iterator();
	while (pit.hasNext()) {
	    Object po = get_AugStmt( (Stmt) pit.next());
	    if (preds.contains( po) == false)
		preds.add( po);
	}

	// mirror the successors
	Iterator sit = ug.getSuccsOf( s).iterator();
	while (sit.hasNext()) {
	    Object so = get_AugStmt( (Stmt) sit.next());
	    if (succs.contains( so) == false)
		succs.add( so);
	}

	// attach the mirrors properly to the AugmentedStmt
	if (ug instanceof BriefUnitGraph) {
	    as.bpreds = preds;
	    as.bsuccs = succs;

	    if (preds.size() == 0)
		bheads.add( as);
	    if (succs.size() == 0)
		btails.add( as);
	}
	else if (ug instanceof TrapUnitGraph) {
	    as.cpreds = preds;
	    as.csuccs = succs;

	    if (preds.size() == 0)
		cheads.add( as);
	    if (succs.size() == 0)
		ctails.add( as);
	}
	else throw new RuntimeException( "Unknown UnitGraph type: " + ug.getClass());
    }


    public IterableSet clone_Body( IterableSet oldBody)
    {
	HashMap 
	    old2new = new HashMap(),
	    new2old = new HashMap();
	
	IterableSet newBody = new IterableSet();

	Iterator it = oldBody.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();
	    AugmentedStmt clone = (AugmentedStmt) as.clone();

	    original2clone.put( as, clone);

	    old2new.put( as, clone);
	    new2old.put( clone, as);
	    newBody.add( clone);
	}

	it = newBody.iterator();
	while (it.hasNext()) {
	    AugmentedStmt newAs = (AugmentedStmt) it.next();
	    AugmentedStmt oldAs = (AugmentedStmt) new2old.get( newAs);

	    mirror_PredsSuccs( oldAs, oldAs.bpreds, newAs.bpreds, old2new);
	    mirror_PredsSuccs( oldAs, oldAs.cpreds, newAs.cpreds, old2new);
	    mirror_PredsSuccs( oldAs, oldAs.bsuccs, newAs.bsuccs, old2new);
	    mirror_PredsSuccs( oldAs, oldAs.csuccs, newAs.csuccs, old2new);
	}

	it = newBody.iterator();
	while (it.hasNext()) 
	    add_AugmentedStmt( (AugmentedStmt) it.next());

	HashMap so2n = new HashMap();

	it = oldBody.iterator();
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();
	    
	    Stmt os = as.get_Stmt();
	    Stmt ns = ((AugmentedStmt) old2new.get( as)).get_Stmt();

	    so2n.put( os, ns);
	}

	it = newBody.iterator();
	while (it.hasNext()) {
	    AugmentedStmt nas = (AugmentedStmt) it.next();
	    AugmentedStmt oas = (AugmentedStmt) new2old.get( nas);
	    	    
	    Stmt 
		ns = nas.get_Stmt(),
		os = oas.get_Stmt();

	    if (os instanceof IfStmt) {
		Unit 
		    target = ((IfStmt) os).getTarget(),
		    newTgt = null;

		if ((newTgt = (Unit) so2n.get( target)) != null)
		    ((IfStmt) ns).setTarget( newTgt);
		else
		    ((IfStmt) ns).setTarget( target);
	    }

	    else if (os instanceof TableSwitchStmt) {
		TableSwitchStmt 
		    otss = (TableSwitchStmt) os,
		    ntss = (TableSwitchStmt) ns;

		Unit
		    target = otss.getDefaultTarget(),
		    newTgt = null;

		if ((newTgt = (Unit) so2n.get( target)) != null)
		    ntss.setDefaultTarget( newTgt);
		else
		    ntss.setDefaultTarget( target);
		
		LinkedList new_target_list = new LinkedList();
		
		int target_count = otss.getHighIndex() - otss.getLowIndex() + 1;
		for (int i=0; i<target_count; i++) {
		    target = otss.getTarget(i);
		    newTgt = null;

		    if ((newTgt = (Unit) so2n.get( target)) != null)
			new_target_list.add( newTgt);
		    else
			new_target_list.add( target);
		}
		ntss.setTargets( new_target_list);
	    }

	    else if (os instanceof LookupSwitchStmt) {
		LookupSwitchStmt 
		    olss = (LookupSwitchStmt) os,
		    nlss = (LookupSwitchStmt) ns;

		Unit
		    target = olss.getDefaultTarget(),
		    newTgt = null;

		if ((newTgt = (Unit) so2n.get( target)) != null)
		    nlss.setDefaultTarget( newTgt);
		else
		    nlss.setDefaultTarget( target);
		
		Unit[] new_target_list = new Unit[ olss.getTargetCount()];
		
		for (int i=0; i<new_target_list.length; i++) {
		    target = olss.getTarget(i);
		    newTgt = null;

		    if ((newTgt = (Unit) so2n.get( target)) != null)
			new_target_list[i] = newTgt;
		    else
			new_target_list[i] = target;
		}
		nlss.setTargets( new_target_list);
		nlss.setLookupValues( olss.getLookupValues());
	    }
	}
	
	return newBody;
    }

    private void mirror_PredsSuccs( AugmentedStmt originalAs, List oldList, List newList, Map old2new)
    {
	Iterator it = oldList.iterator();
	while (it.hasNext()) {
	    AugmentedStmt oldAs = (AugmentedStmt) it.next();
	    AugmentedStmt newAs = (AugmentedStmt) old2new.get( oldAs);
	    
	    if (newAs != null)
		newList.add( newAs);

	    else {
		newList.add( oldAs);
		
		AugmentedStmt clonedAs = (AugmentedStmt) old2new.get( originalAs);

		if (oldList == originalAs.bpreds)
		    oldAs.bsuccs.add( clonedAs);
		else if (oldList == originalAs.cpreds)
		    oldAs.csuccs.add( clonedAs);
		else if (oldList == originalAs.bsuccs)
		    oldAs.bpreds.add( clonedAs);
		else if (oldList == originalAs.csuccs)
		    oldAs.cpreds.add( clonedAs);
		else 
		    throw new RuntimeException( "Error mirroring preds and succs in Try block splitting.");
	    }
	}
    }


    public void find_Dominators()
    {
	// set up the dominator sets for all the nodes in the graph
	Iterator asgit = aug_list.iterator();
	while (asgit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) asgit.next();

	    // Dominators:
	    // safe starting approximation for S(0) ... empty set
	    // unsafe starting approximation for S(i) .. full set
	    if (as.cpreds.size() != 0) {

		if (as.get_Dominators().isEmpty() == false)
		    as.get_Dominators().clear();

		as.get_Dominators().addAll( aug_list);
	    }
	    else 
		as.get_Dominators().clear();
	}

	// build the worklist
	IterableSet worklist = new IterableSet();
	worklist.addAll( aug_list);

	// keep going until the worklist is empty
	while (worklist.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
	    worklist.removeFirst();
	    
	    IterableSet pred_intersection = new IterableSet();
	    boolean first_pred = true;

	    // run through all the predecessors and get their dominance intersection
	    Iterator pit = as.cpreds.iterator();
	    while (pit.hasNext()) {
		AugmentedStmt pas = (AugmentedStmt) pit.next();
		
		// for the first predecessor just take all his dominators
		if (first_pred) {
		    pred_intersection.addAll( pas.get_Dominators());
		    if (pred_intersection.contains( pas) == false)
			pred_intersection.add( pas);
		    first_pred = false;
		}

		// for the subsequent predecessors remove the ones they do not have from the intersection
		else {
		    Iterator piit = pred_intersection.snapshotIterator();
		    while (piit.hasNext()) {
			AugmentedStmt pid = (AugmentedStmt) piit.next();

			if ((pas.get_Dominators().contains( pid) == false) && (pas != pid))
			    pred_intersection.remove( pid);
		    }
		}
	    }

	    // update dominance if we have a change
	    if (as.get_Dominators().equals( pred_intersection) == false) {
		Iterator sit = as.csuccs.iterator();
		while (sit.hasNext()) {
		    Object o = sit.next();

		    if (worklist.contains( o) == false)
			worklist.add( o);
		}

		as.get_Dominators().clear();
		as.get_Dominators().addAll( pred_intersection);
	    }
	}
    }
}
