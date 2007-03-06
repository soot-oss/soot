package soot.jimple.toolkits.transaction;
/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.pointer.*;
import soot.toolkits.scalar.*;

import java.util.*;
import soot.util.*;

/** Generates side-effect information from a PointsToAnalysis. 
 *  Uses various heuristic rules to filter out side-effects that 
 *  are not visible to other threads in a Transactional program.
 */
class WholeObject
{
	Type type;
	
	public WholeObject(Type type)
	{
		this.type = type;
	}
	
	public WholeObject()
	{
		this.type = null;
	}

	public String toString()
	{
		return "Whole Object" + (type == null ? "" : " (" + type + ")");
	}
	
	public boolean equals(Object o)
	{
		if(o instanceof WholeObject)
		{
			G.v().out.println("Comparing " + toString() + " to " + ((WholeObject) o).toString() + ": " + (tempequals(o) ? "equal" : "not equal"));
		}
		else if(o instanceof FieldRef)
		{
			G.v().out.println("Comparing " + toString() + " to " + ((FieldRef)o).toString() + ": " + (tempequals(o) ? "equal" : "not equal"));
		}
		else if(o instanceof SootFieldRef)
		{
			G.v().out.println("Comparing " + toString() + " to " + ((SootFieldRef)o).toString() + ": " + (tempequals(o) ? "equal" : "not equal"));
		}
		else if(o instanceof SootField)
		{
			G.v().out.println("Comparing " + toString() + " to " + ((SootField)o).toString() + ": " + (tempequals(o) ? "equal" : "not equal"));
		}
		else
		{
			G.v().out.println("Comparing " + toString() + " to " + o.toString() + ": " + (tempequals(o) ? "equal" : "not equal"));
		}
		return tempequals(o);
	}
	
	public boolean tempequals(Object o)
	{
		if(type == null)
			return true;
		if(o instanceof WholeObject)
		{
			WholeObject other = (WholeObject) o;
			if(other.type == null)
				return true;
			else
				return (type == other.type);
		}
		else if(o instanceof FieldRef)
		{
			return type == ((FieldRef)o).getType();
		}
		else if(o instanceof SootFieldRef)
		{
			return type == ((SootFieldRef)o).type();
		}
		else if(o instanceof SootField)
		{
			return type == ((SootField)o).getType();
		}
		else
		{
			return true;
		}
	}
}

public class TransactionAwareSideEffectAnalysis {
	PointsToAnalysis pa;
	CallGraph cg;
	Map methodToNTReadSet = new HashMap();
	Map methodToNTWriteSet = new HashMap();
	int rwsetcount = 0;
	ThreadVisibleEdgesPred tve;
	TransitiveTargets tt;
	Collection transactions;
	EncapsulatedObjectAnalysis eoa;
	ThreadLocalObjectsAnalysis tlo;
	
	public Vector sigBlacklist;
	public Vector sigReadGraylist;
	public Vector sigWriteGraylist;
	public Vector subSigBlacklist;
	
	public void findNTRWSets( SootMethod method ) {
		if( methodToNTReadSet.containsKey( method )
				&& methodToNTWriteSet.containsKey( method ) ) return;
		
		MethodRWSet read = null;
		MethodRWSet write = null;
		for( Iterator sIt = method.retrieveActiveBody().getUnits().iterator(); sIt.hasNext(); ) {
			final Stmt s = (Stmt) sIt.next();
			
			boolean ignore = false;

			// Ignore Reads/Writes inside another transaction
			if(transactions != null)
			{
				Iterator tnIt = transactions.iterator();
				while(tnIt.hasNext())
				{
					Transaction tn = (Transaction) tnIt.next();
					if(tn.units.contains(s) || tn.prepStmt == s)
					{
						ignore = true;
						break;
					}
				}
			}
			
			if(!ignore)
			{
				RWSet ntr = ntReadSet( method, s );
				if( ntr != null ) {
					if( read == null ) read = new MethodRWSet();
					read.union( ntr );
				}
				RWSet ntw = ntWriteSet( method, s );
				if( ntw != null ) {
					if( write == null ) write = new MethodRWSet();
					write.union( ntw );
				}
			}
		}
		methodToNTReadSet.put( method, read );
		methodToNTWriteSet.put( method, write );
//		SootClass c = method.getDeclaringClass();
	}
	
