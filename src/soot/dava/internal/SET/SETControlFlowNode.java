package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;

public abstract class SETControlFlowNode extends SETNode
{
    private AugmentedStmt characterizingStmt;

    public SETControlFlowNode( AugmentedStmt characterizingStmt, IteratorableSet body)
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
	    IteratorableSet subBody = (IteratorableSet) sbit.next();
	    
	    if (subBody.contains( get_EntryStmt()) == false)
		continue;

	    IteratorableSet childChain = (IteratorableSet) parent.get_Body2ChildChain().get( subBody);
	    HashSet childUnion = new HashSet();

	    Iterator ccit = childChain.iterator();
	    while (ccit.hasNext()) {
		IteratorableSet childBody = ((SETNode) ccit.next()).get_Body();
		childUnion.addAll( childBody);

		if (childBody.contains( characterizingStmt)) {
		    
		    Iterator bit = get_Body().snapshotIterator();
		    while (bit.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) bit.next();

			if (childBody.contains( as) == false) 
			    remove_AugmentedStmt( as);
		    }
		}
	    }
	}

	return true;
    }
}
