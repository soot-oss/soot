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

import java.util.*;
import soot.*;
import java.io.*;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.exceptions.ThrowableSet;

/**
 *  <p>Represents a CFG where the nodes are {@link Block}s and the
 *  edges are derived from control flow.  Control flow associated with
 *  exceptions is taken into account: when a {@link Unit} may throw
 *  an exception that is caught by a {@link Trap} within the
 *  <code>Body</code>, the excepting <code>Unit</code> starts a new basic
 *  block (<code>Unit</code>s do not start a new block when all the exceptions
 *  they might throw would escape the method without being caught).
 *  </p>
 */

public class ExceptionalBlockGraph extends BlockGraph implements ExceptionalGraph
{
    // Maps for distinguishing exceptional and unexceptional control flow.
    // We follow two conventions to save space (and runtime, if no client ever 
    // asks for the exceptional information):  if the graph contains no
    // exceptional edges (e.g. there are no traps in the method) we leave
    // all these map references as NULL, while if an individual block has only 
    // unexceptional successors or predecessors, it is not added to the 
    // relevant map. When the access methods are asked about such blocks, 
    // they return empty lists for the exceptional predecessors and successors,
    // and the complete list of predecessors or successors for 
    // the unexceptional predecessors and successors.
    Map blockToExceptionalPreds;
    Map blockToExceptionalSuccs;
    Map blockToUnexceptionalPreds;
    Map blockToUnexceptionalSuccs;
    Map blockToExceptionDests;

    // When the graph has no traps (thus no exceptional CFG edges); we cache the
    // throwAnalysis for generating ExceptionDests on demand.  If there
    // are traps, throwAnalysis remains null.
    ThrowAnalysis throwAnalysis ;

    /**
     *   <p>Constructs an <code>ExceptionalBlockGraph</code> for the
     *   blocks found by partitioning the the units of the provided
     *   <code>Body</code> instance into basic blocks.</p>
     *
     *   <p>Note that this constructor builds an {@link
     *   ExceptionalUnitGraph} internally when splitting <code>body</code>'s
     *   {@link Unit}s into {@link Block}s.  Callers who already have
     *   an <code>ExceptionalUnitGraph</code> to hand can use the constructor
     *   taking an <code>ExceptionalUnitGraph</code> as a parameter, as a
     *   minor optimization.
     *
     *   @param body    The underlying body we want to make a graph for.
     */
    public ExceptionalBlockGraph(Body body)
    {
        this(new ExceptionalUnitGraph(body));
    }


    /**
     *   Constructs a graph for the blocks found by partitioning the
     *   the {@link Unit}s in an {@link ExceptionalUnitGraph}.  
     *
     *   @param unitGraph The <code>ExceptionalUnitGraph</code> whose
     *                    <code>Unit</code>s are to be split into blocks.
     */
    public ExceptionalBlockGraph(ExceptionalUnitGraph unitGraph)
    {
        super(unitGraph);

	soot.util.PhaseDumper.v().dumpGraph(this);
    }


    /**
     * {@inheritDoc}
     *
     * This implementation calls the inherited implementation to split
     * units into blocks, before adding the distinctions between
     * exceptional and unexceptional control flow.
     *
     * @param {@inheritDoc}
     *
     * @return {@inheritDoc}
     */

    protected Map buildBlocks(Set leaders, UnitGraph uncastUnitGraph) {
	ExceptionalUnitGraph unitGraph = (ExceptionalUnitGraph) uncastUnitGraph;
	Map unitToBlock = super.buildBlocks(leaders, unitGraph);

	if (unitGraph.getBody().getTraps().size() == 0) {
	    // All exceptions escape the method.  Cache the ThrowAnalysis
	    // to respond to getExceptionDests() on demand.
	    throwAnalysis = unitGraph.getThrowAnalysis();
	    if (throwAnalysis == null) {
		throw new IllegalStateException("ExceptionalUnitGraph lacked a cached ThrowAnalysis for a Body with no Traps.");
	    }

	} else {
	    int initialMapSize = (mBlocks.size() * 2) / 3;
	    blockToUnexceptionalPreds = new HashMap(initialMapSize);
	    blockToUnexceptionalSuccs = new HashMap(initialMapSize);
	    blockToExceptionalPreds = new HashMap(initialMapSize);
	    blockToExceptionalSuccs = new HashMap(initialMapSize);

	    for (Iterator blockIt = mBlocks.iterator(); blockIt.hasNext(); ) {
		Block block = (Block) blockIt.next();

		Unit blockHead = block.getHead();
		List exceptionalPredUnits = unitGraph.getExceptionalPredsOf(blockHead);
		if (exceptionalPredUnits.size() != 0) {
		    List exceptionalPreds = mappedValues(exceptionalPredUnits,
							 unitToBlock);
		    exceptionalPreds = Collections.unmodifiableList(exceptionalPreds);
		    blockToExceptionalPreds.put(block, exceptionalPreds);
		    List unexceptionalPredUnits = unitGraph.getUnexceptionalPredsOf(blockHead);
		    List unexceptionalPreds = null;
		    if (unexceptionalPredUnits.size() == 0) {
			unexceptionalPreds = Collections.EMPTY_LIST;
		    } else {
			unexceptionalPreds = mappedValues(unexceptionalPredUnits,
							  unitToBlock);
			unexceptionalPreds = Collections.unmodifiableList(unexceptionalPreds);
		    }
		    blockToUnexceptionalPreds.put(block, unexceptionalPreds);
		}

		Unit blockTail = block.getTail();
		List exceptionalSuccUnits = unitGraph.getExceptionalSuccsOf(blockTail);
		if (exceptionalSuccUnits.size() != 0) {
		    List exceptionalSuccs = mappedValues(exceptionalSuccUnits,
							 unitToBlock);
		    exceptionalSuccs = Collections.unmodifiableList(exceptionalSuccs);
		      blockToExceptionalSuccs.put(block, exceptionalSuccs);
		    List unexceptionalSuccUnits = unitGraph.getUnexceptionalSuccsOf(blockTail);
		    List unexceptionalSuccs = null;
		    if (unexceptionalSuccUnits.size() == 0) {
			unexceptionalSuccs = Collections.EMPTY_LIST;
		    } else {
			unexceptionalSuccs = mappedValues(unexceptionalSuccUnits,
							  unitToBlock);
			unexceptionalSuccs = Collections.unmodifiableList(unexceptionalSuccs);
		    }
		    blockToUnexceptionalSuccs.put(block,unexceptionalSuccs);
		}
	    }
	    blockToExceptionDests = buildExceptionDests(unitGraph, unitToBlock);
	}
	return unitToBlock;
    }


