/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-2004.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


 



package soot.toolkits.graph;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.options.Options;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.exceptions.ThrowableSet;
import soot.baf.Inst;
import soot.baf.NewInst;
import soot.baf.StaticPutInst;
import soot.baf.StaticGetInst;
import soot.baf.ThrowInst;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewExpr;


/**
 *  <p>Represents a CFG for a {@link Body} instance where the nodes are
 *  {@link Unit} instances, and where control flow associated with
 *  exceptions is taken into account.</p> 
 *
 *  <p>For every <tt>Unit</tt> which
 *  may throw an exception that could be caught by a {@link Trap} in
 *  the <tt>Body</tt>, there will be an edge from each of the
 *  excepting <tt>Unit</tt>'s predecessors to the <tt>Trap</tt>
 *  handler's first <tt>Unit</tt> (since any of those predecessors may have
 *  been the last <tt>Unit</tt> to complete execution before the
 *  handler). If the excepting <tt>Unit</tt> might have the side
 *  effect of changing some fields, then there will be an edge from
 *  the excepting <tt>Unit</tt> itself to its handlers, since the
 *  side effects might occur before the exception is raised. If the 
 *  excepting <tt>Unit</tt> has no side effects, then parameters passed
 *  to the <tt>ExceptionalUnitGraph</tt> constructor determine whether
 *  or not there is an edge from the excepting <tt>Unit</tt> itself to
 *  the handler <tt>Unit</tt>.</p>
 */
public class ExceptionalUnitGraph extends UnitGraph
{
    protected Map unitToUnexceptionalSuccs; // If there are no Traps within
    protected Map unitToUnexceptionalPreds; // the method, these will be the
					    // same maps as unitToSuccs and
					    // unitToPreds.

    protected Map unitToExceptionalSuccs; // These will be null if 
    protected Map unitToExceptionalPreds; // there are no Traps within 
    protected Map unitToExceptionDests;   // the method,

    protected ThrowAnalysis throwAnalysis; // This will be null unless there
					   // are no Traps within the
					   // method, in which case we
					   // keep the throwAnalysis around
					   // for getExceptionDests().


