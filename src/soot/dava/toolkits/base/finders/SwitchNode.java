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

package soot.dava.toolkits.base.finders;

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;

public class SwitchNode implements Comparable
{
    private LinkedList preds, succs;
    private AugmentedStmt as;
    private int score;
    private TreeSet indexSet;
    private IterableSet body;

    public SwitchNode( AugmentedStmt as, TreeSet indexSet, IterableSet body)
    {
	this.as = as;
	this.indexSet = indexSet;
	this.body = body;

	preds = new LinkedList();
	succs = new LinkedList();

	score = -1;
    }

    public int get_Score()
    {
	if (score == -1) {
	    score = 0;

	    if (preds.size() < 2) {

		Iterator sit = succs.iterator();
		while (sit.hasNext()) {
		    SwitchNode ssn = (SwitchNode) sit.next();

		    int curScore = ssn.get_Score();
		    if (score < curScore)
			score = curScore;
		}

		score++;
	    }
	}

	return score;
    }

    public List get_Preds()
    {
	return preds;
    }

    public List get_Succs()
    {
	return succs;
    }

    public AugmentedStmt get_AugStmt()
    {
	return as;
    }

    public TreeSet get_IndexSet()
    {
	return indexSet;
    }

    public IterableSet get_Body()
    {
	return body;
    }

    public SwitchNode reset()
    {
	preds.clear();
	succs.clear();

	return this;
    }

    public void setup_Graph( HashMap binding)
    {
	Iterator rit = ((AugmentedStmt) as.bsuccs.get(0)).get_Reachers().iterator();
	while (rit.hasNext()) {
	    SwitchNode pred = (SwitchNode) binding.get( rit.next());
	    
	    if (pred != null) {
		if (preds.contains( pred) == false)
		    preds.add( pred);
		
		if (pred.succs.contains( this) == false)
		    pred.succs.add( this);
	    }
	}
    }


    /*
     *  Can compare to an Integer, a String, a set of Indices, and another SwitchNode.
     */

    public int compareTo( Object o)
    {
	if (o == this)
	    return 0;

	if (indexSet.last() instanceof String)
	    return 1;

	if (o instanceof String)
	    return -1;

	if (o instanceof Integer) 
	    return ((Integer) indexSet.last()).intValue() - ((Integer) o).intValue();

	if (o instanceof TreeSet) {
	    TreeSet other = (TreeSet) o;

	    if (other.last() instanceof String)
		return -1;

	    return ((Integer) indexSet.last()).intValue() - ((Integer) other.last()).intValue();
	}
	
	SwitchNode other = (SwitchNode) o;

	if (other.indexSet.last() instanceof String)
	    return -1;

	return ((Integer) indexSet.last()).intValue() - ((Integer) other.indexSet.last()).intValue();
    }
}

