package soot.dava.toolkits.base.finders;

import soot.*;
import soot.dava.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;

public class SynchronizedBlockFinder implements FactFinder
{
    private SynchronizedBlockFinder() 
    {
	UNKNOWN = -100000; // Note there are at most 65536 monitor exits in a method.

	WHITE = new Integer( 0);
	GRAY  = new Integer( 1);
	BLACK = new Integer( 2);

	VARIABLE_INCR = new Integer( UNKNOWN);

	THROWABLE = "java.lang.Throwable";
    }
    
    private static SynchronizedBlockFinder instance = new SynchronizedBlockFinder();

    private HashMap as2ml;
    private IterableSet monitorLocalSet, monitorEnterSet;

    private Integer WHITE, GRAY, BLACK, VARIABLE_INCR;
    private int UNKNOWN;
    private String THROWABLE;


    public static SynchronizedBlockFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Dava.v().log( "SynchronizedBlockFinder::find()");

	as2ml = new HashMap();

	IterableSet synchronizedBlockFacts = body.get_SynchronizedBlockFacts();
	synchronizedBlockFacts.clear();

	set_MonitorLevels( asg);
	Map as2synchSet = build_SynchSets();

	IterableSet usedMonitors = new IterableSet();

	Iterator asgit = asg.iterator();
	while (asgit.hasNext()) {

	    AugmentedStmt as = (AugmentedStmt) asgit.next();
	    if (as.get_Stmt() instanceof EnterMonitorStmt) {

		IterableSet synchSet = (IterableSet) as2synchSet.get( as);
		if (synchSet != null) {

		    IterableSet synchBody = get_BodyApproximation( as, synchSet);		    
		    Value local = ((EnterMonitorStmt) as.get_Stmt()).getOp();
		    Integer level = (Integer) ((HashMap) as2ml.get( as)).get( local);

		    Iterator enit = body.get_ExceptionFacts().iterator();
		    while (enit.hasNext()) {
			ExceptionNode en = (ExceptionNode) enit.next();

			if (verify_CatchBody( en, synchBody, local)) {
			    if (SET.nest( new SETSynchronizedBlockNode( en, local))) {

				Iterator ssit = synchSet.iterator();
				while (ssit.hasNext()) {
				    AugmentedStmt ssas = (AugmentedStmt) ssit.next();
				    Stmt sss = ssas.get_Stmt();
				    
				    if ((sss instanceof MonitorStmt) && 
					(((MonitorStmt) sss).getOp() == local) && 
					(((Integer) ((HashMap) as2ml.get( ssas)).get( local)).equals( level)) &&
					(usedMonitors.contains( ssas) == false))
					
					usedMonitors.add( ssas);
				}
				
				synchronizedBlockFacts.add( en);
			    }
			    
			    break;
			}
		    }
		}
	    }
	}

	IterableSet monitorFacts = body.get_MonitorFacts();
	monitorFacts.clear();

	asgit = asg.iterator();
	while (asgit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) asgit.next();