    /**
     *  Constructs the graph from a given Body instance.
     *
     *  @param body the <tt>Body</tt> from which to build a graph.
     *
     *  @param throwAnalysis the source of information about the exceptions
     *                       which each {@link Unit} may throw.
     *
     *  @param omitExceptingUnitEdges indicates whether the CFG should
     *			     omit edges to a handler from trapped
     *			     <tt>Unit</tt>s which may throw an
     *			     exception which the handler catches but
     *			     which have no potential side effects.
     *			     The CFG will contain edges to the handler
     *			     from all predecessors of the
     *			     <tt>Unit</tt>s which may throw a caught
     *			     exception regardless of the setting for
     *			     this parameter. If this parameter is
     *			     <code>false</code>, there will also be
     *			     edges to the handler from all the
     *			     potentially excepting <tt>Unit</tt>s
     *			     themselves. If this parameter is
     *			     <code>true</code>, there will be edges to
     *			     the handler from the excepting
     *			     <tt>Unit</tt>s themselves only if they
     *			     have potential side effects (or if they 
     *			     are themselves the predecessors of other
     *			     potentially excepting <tt>Unit</tt>s).
     *			     A setting of <code>true</code> produces
     *			     CFGs which allow for more precise
     *			     analyses, since a <tt>Unit</tt> without
     *			     side effects has no effect on the
     *			     computational state when it throws an
     *			     exception.  Use settings of
     *			     <code>false</code> for compatibility with
     *			     more conservative analyses, or to cater
     *			     to conservative bytecode verifiers.
     */
    public ExceptionalUnitGraph(Body body, ThrowAnalysis throwAnalysis,
				boolean omitExceptingUnitEdges) {
	super(body);

	int size = unitChain.size();
	Set trapsThatAreHeads = Collections.EMPTY_SET;
        
        if(Options.v().time())
            Timers.v().graphTimer.start();
        
	unitToUnexceptionalSuccs = new HashMap(size * 2 + 1, 0.7f);
	unitToUnexceptionalPreds = new HashMap(size * 2 + 1, 0.7f);
	buildUnexceptionalEdges(unitToUnexceptionalSuccs, 
				unitToUnexceptionalPreds);
	makeMappedListsUnmodifiable(unitToUnexceptionalSuccs);
	makeMappedListsUnmodifiable(unitToUnexceptionalPreds);

	if (body.getTraps().size() == 0) {
	    // No handlers, so all exceptional control flow exits the
	    // method.
	    unitToExceptionalSuccs = null;
	    unitToExceptionalPreds = null;
	    unitToSuccs = unitToUnexceptionalSuccs;
	    unitToPreds = unitToUnexceptionalPreds;
	    this.throwAnalysis = throwAnalysis;	// Keep the throwAnalysis,
						// for generating ThrowableSets
						// on the fly.

	} else {
	    unitToExceptionDests = buildExceptionDests(throwAnalysis);
	    unitToExceptionalSuccs = 
		new HashMap(unitToExceptionDests.size() * 2 + 1, 0.7f);
	    unitToExceptionalPreds = 
		new HashMap(body.getTraps().size() * 2 + 1, 0.7f);
	    trapsThatAreHeads = buildExceptionalEdges(unitToExceptionDests,
						      unitToExceptionalSuccs, 
						      unitToExceptionalPreds,
						      omitExceptingUnitEdges);
	    makeMappedListsUnmodifiable(unitToExceptionalSuccs);
	    makeMappedListsUnmodifiable(unitToExceptionalPreds);

	    // We'll need separate maps for the combined 
	    // exceptional and unexceptional edges:
	    unitToSuccs = combineMapValues(unitToUnexceptionalSuccs, 
					   unitToExceptionalSuccs);
	    unitToPreds = combineMapValues(unitToUnexceptionalPreds, 
					   unitToExceptionalPreds);

	    this.throwAnalysis = null; // We won't need throwAnalysis
	                               // anymore, so don't save a reference
                    	               // that might keep its object live.
	}

	buildHeadsAndTails(trapsThatAreHeads);

        if(Options.v().time())
            Timers.v().graphTimer.end();        

	if (DEBUG)
	    soot.util.PhaseDumper.v().dumpGraph(this);
    }


    /**
     *  Constructs the graph from a given Body instance.  This
     *  constructor variant uses a default value, provided by the
     *  {@link Options} class, for the
     *  <code>omitExceptingUnitEdges</code> parameter.
     *
     *  @param body the {@link Body} from which to build a graph.
     *
     *  @param throwAnalysis the source of information about the exceptions
     *                       which each {@link Unit} may throw.
     *
     */
    public ExceptionalUnitGraph(Body body, ThrowAnalysis throwAnalysis) {
	this(body, throwAnalysis, Options.v().omit_excepting_unit_edges());
    }

    /**
     *  Constructs the graph from a given Body instance, using the
     *  {@link Scene}'s default {@link ThrowAnalysis} to estimate the
     *  set of exceptions that each {@link Unit} might throw and a
     *  default value, provided by the {@link Options} class, for the
     *  <code>omitExceptingUnitEdges</code> parameter.
     *
     *  @param body the <tt>Body</tt> from which to build a graph.
     *
     */
    public ExceptionalUnitGraph(Body body) {
	this(body, Scene.v().getDefaultThrowAnalysis(),
	     Options.v().omit_excepting_unit_edges());
    }


