package soot.jimple.toolkits.thread.transaction;
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
import soot.jimple.toolkits.thread.*;
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
		return "All Fields" + (type == null ? "" : " (" + type + ")");
	}
	
	public int hashCode()
	{
		if(type == null)
			return 1;
		return type.hashCode();
	}
	
	public boolean equals(Object o)
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
	TransactionVisibleEdgesPred tve;
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
		this.tve = new TransactionVisibleEdgesPred(transactions);
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
	
	public RWSet readSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld, HashSet uses )
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

		boolean inaccessibleUses = false;
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
				// An approximation of their behavior must be performed here
				if( target.getDeclaringClass().toString().startsWith("java.util") ||
					target.getDeclaringClass().toString().startsWith("java.lang") )
				{
					if(stmt.getInvokeExpr() instanceof InstanceInvokeExpr)
					{
						Local base = (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase();

						// Add base object to set of possibly contributing uses at this stmt
						if(!inaccessibleUses)
						{
							uses.add(base);
							int argCount = stmt.getInvokeExpr().getArgCount();
							for(int i = 0; i < argCount; i++)
							{
								if(addValue( stmt.getInvokeExpr().getArg(i), method, stmt ) != null)
									uses.add(stmt.getInvokeExpr().getArg(i));
							}
						}
						
						// Add base object to read set
						ret = new SiteRWSet();
		            	List rDefs = new ArrayList();
		            	rDefs.addAll( sld.getDefsOfAt( base , (Unit) stmt ));
		            	for(int i = 0; i < rDefs.size(); i++) // TODO: is it possible that there's an infinite loop here?
		            	{
		            		Stmt rDef = (Stmt) rDefs.get(i);
		            		if(rDef instanceof DefinitionStmt)
		            		{
		            			Value r = ((DefinitionStmt) rDef).getRightOp();
		            			Value l = ((DefinitionStmt) rDef).getLeftOp();

		            			// If the rvalue is a local, we can find THAT local's defs
		            			if(r instanceof Local)
			            			rDefs.addAll(sld.getDefsOfAt( (Local) r , (Unit) rDef ));
			            		// If the rvalue is a field ref, add it to the read set
			            		else if(r instanceof FieldRef)
			            			ret.union(approximatedReadSet(method, stmt, r));
			            		// If the rvalue is something else, add the lvalue to the read set
			            		else
			            			ret.union(approximatedReadSet(method, stmt, l));
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
//						if(ntr.getFields().size() == 0)
//						{
							// if this read set consists only of globals, just add them to the uses for this stmt
//							uses.addAll(ntr.getGlobals()); // these are static SootFields, not StaticFieldRefs!!!
//						}
//						else
//						{
							uses.clear();
							inaccessibleUses = true; // can we improve lockset discovery? perhaps by using InfoFlow?
//						}
						if( ret == null ) ret = new SiteRWSet();
						ret.union( ntr );
					}
				}
			}
		}
		RWSet ntr = ntReadSet( method, stmt );
		
		if( inaccessibleUses == false && ntr != null && stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value r = a.getRightOp();
			if(r instanceof InstanceFieldRef)
			{
				uses.add( ((InstanceFieldRef)r).getBase() );
			}
			else if(r instanceof StaticFieldRef)
			{
				uses.add( r );
			}
			else if(r instanceof ArrayRef)
			{
				uses.add( ((ArrayRef)r).getBase() );
			}
		}
		
		if( ret == null ) return ntr;
		ret.union( ntr );
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
			else if( v instanceof FieldRef) // does this ever happen?
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
	
	public RWSet writeSet( SootMethod method, Stmt stmt, Transaction tn, LocalDefs sld, Set uses )
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

		boolean inaccessibleUses = false;
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
						Local base = (Local)((InstanceInvokeExpr)stmt.getInvokeExpr()).getBase();
						
						// Add base object to set of possibly contributing uses at this stmt
						if(!inaccessibleUses)
							uses.add(base);
						
						// Add base object to write set
						ret = new SiteRWSet();
		            	List rDefs = new ArrayList();
		            	rDefs.addAll( sld.getDefsOfAt( base , stmt ));
		            	for(int i = 0; i < rDefs.size(); i++)
		            	{
		            		Stmt rDef = (Stmt) rDefs.get(i);
		            		if(rDef instanceof DefinitionStmt)
		            		{
		            			Value r = ((DefinitionStmt) rDef).getRightOp();
		            			Value l = ((DefinitionStmt) rDef).getLeftOp();
		            			
		            			// If the rvalue is a local, we can find THAT local's defs
		            			if(r instanceof Local)
			            			rDefs.addAll(sld.getDefsOfAt( (Local)r , (Unit) rDef ));
			            		// If the rvalue is a field ref, add it to the write set
			            		else if(r instanceof FieldRef)
			            			ret.union(approximatedWriteSet(method, stmt, r));
			            		// If the rvalue is something else, add the lvalue to the write set
			            		else
			            			ret.union(approximatedWriteSet(method, stmt, l));
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
						uses.clear();
						inaccessibleUses = true;
						if( ret == null ) ret = new SiteRWSet();
						ret.union( ntw );
					}
				}
			}
		}

		RWSet ntw = ntWriteSet( method, stmt );
		if( !inaccessibleUses && ntw != null && stmt instanceof AssignStmt ) {
			AssignStmt a = (AssignStmt) stmt;
			Value l = a.getLeftOp();
			if(l instanceof InstanceFieldRef)
			{
				uses.add( ((InstanceFieldRef)l).getBase() );
			}
			else if(l instanceof StaticFieldRef)
			{
				uses.add( l );
			}
			else if(l instanceof ArrayRef)
			{
				uses.add( ((ArrayRef)l).getBase() );
			}
		}
		if( ret == null ) return ntw;
		ret.union( ntw );
		return ret;
	}
	
	protected RWSet addValue( Value v, SootMethod m, Stmt s ) {
		RWSet ret = null;
		
		if(tlo != null)
		{
			// fields/elements of local objects may be read/written w/o visible
			// side effects if the base object is local, or if the base is "this"
			// and the field itself is local (since "this" is always assumed shared)
			if( v instanceof InstanceFieldRef )
			{
				InstanceFieldRef ifr = (InstanceFieldRef) v;
				if( m.isConcrete() && !m.isStatic() && 
					m.retrieveActiveBody().getThisLocal().equivTo(ifr.getBase()) && 
					tlo.isObjectThreadLocal(ifr, m) )
					return null;
				else if( tlo.isObjectThreadLocal(ifr.getBase(), m) )
					return null;
			}
			else if( v instanceof ArrayRef && tlo.isObjectThreadLocal(((ArrayRef)v).getBase(), m) )
				return null;
		}
		
//		if(tlo != null && 
//			(( v instanceof InstanceFieldRef && tlo.isObjectThreadLocal(((InstanceFieldRef)v).getBase(), m) ) ||
//			 ( v instanceof ArrayRef && tlo.isObjectThreadLocal(((ArrayRef)v).getBase(), m) )))
//			return null;
		
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