	public RWSet nonTransitiveReadSet( SootMethod method ) {
		findNTRWSets( method );
		return (RWSet) methodToNTReadSet.get( method );
	}
	
	public RWSet nonTransitiveWriteSet( SootMethod method ) {
		findNTRWSets( method );
		return (RWSet) methodToNTWriteSet.get( method );
	}
	
	public TransactionAwareSideEffectAnalysis( PointsToAnalysis pa, CallGraph cg, Collection transactions, ThreadLocalObjectsAnalysis tlo ) {
		this.pa = pa;
		this.cg = cg;
		this.tve = new ThreadVisibleEdgesPred(transactions);
		this.tt = new TransitiveTargets( cg, new Filter(tve) );
		this.transactions = transactions;
		this.eoa = new EncapsulatedObjectAnalysis();
		this.tlo = tlo; // can be null
		
		sigBlacklist = new Vector(); // Signatures of methods known to have effective read/write sets of size 0
		// Math does not have any synchronization risks, we think :-)
/*		sigBlacklist.add("<java.lang.Math: double abs(double)>");
		sigBlacklist.add("<java.lang.Math: double min(double,double)>");
		sigBlacklist.add("<java.lang.Math: double sqrt(double)>");
		sigBlacklist.add("<java.lang.Math: double pow(double,double)>");
//*/
//		sigBlacklist.add("");

		sigReadGraylist = new Vector(); // Signatures of methods whose effects must be approximated
		sigWriteGraylist = new Vector();
		
/*		sigReadGraylist.add("<java.util.Vector: boolean remove(java.lang.Object)>");
		sigWriteGraylist.add("<java.util.Vector: boolean remove(java.lang.Object)>");

		sigReadGraylist.add("<java.util.Vector: boolean add(java.lang.Object)>");
		sigWriteGraylist.add("<java.util.Vector: boolean add(java.lang.Object)>");

		sigReadGraylist.add("<java.util.Vector: java.lang.Object clone()>");
//		sigWriteGraylist.add("<java.util.Vector: java.lang.Object clone()>");

		sigReadGraylist.add("<java.util.Vector: java.lang.Object get(int)>");
//		sigWriteGraylist.add("<java.util.Vector: java.lang.Object get(int)>");

		sigReadGraylist.add("<java.util.Vector: java.util.List subList(int,int)>");
//		sigWriteGraylist.add("<java.util.Vector: java.util.List subList(int,int)>");

		sigReadGraylist.add("<java.util.List: void clear()>");
		sigWriteGraylist.add("<java.util.List: void clear()>");
//*/
		subSigBlacklist = new Vector(); // Subsignatures of methods on all objects known to have read/write sets of size 0
/*		subSigBlacklist.add("java.lang.Class class$(java.lang.String)");
		subSigBlacklist.add("void notify()");
		subSigBlacklist.add("void notifyAll()");
		subSigBlacklist.add("void wait()");
		subSigBlacklist.add("void <clinit>()");
//*/
	}
	
	private RWSet ntReadSet( SootMethod method, Stmt stmt )
	{
		if( stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value r = a.getRightOp();
			if(r instanceof NewExpr) // IGNORE NEW STATEMENTS
				return null;
			return addValue( r, method, stmt );
		}
		return null;
	}
	