    /**
     * Utility method used in the construction of {@link UnitGraph}
     * variants which include exceptional control flow. It determines
     * the possible exceptions which each <tt>Unit</tt> might throw
     * and the set of traps which might catch those exceptions.
     *
     * @param throwAnalysis The source of information about which
     *			    exceptions each <tt>Unit</tt> may throw.
     *
     * @return A {@link Map} from <tt>Unit</tt>s to {@link Collection}s of
     *         {@link ExceptionDest}s. Each <tt>ExceptionDest</tt> associated
     *         with a <tt>Unit</tt> includes a {@link ThrowableSet} specifying 
     *         exceptions that the <tt>Unit</tt> might throw and a
     *         {@link Trap} specifying the handler which catches those exceptions
     *         (if the <tt>Trap</tt> is <tt>null</tt>, those exceptions 
     *         escape the method without being caught).
     */
    protected Map buildExceptionDests(ThrowAnalysis throwAnalysis) {
	// To add possible exception edges, we need to keep
	// track of which traps are active as we iterate
	// through the units of the body, and we need to check
	// exceptions against active traps in the proper
	// order, to ensure we choose the correct active trap
	// when more than one of them can catch a particular
	// exception type.  Hence the trapTable array.
	class TrapRecord {
	    Trap trap;
	    RefType caughtType;
	    boolean active;

	    TrapRecord(Trap trap, RefType caughtType) {
		this.trap = trap;
		this.caughtType = caughtType;
		active = false;
	    }
	}
	TrapRecord[] trapTable = new TrapRecord[body.getTraps().size()];
	int trapIndex = 0;
	for (Iterator trapIt = body.getTraps().iterator(); 
	     trapIt.hasNext(); trapIndex++) {
	    Trap trap = (Trap) trapIt.next();
	    trapTable[trapIndex] = new TrapRecord(trap, 
						  RefType.v(trap.getException()));
	}

	Map unitToExceptionDests = new HashMap(unitChain.size() * 2 + 1, 0.7f);
	FastHierarchy hier = Scene.v().getOrMakeFastHierarchy();

	// Encode the possible destinations of each Unit's
	// exceptions in ExceptionDest objects.
	for (Iterator unitIt = unitChain.iterator(); unitIt.hasNext(); ) {
	    Unit u = (Unit) unitIt.next(); 

	    // First, update the set of traps active for this Unit.
	    for (int i = 0; i < trapTable.length; i++) {
		// Be sure to turn on traps before turning them off, so
		// that degenerate traps where getBeginUnit() == getEndUnit()
		// are never active. Obviously, I learned this the hard way!
		if (trapTable[i].trap.getBeginUnit() == u) {
		    trapTable[i].active = true;
		}
		if (trapTable[i].trap.getEndUnit() == u) {
		    trapTable[i].active = false;
		}
	    }

	    UnitDestsMap unitDests = new UnitDestsMap();
	    ThrowableSet thrownSet = throwAnalysis.mightThrow(u);

	    nextThrowable:
	    for (Iterator i = thrownSet.types().iterator(); i.hasNext(); ) {
		RefLikeType thrown = (RefLikeType)i.next();
		if (thrown instanceof RefType) {
		    RefType thrownType = (RefType)thrown;
		    for (int j = 0; j < trapTable.length; j++) {
			if (trapTable[j].active) {
			    Trap trap = trapTable[j].trap;
			    RefType caughtType = trapTable[j].caughtType;
			    if (hier.canStoreType(thrownType, caughtType)) {
				unitDests.add(trap, thrownType);
				continue nextThrowable;
			    }
			}
		    }
		    // If we get here, thrownType escapes the method.
		    unitDests.add(null, thrownType);

		} else if (thrown instanceof AnySubType) {
		    AnySubType thrownType = (AnySubType) thrown;
		    RefType thrownBase = thrownType.getBase();
		    List alreadyCaught = new ArrayList();
		    // alreadyCaught serves to screen a special case
		    // (which is probably not worth screening) best
		    // illustrated with a concrete example: imagine
		    // that thrownBase is AnySubType(RuntimeException)
		    // and that we have two Traps, the first of
		    // which catches IndexOutOfBoundsException while
		    // the second catches ArrayIndexOutOfBounds-
		    // Exception.  We use alreadyCaught to detect that
		    // the second Trap will never catch anything.
		    // (javac would warn you that the catch block is dead,
		    // but arbitrary bytecode could still include such
		    // dead handlers.)

		    for (int j = 0; j < trapTable.length; j++) {
			if (trapTable[j].active) {
			    Trap trap = trapTable[j].trap;
			    RefType caughtType = trapTable[j].caughtType;
			    if (hier.canStoreType(thrownBase, caughtType)) {
				// This trap catches all the types
				// thrownType can represent.
				unitDests.add(trap, AnySubType.v(thrownBase));
				continue nextThrowable;
			    } else if (hier.canStoreType(caughtType, thrownBase)) {
				// This trap catches some of the
				// types thrownType can represent,
				// unless it is being screened by a
				// previous trap whose catch parameter
				// is a supertype of this one's.
				for (Iterator k = alreadyCaught.iterator();
				     k.hasNext(); ) {
				    RefType alreadyType = (RefType)k.next();
				    if (hier.canStoreType(caughtType, alreadyType)) {
					continue nextThrowable;
				    }
				}
				unitDests.add(trap, AnySubType.v(caughtType));
				alreadyCaught.add(caughtType);
				// Do NOT continue to the nextThrowable, 
				// since the remaining traps might catch
				// other subtypes of this thrownBase.
			    }
			}
		    }

		    // At least some subtypes escape the method.
		    // We really should subtract any already caught 
		    // subtypes from the representation of escaping
		    // exceptions, but there's no easy way to do so.
		    unitDests.add(null, thrownType);
						    
		} else {
		    // assertion failure.
		    throw new RuntimeException("UnitGraph(): ThrowableSet element " +
					       thrown.toString() +
					       " is neither a RefType nor an AnySubType.");
		}
	    }
	    unitToExceptionDests.put(u, unitDests.values());
	}
	return unitToExceptionDests;
    }


