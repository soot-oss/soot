package soot.dava.toolkits.base.finders;

import soot.dava.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.misc.*;

public class IfFinder implements FactFinder
{
    private IfFinder() {}
    private static IfFinder instance = new IfFinder();

    public static IfFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Dava.v().log( "IfFinder::find()");

	Iterator asgit = asg.iterator();
	while (asgit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) asgit.next();

	    Stmt s = as.get_Stmt();

	    if (s instanceof IfStmt) {
		IfStmt ifs = (IfStmt) s;

		if (body.get_ConsumedConditions().contains( as))
		    continue;

		body.consume_Condition( as);
		
		AugmentedStmt 
		    succIf   = asg.get_AugStmt( ifs.getTarget()),
		    succElse = (AugmentedStmt) as.bsuccs.get(0);

		if (succIf == succElse)
		    succElse = (AugmentedStmt) as.bsuccs.get(1);


		asg.calculate_Reachability( succIf, succElse, as);
		asg.calculate_Reachability( succElse, succIf, as);

		IterableSet
		    fullBody = new IterableSet(),
		    ifBody   = find_Body( succIf, succElse),
		    elseBody = find_Body( succElse, succIf);

		fullBody.add( as);
		fullBody.addAll( ifBody);
		fullBody.addAll( elseBody);
		
		Iterator enlit = body.get_ExceptionFacts().iterator();
		while (enlit.hasNext()) {
		    ExceptionNode en = (ExceptionNode) enlit.next();
		    IterableSet tryBody = en.get_TryBody();

		    if (tryBody.contains( as)) {
			Iterator fbit = fullBody.snapshotIterator();
			
			while (fbit.hasNext()) {
			    AugmentedStmt fbas = (AugmentedStmt) fbit.next();

			    if (tryBody.contains( fbas) == false) {
				fullBody.remove( fbas);

				if (ifBody.contains( fbas))
				    ifBody.remove( fbas);

				if (elseBody.contains( fbas))
				    elseBody.remove( fbas);
			    }
			}
		    }
		}

		SET.nest( new SETIfElseNode( as, fullBody, ifBody, elseBody));
	    }
	}
    }

    private IterableSet find_Body( AugmentedStmt targetBranch, AugmentedStmt otherBranch)
    {
	IterableSet body = new IterableSet();

	if (targetBranch.get_Reachers().contains( otherBranch))
	    return body;

	LinkedList worklist = new LinkedList();
	worklist.addLast( targetBranch);

	while (worklist.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) worklist.removeFirst();

	    if (body.contains( as) == false) {
		body.add( as);

		Iterator sit = as.csuccs.iterator();
		while (sit.hasNext()) {
		    AugmentedStmt sas = (AugmentedStmt) sit.next();

		    if ((sas.get_Reachers().contains( otherBranch) == false) && (sas.get_Dominators().contains( targetBranch) == true))
			worklist.addLast( sas);
		}
	    }
	}

	return body;
    }
}
