/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.dava.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.dava.*;
import java.util.*;

public class WhileMatcher extends BodyTransformer
{
    private static WhileMatcher instance = new WhileMatcher();
    private WhileMatcher() {}

    public static WhileMatcher v() { return instance; }

    private List tempList;

    protected void internalTransform(Body b, String phaseName, Map options) {
      DavaBody body = (DavaBody) b;
      Trunk t;
      boolean done;
      
      if(Main.isVerbose)
	System.out.println("[" + body.getMethod().getName() + "] Matching while's...");
      
      done = false;
      Iterator it;

      while (!done) {
	done = true;

	tempList = new ArrayList();
	it = body.getTrunks().iterator();
	while (it.hasNext()) 
	    if (findMatch( body, (Trunk) it.next())) {
		done = false;
		body.transformed = true;
	    }

	body.getTrunks().addAll( tempList);
	if (!done)
	  body.clean();
      }
    }
  
  private boolean findMatch( DavaBody body, Trunk t) {

    List succ0List;

    if (t.removed())
      return false;

    boolean found = false;
    
    if ((succ0List = t.getSuccessors()).size() == 2) {
	Trunk t0, t1;
	
	t0 = t1 = t;

	for (int i=0; (i<2) && (!found); i++) {
	    t0 = (Trunk) succ0List.get(i);
	    t1 = (i == 0) ? (Trunk) succ0List.get(1) : (Trunk) succ0List.get(0);
	    
	    if ((t0.getPredecessors().size() == 1) && (t0.getSuccessors().size() == 1) && (t0.getSuccessors().get(0) == t))
		found = true;
	}
	
	if (found) {
	    
	    // create a new while trunk
	    WhileTrunk item = Dava.v().newWhileTrunk( t.getCondition(), t0);
	    
	    item.firstStmt = t.getFirstStmt();
	    if (t0.getLastStmt() instanceof GotoStmt) 
		t0.maskGotoStmt();

	    // set the new while trunk to link to the trunk graph
	    item.addSuccessor( t1);
	    item.setPredecessorList( t.getPredecessors());
	    item.getPredecessors().remove( t0);

	    // "remove" our old trunks from the outside graph
	    t.setRemoved();
	    t0.setRemoved();

	    // fix the successor links from the outside graph to the new trunk
	    Iterator pit = item.getPredecessors().iterator();
	    while (pit.hasNext()) {
	      Trunk pt = (Trunk) pit.next();
	      pt.getSuccessors().remove( t);
	      pt.addSuccessor( item);
	    }
	    
	    // fix the predecessor link from the outside graph to the new trunk
	    t1.getPredecessors().remove( t);
	    t1.addPredecessor( item);
	    
	    // finally, add the new trunk the the body! (not just the graph :-)
	    tempList.add( item);
	  }
      }
    return found;
  }
}