    /**
     * Utility method which, given a {@link List} of objects and a {@link Map}
     * where those objects appear as keys, returns a <code>List</code> of the
     * values to which the keys map, in the corresponding order.
     *
     * @param keys the keys to be looked up.
     *
     * @param keyToValue the map in which to look up the keys.
     *
     * @throws IllegalStateException if one of the elements in <code>keys</code>
     * does not appear in <code>keyToValue</code>
     */
    private List mappedValues(List keys, Map keyToValue) {
	List result = new ArrayList(keys.size());
	for (Iterator it = keys.iterator(); it.hasNext(); ) {
	    Object key = it.next();
	    Object value = keyToValue.get(key);
	    if (value == null) {
		throw new IllegalStateException("No value corresponding to key: " + key.toString());
	    }
	    result.add(value);
	}
	return result;
    }


    private Map buildExceptionDests(ExceptionalUnitGraph unitGraph, 
				    Map unitToBlock) {
	Map result = new HashMap(mBlocks.size() * 2 + 1, 0.7f);
	for (Iterator blockIt = mBlocks.iterator(); blockIt.hasNext(); ) {
	    Block block = (Block) blockIt.next();
	    result.put(block, collectDests(block, unitGraph, unitToBlock));
	}
	return result;
    }


    /**
     * Utility method which, given a {@link Block} and the 
     * {@link ExceptionalUnitGraph} from which it was constructed,
     * returns the {@link ExceptionDest}s representing the exceptions
     * which may be thrown by units in the block.
     *
     * @param block the {@link Block} whose exceptions are to be collected.
     *
     * @param unitGraph the {@link ExceptionalUnitGraph} from which this 
     *                  graph is constructed.
     *
     * @param unitToBlock a {@link Map} from the units which are block heads 
     *                    or tails to the blocks that they belong to.
     *
     * @return a {@link Collection} of {@link ExceptionDest}s representing 
     *           the exceptions that may be thrown by this block, together
     *           with their catchers.
     */
    private Collection collectDests(Block block, ExceptionalUnitGraph unitGraph,
				    Map unitToBlock) {
	Unit blockHead = block.getHead();
	Unit blockTail = block.getTail();
	ArrayList blocksDests = null;
	ThrowableSet escapingThrowables = ThrowableSet.Manager.v().EMPTY;
	Map trapToThrowables = null;	// Don't allocate unless we need it.
	int caughtCount = 0;

	for (Iterator unitIt = block.iterator(); unitIt.hasNext(); ) {
	    Unit unit = (Unit) unitIt.next();
	    Collection unitDests = unitGraph.getExceptionDests(unit);
	    if (unitDests.size() != 1 && unit != blockHead && unit != blockTail) {
		throw new IllegalStateException("Multiple ExceptionDests associated with a unit which does not begin or end its block.");
	    }
	    for (Iterator destIt = unitDests.iterator(); destIt.hasNext(); ) {
		ExceptionalUnitGraph.ExceptionDest unitDest 
		    = (ExceptionalUnitGraph.ExceptionDest) destIt.next();
		if (unitDest.getTrap() == null) {
		    try {
			escapingThrowables = escapingThrowables.add(unitDest.getThrowables());
		    } catch (ThrowableSet.AlreadyHasExclusionsException e) {
			if (escapingThrowables != ThrowableSet.Manager.v().EMPTY) {
			    // Return multiple escaping ExceptionDests,
			    // since ThrowableSet's limitations do not permit us
			    // to add all the escaping type descriptions together.
			    if (blocksDests == null) {
				blocksDests = new ArrayList(10);
			    }
			    blocksDests.add(new ExceptionDest(null, 
							      escapingThrowables,
							      null));
			}
			escapingThrowables = unitDest.getThrowables();
		    }
		} else {
		    if (unit != blockHead && unit != blockTail) {
			// Assertion failure.
			throw new IllegalStateException("Unit " 
							+ unit.toString() 
							+ " is not a block head or tail, yet it throws " 
							+ unitDest.getThrowables() 
							+ " to "
							+ unitDest.getTrap());
		    }
		    caughtCount++;
		    if (trapToThrowables == null) {
			trapToThrowables = new HashMap(unitDests.size() * 2);
		    }
		    Trap trap = unitDest.getTrap();
		    ThrowableSet throwables = (ThrowableSet) trapToThrowables.get(trap);
		    if (throwables == null) {
			throwables = unitDest.getThrowables();
		    } else {
			throwables = throwables.add(unitDest.getThrowables());
		    }
		    trapToThrowables.put(trap, throwables);
		}
	    }
	}
	
	if (blocksDests == null) {
	    blocksDests = new ArrayList(caughtCount + 1);
	} else {
	    blocksDests.ensureCapacity(blocksDests.size() + caughtCount);
	}

	if (escapingThrowables != ThrowableSet.Manager.v().EMPTY) {
	    ExceptionDest escapingDest = new ExceptionDest(null, escapingThrowables, 
							   null);
	    blocksDests.add(escapingDest);
	}
	if (trapToThrowables != null) {
	    for (Iterator entryIt = trapToThrowables.entrySet().iterator();
		 entryIt.hasNext(); ) {
		Map.Entry entry = (Map.Entry) entryIt.next();
		Trap trap = (Trap) entry.getKey();
		Block trapBlock = (Block) unitToBlock.get(trap.getHandlerUnit());
		if (trapBlock == null) {
		    throw new IllegalStateException("catching unit is not recorded as a block leader.");
		}
		ThrowableSet throwables = (ThrowableSet) entry.getValue();
		ExceptionDest blockDest = new ExceptionDest(trap, throwables, trapBlock);
		blocksDests.add(blockDest);
	    }
	}
	return blocksDests;
    }

								  
    public List getUnexceptionalPredsOf(Object b) {
	if ((blockToUnexceptionalPreds == null) 
	    || (! blockToUnexceptionalPreds.containsKey(b))) {
	    Block block = (Block) b;
	    return block.getPreds();
	} else {
	    return (List) blockToUnexceptionalPreds.get(b);
	}
    }