    /** A utility class used to build the list of ExceptionDests for a
     * given unit. It maps from
     * {@link Trap}s to {@link ThrowableSet}s representing the exceptions
     * thrown by the unit under consideration that are caught by that trap.
     */
    private static class UnitDestsMap extends HashMap {

	// We use leavesMethod in the unitDests map to represent the
	// destination of exceptions which are not caught by any
	// trap in the method.  We can't use null, because it
	// doesn't have a hashCode(). It is perhaps bad form to use
	// Collections.EMPTY_LIST as the placeholder, but it is
	// already used elsewhere in UnitGraph, and using EMPTY_LIST
	// saves us from creating a dummy Trap that we would need to 
	// add to Singletons.
	final static Object leavesMethod = Collections.EMPTY_LIST;
		    
	private ExceptionDest getDest(Trap trap) {
	    Object key = trap;
	    if (key == null) {
		key = leavesMethod;
	    }
	    ExceptionDest dest = (ExceptionDest)this.get(key);
	    if (dest == null) {
		dest = new ExceptionDest(trap, ThrowableSet.Manager.v().EMPTY);
		this.put(key, dest);
	    }
	    return dest;
	}

	void add(Trap trap, RefType throwable) {
	    ExceptionDest dest = this.getDest(trap);
	    dest.throwables = dest.throwables.add(throwable);
	}

	void add(Trap trap, AnySubType throwable) {
	    ExceptionDest dest = this.getDest(trap);
	    dest.throwables = dest.throwables.add(throwable);
	}
    }