	public RWSet approximatedReadSet( SootMethod method, Stmt stmt, Value specialRead)
	{// used for stmts with method calls where the effect of the method call should be approximated by 0 or 1 reads (plus reads of all args)
		RWSet ret = new SiteRWSet();
		if(specialRead != null)
		{
			if( specialRead instanceof Local )
			{
				Local vLocal = (Local) specialRead;
				PointsToSet base = pa.reachingObjects( vLocal );
				StmtRWSet sSet = new StmtRWSet();
				sSet.addFieldRef( base, new WholeObject(vLocal.getType()) ); // we approximate that it's not a specific field, but the whole object
				ret.union(sSet);
			}
			else if( specialRead instanceof FieldRef)
			{
				ret.union(addValue(specialRead, method, stmt));
			}
		}
		if(stmt.containsInvokeExpr())
		{
			int argCount = stmt.getInvokeExpr().getArgCount();
			for(int i = 0; i < argCount; i++)
				ret.union(addValue( stmt.getInvokeExpr().getArg(i), method, stmt ));
		}
		if( stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value r = a.getRightOp();
			ret.union(addValue( r, method, stmt ));
		}
		return ret;
	}
	
	public RWSet transactionalReadSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld )
	{
		RWSet stmtRead = null;
		
/*		if( sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
		{
			if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			{
            	Iterator rDefsIt = sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt ).iterator();
            	while (rDefsIt.hasNext())
            	{
                	Stmt next = (Stmt) rDefsIt.next();
                	if(next instanceof DefinitionStmt)
					{
    					stmtRead = approximatedReadSet(method, stmt, ((DefinitionStmt) next).getRightOp() ); // IS THIS RIGHT?  SHOULD WE BE USING REACHING OBJECTS INSTEAD???
					}
				}
			}
			else
			{
				stmtRead = approximatedReadSet(method, stmt, null);
			}
		}
		else if( (sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
			     (subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
		{
			stmtRead = approximatedReadSet(method, stmt, null);
		}
		else
		{
*/
    		stmtRead = readSet( method, stmt, tn, sld );
//		}
		return stmtRead;
	}
	
	public RWSet readSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld )
	{
		boolean ignore = false;
		if(stmt.containsInvokeExpr())
		{
			InvokeExpr ie = stmt.getInvokeExpr();
			SootMethod calledMethod = ie.getMethod();
			if(ie instanceof StaticInvokeExpr)
			{
				// ignore = false;
			}
			else if(ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				
				if(calledMethod.getSubSignature().startsWith("void <init>") && eoa.isInitMethodPureOnObject(calledMethod))
				{
					ignore = true;
				}
				else if(tlo != null && !tlo.hasNonThreadLocalEffects(method, ie))
				{
					ignore = true;
				}
			}
		}

		RWSet ret = null;
		tve.setExemptTransaction(tn);
		Iterator targets = tt.iterator( stmt );
		while( !ignore && targets.hasNext() )
		{
			SootMethod target = (SootMethod) targets.next();
//			if( target.isNative() ) {
//				if( ret == null ) ret = new SiteRWSet();
//				ret.setCallsNative();
//			} else
			if( target.isConcrete() ) 
			{
				// TODO: FIX THIS!!!  What if the declaration is in a parent class of the actual object.
				
				// Special treatment for java.util and java.lang... their children are filtered out by the ThreadVisibleEdges filter
				// Any approximation of their behavior must be performed here
				if( target.getDeclaringClass().toString().startsWith("java.util") ||
					target.getDeclaringClass().toString().startsWith("java.lang") )
				{
					if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
					{
						ret = new SiteRWSet();
		            	List rDefs = new ArrayList();
		            	rDefs.addAll( sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , (Unit) stmt ));
		            	for(int i = 0; i < rDefs.size(); i++)
		            	{
		            		Stmt rDef = (Stmt) rDefs.get(i);
		            		if(rDef instanceof DefinitionStmt)
		            		{
		            			Value r = ((DefinitionStmt) rDef).getRightOp();
		            			if(r instanceof Local)
			            			rDefs.addAll(sld.getDefsOfAt( (Local) r , (Unit) rDef ));
			            		else
			            			ret.union(approximatedReadSet(method, stmt, r));
		            		}
		            	}
//		            	while (rDefsIt.hasNext())
//		            	{
//		                	Stmt next = (Stmt) rDefsIt.next();
//		                	if(next instanceof DefinitionStmt)
//							{
//		    					ret = approximatedReadSet(method, stmt, ((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() );
//							}
//						}
					}
					else
					{
						ret = approximatedReadSet(method, stmt, null);
					}
				}
/*				else if( sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
    			{
    				// This shouldn't happen because all graylisted methods should be by explicit invoke statement only
    				// approximatedReadSet() should be called instead
    			}
    			else if( (sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
					     (subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
				{
    				// No read set
				}
*/
				else
				{// note that all library functions have already been filtered out (by name) via the filter
				 // passed to the TransitiveTargets constructor.
//	            	G.v().out.println("Target   : " + target.toString());
					RWSet ntr = nonTransitiveReadSet(target);
					if( ntr != null ) {
						if( ret == null ) ret = new SiteRWSet();
						ret.union( ntr );
					}
				}
			}
		}
		if( ret == null ) return ntReadSet( method, stmt );
		ret.union( ntReadSet( method, stmt ) );
		return ret;
	}
	
	private RWSet ntWriteSet( SootMethod method, Stmt stmt ) {
		if( stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value l = a.getLeftOp();
			return addValue( l, method, stmt );
		}
		return null;
	}
	
	public RWSet approximatedWriteSet( SootMethod method, Stmt stmt, Value v )
	{// used for stmts with method calls where the effect of the method call should be approximated by 0 or 1 writes
		RWSet ret = new SiteRWSet();
		if(v != null)
		{
			if( v instanceof Local )
			{
				Local vLocal = (Local) v;
				PointsToSet base = pa.reachingObjects( vLocal );
				StmtRWSet sSet = new StmtRWSet();
				sSet.addFieldRef( base, new WholeObject(vLocal.getType()) ); // we approximate not a specific field, but the whole object
				ret.union(sSet);
			}
			else if( v instanceof FieldRef)
			{
				ret.union(addValue(v, method, stmt));
			}
		}
		if( stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value l = a.getLeftOp();
			ret.union(addValue( l, method, stmt ));
		}
		return ret;
	}
	
	public RWSet transactionalWriteSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld )
	{
		RWSet stmtWrite = null;
		
/*
		if( sigWriteGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
		{
			if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
			{
            	Iterator rDefsIt = sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt).iterator();
            	while (rDefsIt.hasNext())
            	{
                	Stmt next = (Stmt) rDefsIt.next();
                	if(next instanceof DefinitionStmt)
					{
    					stmtWrite = approximatedWriteSet(method, stmt, ((DefinitionStmt) next).getRightOp() );
					}
				}
			}
			else
			{
				stmtWrite = approximatedWriteSet(method, stmt, null);
			}
		}
		else if( sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
		{
			stmtWrite = approximatedWriteSet(method, stmt, null);
		}
		// add else ifs for every special case (specifically functions that write to args)
		else if( (sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
				 (subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
		{
			stmtWrite = approximatedWriteSet(method, stmt, null);
		}
		else
		{
*/
        	stmtWrite = writeSet( method, stmt, tn, sld );
//		}
		return stmtWrite;
	}
	
	public RWSet writeSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld )
	{
		boolean ignore = false;
		if(stmt.containsInvokeExpr())
		{
			InvokeExpr ie = stmt.getInvokeExpr();
			SootMethod calledMethod = ie.getMethod();
			if(ie instanceof StaticInvokeExpr)
			{
				// ignore = false;
			}
			else if(ie instanceof InstanceInvokeExpr)
			{
				InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
				
				if(calledMethod.getSubSignature().startsWith("void <init>") && eoa.isInitMethodPureOnObject(calledMethod))
				{
					ignore = true;
				}
				else if(tlo != null && !tlo.hasNonThreadLocalEffects(method, ie))
				{
					ignore = true;
				}
			}
		}

		RWSet ret = null;
		tve.setExemptTransaction(tn);
		Iterator targets = tt.iterator( stmt );
		while( !ignore && targets.hasNext() ) {
			SootMethod target = (SootMethod) targets.next();
//			if( target.isNative() ) {
//				if( ret == null ) ret = new SiteRWSet();
//				ret.setCallsNative();
//			} else
			if( target.isConcrete() )
			{
				if( target.getDeclaringClass().toString().startsWith("java.util") ||
					target.getDeclaringClass().toString().startsWith("java.lang") )
				{
					if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
					{
						ret = new SiteRWSet();
		            	List rDefs = new ArrayList();
		            	rDefs.addAll( sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt ));
		            	for(int i = 0; i < rDefs.size(); i++)
		            	{
		            		Stmt rDef = (Stmt) rDefs.get(i);
		            		if(rDef instanceof DefinitionStmt)
		            		{
		            			Value r = ((DefinitionStmt) rDef).getRightOp();
		            			if(r instanceof Local)
			            			rDefs.addAll(sld.getDefsOfAt( (Local)r , (Unit) rDef ));
			            		else
			            			ret.union(approximatedWriteSet(method, stmt, r));
		            		}
		            	}
//		            	Iterator rDefsIt = sld.getDefsOfAt( (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase() , stmt).iterator();
//		            	while (rDefsIt.hasNext())
//		            	{
//		                	Stmt next = (Stmt) rDefsIt.next();
//		                	if(next instanceof DefinitionStmt)
//							{
//		    					ret = approximatedWriteSet(method, stmt, ((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase());
//							}
//						}
					}
					else
					{
						ret = approximatedWriteSet(method, stmt, null);
					}
				}
/*
				else if( sigWriteGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
    			{
    				// No write set
    			}
    			else if( sigReadGraylist.contains(stmt.getInvokeExpr().getMethod().getSignature()) )
    			{
    				// No write set
    			}
    			else if( (sigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSignature())) ||
					     (subSigBlacklist.contains(stmt.getInvokeExpr().getMethod().getSubSignature())) )
				{
    				// No write set
				}
*/
				else
				{
					RWSet ntw = nonTransitiveWriteSet(target);
					if( ntw != null ) {
						if( ret == null ) ret = new SiteRWSet();
						ret.union( ntw );
					}
				}
			}
		}
		if( ret == null ) return ntWriteSet( method, stmt );
		ret.union( ntWriteSet( method, stmt ) );
		return ret;
	}
	
	protected RWSet addValue( Value v, SootMethod m, Stmt s ) {
		RWSet ret = null;
		
		if(tlo != null && v instanceof FieldRef && tlo.isObjectThreadLocal(v, m))
			return null;
		
		if( v instanceof InstanceFieldRef ) {
			InstanceFieldRef ifr = (InstanceFieldRef) v;
			PointsToSet base = pa.reachingObjects( (Local) ifr.getBase() );
			ret = new StmtRWSet();
			ret.addFieldRef( base, ifr.getField() );
		} else if( v instanceof StaticFieldRef ) {
			StaticFieldRef sfr = (StaticFieldRef) v;
			ret = new StmtRWSet();
			ret.addGlobal( sfr.getField() );
		} else if( v instanceof ArrayRef ) {
			ArrayRef ar = (ArrayRef) v;
			PointsToSet base = pa.reachingObjects( (Local) ar.getBase() );
			ret = new StmtRWSet();
			ret.addFieldRef( base, PointsToAnalysis.ARRAY_ELEMENTS_NODE );
		}
		return ret;
	}
	
	public String toString() {
		return "TransactionAwareSideEffectAnalysis: PA="+pa+" CG="+cg;
	}
}

