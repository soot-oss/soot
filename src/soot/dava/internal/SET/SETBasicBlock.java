package soot.dava.internal.SET;
import soot.*;

import java.util.*;
import soot.util.*;

public class SETBasicBlock implements Comparable
{
    
    private SETNode entryNode, exitNode;
    private IterableSet predecessors, successors, body;
    private int priority;

    
    public SETBasicBlock()
    {
	predecessors = new IterableSet();
	successors = new IterableSet();
	body = new IterableSet();

	entryNode = exitNode = null;
	priority = -1;
    }

    public int compareTo( Object o)
    {
	if (o == this)
	    return 0;

	SETBasicBlock other = (SETBasicBlock) o;

	int difference = other.get_Priority() - get_Priority();  // major sorting order ... _descending_
	if (difference == 0)                                     // bogus minor order ... it isn't consistent
	    difference = 1;                                      //  but it doesn't matter.

	return difference;
    }

    private int get_Priority()
    {
	if (priority == -1) {
	    priority = 0;

	    if (predecessors.size() == 1) {
		Iterator sit = successors.iterator();

		while (sit.hasNext()) {
		    int sucScore = ((SETBasicBlock) sit.next()).get_Priority();

		    if (sucScore > priority)
			priority = sucScore;
		}

		priority++;		
	    }
	}

	return priority;
    }
    

    /* 
     *  adds must be done in order such that the entry node is done first and the exit is done last.
     */

    public void add( SETNode sn)
    {
	if (body.isEmpty())
	    entryNode = sn;

	body.add( sn);
	G.v().SETBasicBlock_binding.put( sn, this);

	exitNode = sn;	
    }
    
    public SETNode get_EntryNode()
    {
	return entryNode;
    }
    
    public SETNode get_ExitNode()
    {
	return exitNode;
    }
    
    public IterableSet get_Predecessors()
    {
	return predecessors;
    }
    
    public IterableSet get_Successors()
    {
	return successors;
    }
    
    public IterableSet get_Body()
    {
	return body;
    }
    
    public static SETBasicBlock get_SETBasicBlock( SETNode o)
    {
	return (SETBasicBlock) G.v().SETBasicBlock_binding.get( o);
    }


    public void printSig()
    {
	Iterator it = body.iterator();
	while (it.hasNext())
	    ((SETNode) it.next()).dump();
    }

    public void dump()
    {
	printSig();
	G.v().out.println( "=== preds ===");

	Iterator it = predecessors.iterator();
	while (it.hasNext())
	    ((SETBasicBlock) it.next()).printSig();

	G.v().out.println( "=== succs ===");

	it = successors.iterator();
	while (it.hasNext())
	    ((SETBasicBlock) it.next()).printSig();
    }
}
