package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;

public abstract class SETControlFlowNode extends SETNode
{
    private AugmentedStmt characterizingStmt;

    public SETControlFlowNode( AugmentedStmt characterizingStmt, IterableSet body)
    {
	super( body);
	this.characterizingStmt = characterizingStmt;
    }

    public AugmentedStmt get_CharacterizingStmt()
    {
	return characterizingStmt;
    }

    protected boolean resolve( SETNode parent)
    {
	Iterator sbit = parent.get_SubBodies().iterator();
	while (sbit.hasNext()) {
	    IterableSet subBody = (IterableSet) sbit.next();
	    
	    if (subBody.contains( get_EntryStmt()) == false)
		continue;

	    IterableSet childChain = (IterableSet) parent.get_Body2ChildChain().get( subBody);
	    HashSet childUnion = new HashSet();

	    Iterator ccit = childChain.iterator();
	    while (ccit.hasNext()) {
		
		SETNode child = (SETNode) ccit.next();
		IterableSet childBody = child.get_Body();
		childUnion.addAll( childBody);

		if (childBody.contains( characterizingStmt)) {
		    
		    Iterator bit = get_Body().snapshotIterator();
		    while (bit.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) bit.next();

			if (childBody.contains( as) == false) 
			    remove_AugmentedStmt( as);

			else if (child instanceof SETControlFlowNode) {
			    SETControlFlowNode scfn = (SETControlFlowNode) child;

			    if ((scfn.get_CharacterizingStmt() == as) ||
				((as.cpreds.size() == 1) && (as.get_Stmt() instanceof GotoStmt) && (scfn.get_CharacterizingStmt() == as.cpreds.get(0))))

				remove_AugmentedStmt( as);
			}
		    }

		    return true;
		}
	    }
	}

	return true;
    }
}
