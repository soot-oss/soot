/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Jerome Miecznikowski
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

package soot.dava;

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.grimp.*;
//import soot.dava.internal.*;
import soot.dava.toolkits.base.*;
import soot.toolkits.graph.*;
import soot.util.*;

public class DavaBody extends Body
{
    /**
     *  Construct an empty DavaBody 
     **/
    
    private List trunkList, tempList;
    private Trunk entryTrunk;
    public boolean transformed;
    
    PatchingChain trunkChain;
    PatchingChain originalUnits;
    
    //public PatchingChain getUnits() {return trunkChain;}
    
    public List getTrunks() {
	return trunkList;
    }
    
    public Trunk getHeadTrunk() {
	return entryTrunk;
    }

  public void addTrunk( Trunk t)
  {
      if (t != null)
	  trunkList.add( t);
  }
  
  public boolean removeTrunk( Trunk t)
  {
    int index;

    if ((index = trunkList.indexOf( t)) < 0) return false;
    return removeTrunkAtIndex( index);
  }

  public boolean removeTrunkAtIndex( int index)
  {
    return (trunkList.remove( index) == null);
  }

     
    DavaBody(SootMethod m)
    {
        super(m);
    }

    public Object clone()
    {
        Body b = Dava.v().newBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    /**
        Constructs a DavaBody from the given Body.
     */

    DavaBody(Body body, Map options)
    {
        super(body.getMethod());

        entryTrunk = null;
	trunkList = new ArrayList();

        if(!(body instanceof GrimpBody))
            throw new RuntimeException("can only create a DavaBody from a GrimpBody!");
        
        GrimpBody grimpBody = (GrimpBody) body;

	originalUnits = new PatchingChain( new HashChain());
            
        // Convert all locals
        {
            Iterator localIt = grimpBody.getLocals().iterator();
            
            while(localIt.hasNext()) {

		Local l = (Local) localIt.next();
		Local newLocal;
		
		newLocal = Grimp.v().newLocal(l.getName(), l.getType());
		getLocals().add(newLocal);
	    }
        }

        // Import body contents from Grimp.
        {        
            HashMap bindings = new HashMap();
    
            Iterator it = grimpBody.getUnits().iterator();
    
            // Clone units in body's statement list 
            while(it.hasNext()) {
                Unit original = (Unit) it.next();
                                
                Unit copy = (Unit) original.clone();
                
                // Add cloned unit to our unitChain.
                getUnits().addLast(copy);
		originalUnits.addLast(copy);
    
                // Build old <-> new map to be able to patch up references to other units 
                // within the cloned units. (these are still refering to the original
                // unit objects).
                bindings.put(original, copy);
            }
    
            // Clone locals.
            it = grimpBody.getLocals().iterator();
            while(it.hasNext()) {
                Local original = (Local) it.next();
                Value copy = Dava.v().newLocal(original.getName(), original.getType());
                
                getLocals().addLast(copy);
    
                // Build old <-> new mapping.
                bindings.put(original, copy);
            }
            
    
            // Patch up references within units using our (old <-> new) map.
            it = getUnitBoxes().iterator();
            while(it.hasNext()) {
                UnitBox box = (UnitBox) it.next();
                Unit newObject, oldObject = box.getUnit();
                
                // if we have a reference to an old object, replace it 
                // it's clone.
                if( (newObject = (Unit)  bindings.get(oldObject)) != null )
                    box.setUnit(newObject);
                    
            }        
    
            // backpatch all local variables.
            it = getUseAndDefBoxes().iterator();
            while(it.hasNext()) {
                ValueBox vb = (ValueBox) it.next();
                if(vb.getValue() instanceof Local) 
                    vb.setValue((Value) bindings.get(vb.getValue()));
            }
        }    
    
        // Call transformers to recover structure
        {
            BlockStructurer.v().transform(this, "db.bs");
	    transformed = true;
	    while (transformed) {
		transformed = false;
		
		IfThenElseMatcher.v().transform(this, "db.item");
		WhileMatcher.v().transform( this, "db.wm");
		IfMatcher.v().transform(this, "db.im");
	    }
        }

	// Remove the Grimp units and put in the Dava units
	while (getUnits().size() > 0)
	    getUnits().removeLast();


	Iterator t_it = trunkList.iterator();
	while( t_it.hasNext()) {
	    getUnits().addLast( (Trunk) t_it.next());
	}


    }


    // clean up list of trunks by looking for trunks with their "removed" flags on and 
    // trunks having only one successor, one predecessor and the successor has only one
    // successor and one predecessor.
    
    public void clean() {
	Iterator it;
	Trunk t;
	boolean done = false;

	while (!done) {
	    done = true;

	    // first make a list of all the trunks to remove
	    it = trunkList.iterator();
	    tempList = new ArrayList();
	    while (it.hasNext())
		if ((t = ((Trunk) it.next())).removed())
		    tempList.add( t);
	    
	    // then remove those trunks
	    it = tempList.iterator();
	    while (it.hasNext())
		trunkList.remove( it.next());

	    boolean done_ttred = false;

	    // now hunt for trunks to make trunktrunks out of
	    while (!done_ttred) {
		done_ttred = true;
		tempList = new ArrayList();

		it = trunkList.iterator();
		while (it.hasNext())
		    if (findMatch( (Trunk) it.next())) {
			done = false;
			done_ttred = false;
		    }

		trunkList.addAll( tempList);
	    }
	}
    }
	
    private boolean findMatch( Trunk t) {
	Trunk t0;
	boolean found = false;

	if (t.removed())
	    return false;

	if (t.getSuccessors().size() == 1) {

	    t0 = (Trunk) t.getSuccessors().get(0);
	    if ((t0.getPredecessors().size() == 1) && (t0.getSuccessors().size() < 2)) {
		found = true;

		// create the new trunk to hold the two old ones
		TrunkTrunk ttm = Dava.v().newTrunkTrunk( t, t0);
		ttm.setPredecessorList( t.getPredecessors());
		ttm.setSuccessorList( t0.getSuccessors());
		
		// fix the successor links from the outside graph to the new trunk
		Iterator pit = ttm.getPredecessors().iterator();
		while (pit.hasNext()) {
		    Trunk pt = (Trunk) pit.next();
		    pt.getSuccessors().remove( t);
		    pt.addSuccessor( ttm);
		}
				
		// set the predecessor to point to our new trunk
		if (t0.getSuccessors().size() == 1) {
		    Trunk st = (Trunk) t0.getSuccessors().get(0);
		    st.getPredecessors().remove( t0);
		    st.getPredecessors().add( ttm);
		}
				
		// and nuke the old trunks
		t.setRemoved();
		t0.setRemoved();

		if (t.getLastStmt() instanceof GotoStmt) 
		    t.maskGotoStmt();
		
		// put in the new trunk
		tempList.add( ttm);
	    }
	}
	return found;
    }
}