    /**
     * Method to compute the edges corresponding to exceptional
     * control flow.
     *
     * @param unitToDests A <tt>Map</tt> from {@link Unit}s to 
     *                    {@link Collection}s of {@link ExceptionalUnitGraph.ExceptionDest ExceptionDest}s
     *                    which represent the handlers that might catch
     *                    exceptions thrown by the <tt>Unit</tt>. This is
     *                    an ``in parameter''.
     *
     * @param unitToSuccs A <tt>Map</tt> from <tt>Unit</tt>s to 
     *                    {@link List}s of <tt>Unit</tt>s. This is an
     *                    ``out parameter'';
     *                    <tt>buildExceptionalEdges</tt> will add a
     *                    mapping from every <tt>Unit</tt> in the body
     *                    that may throw an exception that could be
     *                    caught by a {@link Trap} in the body to a
     *                    list of its exceptional successors.
     *
     * @param unitToPreds A <tt>Map</tt> from <tt>Unit</tt>s to 
     *                    <tt>List</tt>s of <tt>Unit</tt>s. This is an
     *                    ``out parameter'';
     *                    <tt>buildExceptionalEdges</tt> will add a
     *                    mapping from each handler unit that may
     *                    catch an exception to the list of
     *                    <tt>Unit</tt>s whose exceptions it may
     *                    catch.
     * @param omitExceptingUnitEdges Indicates whether to omit
     *			  exceptional edges from excepting units which
     *			  lack side effects
     *
     * @return a {@link Set} of trap <tt>Unit</tt>s that might catch 
     *         exceptions thrown by the first <tt>Unit</tt> in the {@link Body}
     *         associated with the graph being constructed. 
     *         Such trap <tt>Unit</tt>s may need to be added to the
     *         list of heads (depending on your definition of heads),
     *         since they can be the first <tt>Unit</tt> in the
     *         <tt>Body</tt> which actually completes execution.
     */
    protected Set buildExceptionalEdges(Map unitToDests, 
					Map unitToSuccs, Map unitToPreds,
					boolean omitExceptingUnitEdges) {
	Set trapsThatAreHeads = new ArraySet();
	Unit entryPoint = (Unit) unitChain.getFirst();

	// Add exceptional edges from each predecessor of units that
	// throw exceptions to the handler that catches them.  Add an
	// additional edge from the thrower itself to the catcher if
	// the thrower may have side effects.
	for (Iterator it = unitToDests.entrySet().iterator();
	     it.hasNext(); ) {
	    Map.Entry entry = (Map.Entry) it.next();
	    Unit thrower = (Unit) entry.getKey();
	    Collection dests = (Collection) entry.getValue();
	    for (Iterator destIt = dests.iterator(); destIt.hasNext(); ) {
		ExceptionDest dest = (ExceptionDest) destIt.next();
		if (dest.trap() != null) {
		    Unit catcher = dest.trap().getHandlerUnit();
		    List throwersPreds = getUnexceptionalPredsOf(thrower);
		    if (thrower == entryPoint) {
			trapsThatAreHeads.add(catcher);
		    } else {
			for (Iterator j = throwersPreds.iterator(); 
			     j.hasNext(); ) {
			    Unit pred = (Unit) j.next();
			    addEdge(unitToSuccs, unitToPreds, pred, catcher);
			}
		    }
		    if ((! omitExceptingUnitEdges) ||
			thrower instanceof ThrowInst ||
			thrower instanceof ThrowStmt || 
			mightHaveSideEffects(thrower)) {
			// An athrow instruction actually completes when
			// it throws its argument exception, so we
			// need to include an edge from it to avoid
			// the throw being removed by dead code
			// elimination.
			addEdge(unitToSuccs, unitToPreds, thrower, catcher);
		    }
		}
	    }
	}
			
	// Now we have to worry about transitive exceptional
	// edges, when a handler might itself throw an exception
	// that is caught within the method.  For that we need a
	// worklist containing CFG edges that lead to such a handler.
	class CFGEdge {
	    Unit head;		// If null, represents an edge to the handler
				// from the fictitious "predecessor" of the 
				// very first unit in the chain. I.e., tail
				// is a handler which might be reached as a
				// result of an exception thrown by the
				// first Unit in the Body.
	    Unit tail;

	    CFGEdge(Unit head, Unit tail) {
		if (tail == null)
		    throw new RuntimeException("invalid CFGEdge(" 
					       + head.toString() + ',' 
					       + tail.toString() + ')');
		this.head = head;
		this.tail = tail;
	    }

	    public boolean equals(Object rhs) {
		if (rhs == this) {
		    return true;
		}
		if (! (rhs instanceof CFGEdge)) {
		    return false;
		}
		CFGEdge rhsEdge = (CFGEdge) rhs;
		return ((this.head == rhsEdge.head) && 
			(this.tail == rhsEdge.tail));
	    }

	    public int hashCode() {
		// Following Joshua Bloch's recipe in "Effective Java", Item 8:
		int result = 17;
		result = 37 * result + this.head.hashCode();
		result = 37 * result + this.tail.hashCode();
		return result;
	    }
	}

	LinkedList workList = new LinkedList();

	for (Iterator trapIt = body.getTraps().iterator(); trapIt.hasNext(); ) {
	    Trap trap = (Trap) trapIt.next();
	    Unit handlerStart = trap.getHandlerUnit();
	    if (mightThrowToIntraproceduralCatcher(handlerStart)) {
		List handlerPreds = getUnexceptionalPredsOf(handlerStart);
		for (Iterator it = handlerPreds.iterator(); it.hasNext(); ) {
		    Unit pred = (Unit) it.next();
		    workList.addLast(new CFGEdge(pred, handlerStart));
		}
		handlerPreds = getExceptionalPredsOf(handlerStart);
		for (Iterator it = handlerPreds.iterator(); it.hasNext(); ) {
		    Unit pred = (Unit) it.next();
		    workList.addLast(new CFGEdge(pred, handlerStart));
		}
		if (trapsThatAreHeads.contains(handlerStart)) {
		    workList.addLast(new CFGEdge(null, handlerStart));
		}
	    }
	}

	// Now for every CFG edge that leads to a handler that may
	// itself throw an exception catchable within the method, add
	// edges from the head of that edge to the unit that catches
	// the handler's exception.
	while (workList.size() > 0) {
	    CFGEdge edgeToThrower = (CFGEdge) workList.removeFirst();
	    Unit pred = edgeToThrower.head;
	    Unit thrower = edgeToThrower.tail;
	    Collection throwerDests = getExceptionDests(thrower);
	    for (Iterator i = throwerDests.iterator(); i.hasNext(); ) {
		ExceptionDest dest = (ExceptionDest) i.next();
		if (dest.trap() != null) {
		    Unit handlerStart = dest.trap().getHandlerUnit();
		    boolean edgeAdded = false;
		    if (pred == null) {
			if (! trapsThatAreHeads.contains(handlerStart)) {
			    trapsThatAreHeads.add(handlerStart);
			    edgeAdded = true;
			}
		    } else {
			if (! getExceptionalSuccsOf(pred).contains(handlerStart)) {
			    addEdge(unitToSuccs, unitToPreds, pred, handlerStart);
			    edgeAdded = true;
			}
		    }
		    if (edgeAdded && 
			mightThrowToIntraproceduralCatcher(handlerStart)) {
			workList.addLast(new CFGEdge(pred, handlerStart));
		    }
		}
	    }
	}
	return trapsThatAreHeads;
    }