    public List getUnexceptionalSuccsOf(Object b) {
	if ((blockToUnexceptionalSuccs == null) 
	    || (! blockToUnexceptionalSuccs.containsKey(b))) {
	    Block block = (Block) b;
	    return block.getSuccs();
	} else {
	    return (List) blockToUnexceptionalSuccs.get(b);
	}
    }


    public List getExceptionalPredsOf(Object  b) {
	if (blockToExceptionalPreds == null ||
	    (!blockToExceptionalPreds.containsKey(b))) {
	    return Collections.EMPTY_LIST;
	} else {
	    return (List) blockToExceptionalPreds.get(b);
	}
    }


    public List getExceptionalSuccsOf(Object b) {
	if (blockToExceptionalSuccs == null ||
	    (!blockToExceptionalSuccs.containsKey(b))) {
	    return Collections.EMPTY_LIST;
	} else {
	    return (List) blockToExceptionalSuccs.get(b);
	}
    }


    public Collection getExceptionDests(Object b) {
	if (blockToExceptionDests == null) {
	    Block block = (Block) b;
	    Collection result = new ArrayList(1);
	    ThrowableSet throwables = ThrowableSet.Manager.v().EMPTY;
	    for (Iterator unitIt = block.iterator(); unitIt.hasNext(); ) {
		Unit unit = (Unit) unitIt.next();
		throwables = throwables.add(throwAnalysis.mightThrow(unit));
	    }
	    result.add(new ExceptionalBlockGraph.ExceptionDest(null, throwables,
							       null));
	    return result;
	} else {
	    return (Collection) blockToExceptionDests.get(b);
	}
    }


    public static class ExceptionDest implements ExceptionalGraph.ExceptionDest {
	private Trap trap;
	private ThrowableSet throwables;
	private Block handler;

	protected ExceptionDest(Trap trap, ThrowableSet throwables, Block handler) {
	    this.trap = trap;
	    this.throwables = throwables;
	    this.handler = handler;
	}
	
	public Trap getTrap() {
	    return trap;
	}

	public ThrowableSet getThrowables() {
	    return throwables;
	}

	public Object getHandlerNode() {
	    return handler;
	}

	public String toString() {
	    StringBuffer buf = new StringBuffer();
	    buf.append(throwables.toString());
	    buf.append(" -> ");
	    if (trap == null) {
		buf.append("(escapes)");
	    } else {
		buf.append(trap.toString());
		buf.append("handler: ");
		buf.append(getHandlerNode().toString());
	    }
	    return buf.toString();
	}
    }
}


