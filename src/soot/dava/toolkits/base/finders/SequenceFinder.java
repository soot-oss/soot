package soot.dava.toolkits.base.finders;

import soot.dava.*;
import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;

public class SequenceFinder implements FactFinder
{
    private SequenceFinder() {}
    private static SequenceFinder instance = new SequenceFinder();

    public static SequenceFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Dava.v().log( "SequenceFinder::find()");

	SET.find_StatementSequences( this, body);
    }

    public void find_StatementSequences( SETNode SETParent, IterableSet body, HashSet childUnion, DavaBody davaBody)
    {
	Iterator bit = body.iterator();
	while (bit.hasNext()) {
	    AugmentedStmt as = (AugmentedStmt) bit.next();

	    if (childUnion.contains( as))
		continue;
	    
	    IterableSet sequenceBody = new IterableSet();

	    while (as.bpreds.size() == 1) {
		AugmentedStmt pas = (AugmentedStmt) as.bpreds.get(0);
		if ((body.contains( pas) == false) || (childUnion.contains( pas) == true))
		    break;

		as = pas;
	    }

	    while ((body.contains( as)) && (childUnion.contains( as) == false)) {

		childUnion.add( as);
		sequenceBody.addLast( as);
		
		if (as.bsuccs.isEmpty() == false)
		    as = (AugmentedStmt) as.bsuccs.get(0);

		if (as.bpreds.size() != 1)
		    break;
	    }

	    SETParent.add_Child( new SETStatementSequenceNode( sequenceBody, davaBody), (IterableSet) SETParent.get_Body2ChildChain().get( body));
	}
    }
}