    /**
     * <p>Utility method for checking if a {@link Unit} might have side
     * effects.  It simply returns true for any unit which invokes a
     * method directly or which might invoke static initializers
     * indirectly (by creating a new object or by refering to a static
     * field; see sections 2.17.4, 2.17.5, and 5.5 of the Java Virtual
     * Machine Specification).</p>
     *
     * <tt>mightHaveSideEffects()</tt> is declared package-private so that
     * it is available to unit tests that are part of this package.
     *
     * @param u     The unit whose potential for side effects is to be checked.
     *
     * @return whether or not <code>u</code> has the potential for side effects.
     */
    static boolean mightHaveSideEffects(Unit u) {
	if (u instanceof Inst) {
	    Inst i = (Inst) u;
	    return (i.containsInvokeExpr() || 
		    (i instanceof StaticPutInst) || 
		    (i instanceof StaticGetInst) || 
		    (i instanceof NewInst));
	} else if (u instanceof Stmt) {
	    for (Iterator it = u.getUseBoxes().iterator(); it.hasNext(); ) {
		Value v = ((ValueBox)(it.next())).getValue();
		if ((v instanceof StaticFieldRef) || 
		    (v instanceof InvokeExpr) ||
		    (v instanceof NewExpr)) {
		    return true;
		}
	    }
	}
	return false;
    }


