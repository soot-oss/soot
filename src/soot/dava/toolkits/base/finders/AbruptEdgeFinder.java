package soot.dava.toolkits.base.finders;

import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;

public class AbruptEdgeFinder implements FactFinder
{
    private AbruptEdgeFinder() {}
    private static AbruptEdgeFinder instance = new AbruptEdgeFinder();

    public static AbruptEdgeFinder v()
    {
	return instance;
    }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	SET.find_AbruptEdges( this);
    }

    public void find_Continues( SETNode SETParent, IteratorableSet body, IteratorableSet children)
    {
	if ((SETParent instanceof SETCycleNode) == false)
	    return;

	SETCycleNode scn = (SETCycleNode) SETParent;
	IteratorableSet naturalPreds = ((SETNode) children.getLast()).get_NaturalExits();

	Iterator pit = scn.get_CharacterizingStmt().bpreds.iterator();
	while (pit.hasNext()) {
	    AugmentedStmt pas = (AugmentedStmt) pit.next();
	    
	    if ((body.contains( pas)) && (naturalPreds.contains( pas) == false)) 
		((SETStatementSequenceNode) pas.myNode).insert_AbruptStmt( new DAbruptStmt( "continue", scn.get_Label()));
	}
    }

    public void find_Breaks( SETNode prev, SETNode cur)
    {
	IteratorableSet naturalPreds = prev.get_NaturalExits();

	Iterator pit = cur.get_EntryStmt().bpreds.iterator();
	while (pit.hasNext()) {
	    AugmentedStmt pas = (AugmentedStmt) pit.next();
	    
	    if (prev.get_Body().contains( pas) == false)
		continue;

	    if (naturalPreds.contains( pas) == false)
		((SETStatementSequenceNode) pas.myNode).insert_AbruptStmt( new DAbruptStmt( "break", prev.get_Label()));
	}
    }
}
