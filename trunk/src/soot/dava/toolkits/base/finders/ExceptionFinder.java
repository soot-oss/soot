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

import soot.*;

import java.io.Serializable;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;

public class ExceptionFinder implements FactFinder
{
    public ExceptionFinder( Singletons.Global g ) {}
    public static ExceptionFinder v() { return G.v().soot_dava_toolkits_base_finders_ExceptionFinder(); }

    public void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException
    {
	Dava.v().log( "ExceptionFinder::find()");

	Iterator it = body.get_ExceptionFacts().iterator();
	while (it.hasNext()) {
	    ExceptionNode en = (ExceptionNode) it.next();

	    if (body.get_SynchronizedBlockFacts().contains( en))
		continue;

	    IterableSet fullBody = new IterableSet();

	    Iterator cit = en.get_CatchList().iterator();
	    while (cit.hasNext()) 
		fullBody.addAll( (IterableSet) cit.next());
	    
	    fullBody.addAll( en.get_TryBody());

	    if (SET.nest( new SETTryNode( fullBody, en, asg, body)) == false)
		throw new RetriggerAnalysisException();
	}
    }

    public void preprocess( DavaBody body, AugmentedStmtGraph asg)
    {
	Dava.v().log( "ExceptionFinder::preprocess()");

	IterableSet enlist = new IterableSet();

	// Find the first approximation for all the try catch bodies.
	{
	    Iterator trapIt = body.getTraps().iterator();
	    while (trapIt.hasNext()) {
		Trap trap = (Trap) trapIt.next();
		Unit endUnit = trap.getEndUnit();

		// get the body of the try block as a raw read of the area of protection
		IterableSet tryBody = new IterableSet();
		
		Iterator btit = body.getUnits().iterator( trap.getBeginUnit());
		for (Unit u = (Unit) btit.next(); u != endUnit; u = (Unit) btit.next())
		    tryBody.add( asg.get_AugStmt( (Stmt) u));

		enlist.add( new ExceptionNode( tryBody, trap.getException(), asg.get_AugStmt( (Stmt) trap.getHandlerUnit())));
	    }
	}



	// Add in gotos that may escape the try body (created by the indirection introduced in DavaBody).
	{
	    Iterator enlit = enlist.iterator();
	    while (enlit.hasNext()) {
		ExceptionNode en = (ExceptionNode) enlit.next();
		IterableSet try_body = en.get_TryBody();
		
		Iterator tryIt = try_body.snapshotIterator();
		while (tryIt.hasNext()) {
		    AugmentedStmt tras = (AugmentedStmt) tryIt.next();
		    
		    Iterator ptIt = tras.cpreds.iterator();
		    while (ptIt.hasNext()) {
			AugmentedStmt pas = (AugmentedStmt) ptIt.next();
			Stmt ps = pas.get_Stmt();
			
			if ((try_body.contains( pas) == false) && (ps instanceof GotoStmt)) {
			    boolean add_it = true;
			    
			    Iterator cpit = pas.cpreds.iterator();
			    while (cpit.hasNext()) 
				if ((add_it = try_body.contains( cpit.next())) == false) 
				    break;
			    
			    if (add_it)
				en.add_TryStmt( pas);
			}
		    }
		}
	    }
	}









	// Split up the try blocks until they cause no nesting problems.
    splitLoop:
	while (true)
	{
	    // refresh the catch bodies
	    {
		Iterator enlit = enlist.iterator();
		while (enlit.hasNext())
		    ((ExceptionNode) enlit.next()).refresh_CatchBody( this);
	    }

	    // split for inter-exception nesting problems
	    {
		ExceptionNode[] ena = new ExceptionNode[ enlist.size()];
		Iterator enlit = enlist.iterator();
		for (int i=0; enlit.hasNext(); i++) 
		    ena[ i] = (ExceptionNode) enlit.next();
		
		for (int i=0; i<ena.length-1; i++) {
		    ExceptionNode eni = ena[i];
		    for (int j=i+1; j<ena.length; j++) {
			ExceptionNode enj = ena[j];
			
			IterableSet 
			    eniTryBody = eni.get_TryBody(),
			    enjTryBody = enj.get_TryBody();

			if ((eniTryBody.equals( enjTryBody) == false) && (eniTryBody.intersects( enjTryBody))) {

			    if ((eniTryBody.isSupersetOf( enj.get_Body())) ||
				(enjTryBody.isSupersetOf( eni.get_Body())))

				continue;

			    IterableSet newTryBody = eniTryBody.intersection( enjTryBody);

			    if (newTryBody.equals( enjTryBody))
				eni.splitOff_ExceptionNode( newTryBody, asg, enlist);
			    else 
				enj.splitOff_ExceptionNode( newTryBody, asg, enlist);

			    continue splitLoop;
			}
		    }
		}
	    }

	    // split for intra-try-body issues
	    {
		Iterator enlit = enlist.iterator();
		while (enlit.hasNext()) {
		    ExceptionNode en = (ExceptionNode) enlit.next();

		    // Get the try block entry points
		    IterableSet tryBody = en.get_TryBody();
		    LinkedList<AugmentedStmt> heads = new LinkedList<AugmentedStmt>();
		    Iterator trIt = tryBody.iterator();
		    while (trIt.hasNext()) {
			AugmentedStmt as = (AugmentedStmt) trIt.next();

			if (as.cpreds.isEmpty()) {
			    heads.add( as);
			    continue;
			}

			Iterator pit = as.cpreds.iterator();
			while (pit.hasNext()) 
			    if (tryBody.contains( pit.next()) == false) {
				heads.add( as);
				break;
			    }
		    }

		    HashSet<AugmentedStmt> touchSet = new HashSet<AugmentedStmt>();
		    touchSet.addAll( heads);

		    // Break up the try block for all the so-far detectable parts.
		    AugmentedStmt head = heads.removeFirst();
		    IterableSet subTryBlock = new IterableSet();
		    LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();
		    
		    worklist.add( head);
		    
		    while (worklist.isEmpty() == false) {
			AugmentedStmt as = worklist.removeFirst();
			
			subTryBlock.add( as);
			Iterator sit = as.csuccs.iterator();
			while (sit.hasNext()) {
			    AugmentedStmt sas = (AugmentedStmt) sit.next();
			    
			    if ((tryBody.contains( sas) == false) || (touchSet.contains( sas)))
				continue;
			    
			    touchSet.add( sas);
			    
			    if (sas.get_Dominators().contains( head))
				worklist.add( sas);
			    else  
				heads.addLast( sas);
			}
		    }
		    
		    if (heads.isEmpty() == false) {
			en.splitOff_ExceptionNode( subTryBlock, asg, enlist);
			continue splitLoop;
		    }
		}   
	    }

	    break;
	}

	// Aggregate the try blocks.
	{
	    LinkedList<ExceptionNode> reps = new LinkedList<ExceptionNode>();
	    HashMap<Serializable, LinkedList<IterableSet>>
	    	hCode2bucket = new HashMap<Serializable, LinkedList<IterableSet>>();	    
		HashMap<Serializable, ExceptionNode> 
			tryBody2exceptionNode = new HashMap<Serializable, ExceptionNode>();
	    
	    Iterator enlit = enlist.iterator();
	    while (enlit.hasNext()) {
		ExceptionNode en = (ExceptionNode) enlit.next();

		int hashCode = 0;
		IterableSet curTryBody = en.get_TryBody();

		Iterator trit = curTryBody.iterator();
		while (trit.hasNext()) 
		    hashCode ^= trit.next().hashCode();
		Integer I = new Integer( hashCode);

		LinkedList<IterableSet> bucket = hCode2bucket.get( I);
		if (bucket == null) {
		    bucket = new LinkedList<IterableSet>();
		    hCode2bucket.put( I, bucket);
		}

		ExceptionNode repExceptionNode = null;

		Iterator<IterableSet> bit = bucket.iterator();
		while (bit.hasNext()) {
		    IterableSet bucketTryBody = bit.next();
			
		    if (bucketTryBody.equals( curTryBody)) {
			repExceptionNode = tryBody2exceptionNode.get( bucketTryBody);
			break;
		    }
		}
		    
		if (repExceptionNode == null) {
		    tryBody2exceptionNode.put( curTryBody, en);
		    bucket.add( curTryBody);
		    reps.add( en);
		}
		else 
		    repExceptionNode.add_CatchBody( en);
	    }

	    enlist.clear();
	    enlist.addAll( reps);
	}

	body.get_ExceptionFacts().clear();
	body.get_ExceptionFacts().addAll( enlist);







    }

    public IterableSet get_CatchBody( AugmentedStmt handlerAugmentedStmt) 
    {
	IterableSet catchBody = new IterableSet();
	LinkedList catchQueue = new LinkedList();
	
	catchBody.add( handlerAugmentedStmt);	    
	catchQueue.addAll( handlerAugmentedStmt.csuccs);
	
	while (catchQueue.isEmpty() == false) {
	    AugmentedStmt as = (AugmentedStmt) catchQueue.removeFirst();
	    
	    if (catchBody.contains( as))
		continue;
	    
	    if (as.get_Dominators().contains( handlerAugmentedStmt)) {
		catchBody.add( as);
		catchQueue.addAll( as.csuccs);
	    }
	}

	return catchBody;
    }
}