    /**
     * Utility method for checking if a Unit might throw an exception which
     * may be caught by a {@link Trap} within this method.  
     *
     * @param u     The unit for whose exceptions are to be checked
     *
     * @return whether or not <code>u</code> may throw an exception which may be
     *              caught by a <tt>Trap</tt> in this method,
     */
    private boolean mightThrowToIntraproceduralCatcher(Unit u) {
	Collection dests = getExceptionDests(u);
	for (Iterator i = dests.iterator(); i.hasNext(); ) {
	    ExceptionDest dest = (ExceptionDest) i.next();
	    if (dest.trap() != null) {
		return true;
	    }
	}
	return false;
    }


    /**
     * Utility method, to be called only after the unitToPreds and
     * unitToSuccs maps have been built. It defines the graph's set of
     * heads to include the first {@link Unit} in the graph's body,
     * together with all the <tt>Unit</tt>s in
     * <tt>additionalHeads</tt>.  It defines the graph's set of tails
     * to include all <tt>Unit</tt>s which represent some sort of
     * return bytecode or an athrow bytecode which may escape the method.
     */
    private void buildHeadsAndTails(Set additionalHeads) {
	List headList = new ArrayList(additionalHeads.size() + 1);
	headList.addAll(additionalHeads);
	Unit entryPoint = (Unit) unitChain.getFirst();
	if (! headList.contains(entryPoint)) {
	    headList.add(entryPoint);
	}
	
	List tailList = new ArrayList();
	for (Iterator it = unitChain.iterator(); it.hasNext(); ) {
	    Unit u = (Unit) it.next();
	    if (u instanceof soot.jimple.ReturnStmt ||
		u instanceof soot.jimple.ReturnVoidStmt ||
		u instanceof soot.baf.ReturnInst ||
		u instanceof soot.baf.ReturnVoidInst) {
		tailList.add(u);
	    } else if (u instanceof soot.jimple.ThrowStmt ||
		       u instanceof soot.baf.ThrowInst) {
		Collection dests = getExceptionDests(u);
		int escapeMethodCount = 0;
		for (Iterator destIt = dests.iterator(); destIt.hasNext(); ) {
		    ExceptionDest dest = (ExceptionDest) destIt.next();
		    if (dest.trap() == null) {
			escapeMethodCount++;
		    }
		}
		if (escapeMethodCount > 0) {
		    tailList.add(u);
		}
	    }
	}
	tails = Collections.unmodifiableList(tailList);
	heads = Collections.unmodifiableList(headList);
    }


    /**
     * Returns a collection of 
     * {@link ExceptionalUnitGraph.ExceptionDest ExceptionDest}
     * objects which represent how exceptions thrown by a specified
     * unit will be handled.
     *
     * @param u The unit for which to provide exception information.
     *
     * @return a collection of <tt>ExceptionDest</tt> objects describing
     *	       the traps, if any, which catch the exceptions
     *	       which may be thrown by <CODE>u</CODE>.
     */
    public Collection getExceptionDests(Unit u) {
	Collection result = null;
	if (unitToExceptionDests == null) {
	    // There are no traps in the method; all exceptions
	    // that the unit throws will escape the method.
	    result = new LinkedList();
	    result.add(new ExceptionDest(null, throwAnalysis.mightThrow(u)));
	} else {
	    result = (Collection) unitToExceptionDests.get(u);
	    if(result == null) 
		throw new RuntimeException("Invalid unit " + u);
	}
	return result;
    }

    
    /**
     * <p>Data structure to represent the fact that
     * a given {@link Trap} will catch some subset of the exceptions
     * which may be thrown by a given {@link Unit}.</p>
     *
     * <p>Note that these ``destinations'' are different from the
     * edges in the CFG proper which are returned by 
     * <tt>getSuccsOf()</tt> and <tt>getPredsOf()</tt>. An edge from
     * <CODE>a</CODE> to <CODE>b</CODE>) in the CFG represents the
     * fact that after unit <CODE>a</CODE> executes (perhaps only
     * partially, if it throws an exception after possibly producing a
     * side effect), execution may proceed to unit <CODE>b</CODE>.  An
     * ExceptionDest from <CODE>a</CODE> to <CODE>b</CODE>, on the
     * other hand, says that when <CODE>a</CODE> fails to complete at
     * all, execution may proceed to unit <CODE>b</CODE> instead.</p>
     */
    public static class ExceptionDest {
	private Trap trap;
	private ThrowableSet throwables;

