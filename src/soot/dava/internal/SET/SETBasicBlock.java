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