	    if ((as.get_Stmt() instanceof MonitorStmt) && (usedMonitors.contains( as) == false))
		monitorFacts.add( as);
	}
    }


    private void find_VariableIncreasing( AugmentedStmtGraph asg, HashMap local2level_template, LinkedList viAugStmts, HashMap as2locals) 
    {
    	StronglyConnectedComponents scc = new StronglyConnectedComponents( asg);
	IterableSet viSeeds = new IterableSet();
	HashMap 
	    as2color = new HashMap(),
	    as2rml   = new HashMap();	

	Iterator asgit = asg.iterator();
	while (asgit.hasNext()) 
	    as2rml.put( asgit.next(), local2level_template.clone());

	// loop through all the strongly connected components in the graph
	Iterator sccit = scc.getComponents().iterator();
	while (sccit.hasNext()) {
	    List componentList = (List) sccit.next();

	    // skip trivial strongly connected components
	    if (componentList.size() < 2)
		continue;

	    IterableSet component = new IterableSet();
	    component.addAll( componentList);

	    Iterator cit = component.iterator();
	    while (cit.hasNext()) 
		as2color.put( cit.next(), WHITE);

	    // DFS and mark enough of the variable increasing points to get started.
	    AugmentedStmt seedStmt = (AugmentedStmt) component.getFirst();
	    DFS_Scc( seedStmt, component, as2rml, as2color, seedStmt, viSeeds);
	}

	IterableSet worklist = new IterableSet();
	worklist.addAll( viSeeds);

	// Propegate the variable increasing property.
	while (worklist.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
	    worklist.removeFirst();
	    HashMap local2level = (HashMap) as2rml.get( as);
	    
	    Iterator sit = as.csuccs.iterator();
	    while (sit.hasNext()) {
		AugmentedStmt sas = (AugmentedStmt) sit.next();
		HashMap slocal2level = (HashMap) as2rml.get( sas);

		Iterator mlsit = monitorLocalSet.iterator();
		while (mlsit.hasNext()) {
		    Value local = (Value) mlsit.next();

		    if ((local2level.get( local) == VARIABLE_INCR) && (slocal2level.get( local) != VARIABLE_INCR)) {
			slocal2level.put( local, VARIABLE_INCR);
			
			if (worklist.contains( sas) == false)
			    worklist.addLast( sas);
		    }
		}
	    }
	}

	// Summarize the variable increasing information for the set_MonitorLevels() function.
	asgit = asg.iterator();
	while (asgit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) asgit.next();
	    HashMap local2level = (HashMap) as2rml.get( as);

	    Iterator mlsit = monitorLocalSet.iterator();
	    while (mlsit.hasNext()) {
		Value local = (Value) mlsit.next();
		
		if (local2level.get( local) == VARIABLE_INCR) {

		    if (viAugStmts.getLast() != as)
			viAugStmts.addLast( as);

		    LinkedList locals = null;

		    if ((locals = (LinkedList) as2locals.get( as)) == null) {
			locals = new LinkedList();
			as2locals.put( as, locals);
		    }
		    
		    locals.addLast( local);
		}
	    }
	}
    }


    private void DFS_Scc( AugmentedStmt as, IterableSet component, HashMap as2rml, HashMap as2color, AugmentedStmt seedStmt, IterableSet viSeeds)
    {
	as2color.put( as, GRAY);

	Stmt s = as.get_Stmt();
	HashMap local2level = (HashMap) as2rml.get( as);
	
	if (s instanceof MonitorStmt ) {
	    Value local = ((MonitorStmt) s).getOp();
	    
	    if (s instanceof EnterMonitorStmt) 
		local2level.put( local, new Integer( ((Integer) local2level.get( local)).intValue() + 1));
	    else
		local2level.put( local, new Integer( ((Integer) local2level.get( local)).intValue() - 1));
	}
	    
	Iterator sit = as.csuccs.iterator();
	while (sit.hasNext()) {
	    AugmentedStmt sas = (AugmentedStmt) sit.next();

	    if (component.contains( sas) == false)
		continue;

	    HashMap slocal2level = (HashMap) as2rml.get( sas);
	    Integer scolor = (Integer) as2color.get( sas);

	    if (scolor.equals( WHITE)) {

		Iterator mlsit = monitorLocalSet.iterator();
		while (mlsit.hasNext()) {
		    Value local = (Value) mlsit.next();

		    slocal2level.put( local, local2level.get( local));
		}

		DFS_Scc( sas, component, as2rml, as2color, seedStmt, viSeeds);
	    }

	    else {

		Iterator mlsit = monitorLocalSet.iterator();
		while (mlsit.hasNext()) {
		    Value local = (Value) mlsit.next();

		    if (((Integer) slocal2level.get( local)).intValue() < ((Integer) local2level.get( local)).intValue()) {
			slocal2level.put( local, VARIABLE_INCR);
 
			if (viSeeds.contains( sas) == false)
			    viSeeds.add( sas);
		    }
		}
	    }
	}

	as2color.put( as, BLACK);
    }

    private Map build_SynchSets()
    {
	HashMap as2synchSet = new HashMap();

	Iterator mesit = monitorEnterSet.iterator();
    monitorEnterLoop:
	while (mesit.hasNext()) {
	    AugmentedStmt headAs = (AugmentedStmt) mesit.next();
	    Value local = ((EnterMonitorStmt) headAs.get_Stmt()).getOp();
	    IterableSet synchSet = new IterableSet();

	    int monitorLevel = ((Integer) ((HashMap) as2ml.get( headAs)).get( local)).intValue();
	    IterableSet worklist = new IterableSet();
	    worklist.add( headAs);

	    while (worklist.isEmpty() == false) {
		AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
		worklist.removeFirst();

		Stmt s = as.get_Stmt();
		if ((s instanceof DefinitionStmt) && (((DefinitionStmt) s).getLeftOp() == local))
		    continue monitorEnterLoop;

		synchSet.add( as);

		Iterator sit = as.csuccs.iterator();
		while (sit.hasNext()) {
		    AugmentedStmt sas = (AugmentedStmt) sit.next();
		    int sml = ((Integer) ((HashMap) as2ml.get( sas)).get( local)).intValue();

		    if (sas.get_Dominators().contains( headAs) && (sml >= monitorLevel) && 
			(worklist.contains( sas) == false) && (synchSet.contains( sas) == false))

			worklist.addLast( sas);			
		}
	    }

	    as2synchSet.put( headAs, synchSet);
	}

	return as2synchSet;
    }

    private void set_MonitorLevels( AugmentedStmtGraph asg)
    {
	monitorLocalSet = new IterableSet();
	monitorEnterSet = new IterableSet();

	// Identify the locals that are used in monitor statements, and all the monitor enters.
	Iterator asgit = asg.iterator();
	while (asgit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) asgit.next();
	    Stmt s = as.get_Stmt();
	    
	    if (s instanceof MonitorStmt) {
		Value local = ((MonitorStmt) s).getOp();

		if (monitorLocalSet.contains( local) == false)
		    monitorLocalSet.add( local);

		if (s instanceof EnterMonitorStmt)
		    monitorEnterSet.add( as);
	    }
	}
	
	// Set up a base monitor lock level of 0 for all monitor locals.
	HashMap local2level_template = new HashMap();
	Iterator mlsit = monitorLocalSet.iterator();
	while (mlsit.hasNext()) 
	    local2level_template.put( mlsit.next(), new Integer( 0));

	// Give each statement the base monitor lock levels.
	asgit = asg.iterator();
	while (asgit.hasNext())
	    as2ml.put( asgit.next(), local2level_template.clone());

	LinkedList viAugStmts = new LinkedList();
	HashMap incrAs2locals = new HashMap();
	
	// setup the variable increasing monitor levels
	find_VariableIncreasing( asg, local2level_template, viAugStmts, incrAs2locals);	
	Iterator viasit = viAugStmts.iterator();
	while (viasit.hasNext()) {
	    AugmentedStmt vias = (AugmentedStmt) viasit.next();
	    HashMap local2level = (HashMap) as2ml.get( vias);

	    Iterator lit = ((LinkedList) incrAs2locals.get( vias)).iterator();
	    while (lit.hasNext()) 
		local2level.put( lit.next(), VARIABLE_INCR);
	}

	IterableSet worklist = new IterableSet();
	worklist.addAll( monitorEnterSet);

	// Flow monitor lock levels.
	while (worklist.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) worklist.getFirst();
	    worklist.removeFirst();

	    HashMap cur_local2level = (HashMap) as2ml.get( as);

	    Iterator pit = as.cpreds.iterator();
	    while (pit.hasNext()) {
		AugmentedStmt pas = (AugmentedStmt) pit.next();
		Stmt s = as.get_Stmt();

		HashMap pred_local2level = (HashMap) as2ml.get( pas);

		mlsit = monitorLocalSet.iterator();
		while (mlsit.hasNext()) {
		    Value local = (Value) mlsit.next();
		    
		    int predLevel = ((Integer) pred_local2level.get( local)).intValue();
		    Stmt ps = pas.get_Stmt();

		    if (predLevel == UNKNOWN)  // Already marked as variable increasing.
			continue;
		    
		    if (ps instanceof ExitMonitorStmt) {
			ExitMonitorStmt ems = (ExitMonitorStmt) ps;

			if ((ems.getOp() == local) && (predLevel > 0))
			    predLevel--;
		    }

		    if (s instanceof EnterMonitorStmt) {
			EnterMonitorStmt ems = (EnterMonitorStmt) s;

			if ((ems.getOp() == local) && (predLevel >= 0))
			    predLevel++;
		    }

		    int curLevel  = ((Integer) cur_local2level.get( local)).intValue();

		    if (predLevel > curLevel) {
			cur_local2level.put( local, new Integer( predLevel));
			
			Iterator sit = as.csuccs.iterator();
			while (sit.hasNext()) {
			    Object so = sit.next();
			    
			    if (worklist.contains( so) == false)
				worklist.add( so);
			}
		    }
		}
	    }
	}
    }

    private boolean verify_CatchBody( ExceptionNode en, IterableSet synchBody, Value monitorVariable)
    {
	if ((en.get_Body().equals( synchBody) == false) ||
	    (en.get_Exception().getName().equals( THROWABLE) == false) || 
	    (en.get_CatchList().size() > 1))

	    return false;

	IterableSet catchBody = en.get_CatchBody();
	AugmentedStmt 
	    entryPoint = null;

	Iterator it = catchBody.iterator();
    catchBodyLoop:
	while (it.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) it.next();

	    Iterator pit = as.cpreds.iterator();
	    while (pit.hasNext()) {
		AugmentedStmt pas = (AugmentedStmt) pit.next();

		if (catchBody.contains( pas) == false) {
		    entryPoint = as;
		    break catchBodyLoop;
		}
	    }
	}


	// Horror upon horrors, what follows is a hard coded state machine.

	AugmentedStmt as = entryPoint;

	if (as.bsuccs.size() != 1)
	    return false;

	while (as.get_Stmt() instanceof GotoStmt) {
	    as = (AugmentedStmt) as.bsuccs.get(0);
	    if ((as.bsuccs.size() != 1) || ((as != entryPoint) && (as.cpreds.size() != 1))) 
		return false;
	}
	
	Stmt s = as.get_Stmt();
	
	if ((s instanceof DefinitionStmt) == false)
	    return false;

	DefinitionStmt ds = (DefinitionStmt) s;
	Value asnFrom = ds.getRightOp();
	
	if (((asnFrom instanceof CaughtExceptionRef) == false) || 
	    (((RefType) ((CaughtExceptionRef) asnFrom).getType()).getSootClass().getName().equals( THROWABLE) == false))
	    return false;

	Value throwlocal = ds.getLeftOp();

	IterableSet esuccs = new IterableSet();
	esuccs.addAll( as.csuccs);
	esuccs.removeAll( as.bsuccs);

	as = (AugmentedStmt) as.bsuccs.get(0);
	if ((as.bsuccs.size() != 1) || (as.cpreds.size() != 1) || (verify_ESuccs( as, esuccs) == false))
	    return false;

	s = as.get_Stmt();

	if (((s instanceof ExitMonitorStmt) == false) || (((ExitMonitorStmt) s).getOp() != monitorVariable))
	    return false;

	as = (AugmentedStmt) as.bsuccs.get(0);
	if ((as.bsuccs.size() != 0) || (as.cpreds.size() != 1) || (verify_ESuccs( as, esuccs) == false))
	    return false;

	s = as.get_Stmt();

	if (((s instanceof ThrowStmt) == false) || (((ThrowStmt) s).getOp() != throwlocal))
	    return false;

	return true;
    }

    private boolean verify_ESuccs( AugmentedStmt as, IterableSet ref)
    {
	IterableSet esuccs = new IterableSet();

	esuccs.addAll( as.csuccs);
	esuccs.removeAll( as.bsuccs);

	return esuccs.equals( ref);
    }

    private IterableSet get_BodyApproximation( AugmentedStmt head, IterableSet synchSet) 
    {
	IterableSet body = (IterableSet) synchSet.clone();
	Value local = ((EnterMonitorStmt) head.get_Stmt()).getOp();
	Integer level = (Integer) ((HashMap) as2ml.get( head)).get( local);

	body.remove( head);
	
	Iterator bit = body.snapshotIterator();
	while (bit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) bit.next();
	    Stmt s = as.get_Stmt();
	    
	    if ((s instanceof ExitMonitorStmt) && 
		(((ExitMonitorStmt) s).getOp() == local) && 
		(((Integer) ((HashMap) as2ml.get( as)).get( local)).equals( level))) {

		Iterator sit = as.csuccs.iterator();
		while (sit.hasNext()) {
		    AugmentedStmt sas = (AugmentedStmt) sit.next();
		    
		    if (sas.get_Dominators().contains( head) == false)
			continue;
		    
		    Stmt ss = sas.get_Stmt();
		    
		    if (((ss instanceof GotoStmt) || (ss instanceof ThrowStmt)) && (body.contains( sas) == false))
			body.add( sas);
		}
	    }
	}

	return body;
    }
}