	protected ExceptionDest(Trap trap, ThrowableSet throwables) {
	    this.trap = trap;
	    this.throwables = throwables;
	}
	
	/**
	 * Returns the trap corresponding to this destination.
	 *
	 * @return either a {@link Trap} representing the handler that
	 * catches the exceptions, if there is such a handler within
	 * the method, or <CODE>null</CODE> if there is no such
	 * handler and the exceptions cause the method to terminate
	 * abruptly.
	 */
	public Trap trap() {
	    return trap;
	}

	/**
	 * Returns the exceptions thrown to this destination.
	 *
	 * @return a {@link ThrowableSet} representing
	 * the exceptions which may be caught by this {@link ExceptionalUnitGraph.ExceptionDest ExceptionDest}'s 
	 * trap.
	 */
	public ThrowableSet throwables() {
	    return throwables;
	}

	/**
	 * Returns a string representation of this destination.
	 *
	 * @return a {@link String} representing this destination.
	 */
	public String toString() {
	    StringBuffer buf = new StringBuffer();
	    buf.append(throwables.toString());
	    buf.append(" -> ");
	    if (trap == null) {
		buf.append("(escapes)");
	    } else {
		buf.append(trap.toString());
	    }
	    return buf.toString();
	}
    }


    public List getUnexceptionalPredsOf(Unit u) {
	if (!unitToUnexceptionalPreds.containsKey(u))
	    throw new RuntimeException("Invalid unit " + u);

	return (List) unitToUnexceptionalPreds.get(u);
    }


    public List getUnexceptionalSuccsOf(Unit u) {
	if (!unitToUnexceptionalSuccs.containsKey(u))
	    throw new RuntimeException("Invalid unit " + u);

	return (List) unitToUnexceptionalSuccs.get(u);
    }


    public List getExceptionalPredsOf(Unit u) {
	if (unitToExceptionalPreds == null ||
	    (!unitToExceptionalPreds.containsKey(u))) {
	    return Collections.EMPTY_LIST;
	} else {
	    return (List) unitToExceptionalPreds.get(u);
	}
    }


    public List getExceptionalSuccsOf(Unit u) {
	if (unitToExceptionalSuccs == null ||
	    (!unitToExceptionalSuccs.containsKey(u))) {
	    return Collections.EMPTY_LIST;
	} else {
	    return (List) unitToExceptionalSuccs.get(u);
	}
    }


    public String toString() {
        Iterator it = unitChain.iterator();
        StringBuffer buf = new StringBuffer();
        while(it.hasNext()) {
            Unit u = (Unit) it.next();
            
            buf.append("// preds: "+getPredsOf(u)+"\n");
            buf.append("// unexceptional preds: "+getUnexceptionalPredsOf(u)+"\n");
            buf.append("// exceptional preds: "+getExceptionalPredsOf(u)+"\n");
            buf.append(u.toString() + '\n');
	    buf.append("// exception destinations: "+getExceptionDests(u)+"\n");
            buf.append("// unexceptional succs: "+getUnexceptionalPredsOf(u)+"\n");
            buf.append("// exceptional succs: "+getExceptionalPredsOf(u)+"\n");
            buf.append("// succs "+getSuccsOf(u)+"\n");
        }
        
        return buf.toString();
    }
}
