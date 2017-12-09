/**
 * This class is a piece of test infrastructure. It compares various
 * varieties of control flow graphs.
 */

package soot.toolkits.graph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BriefUnitPrinter;
import soot.CompilationDeathException;
import soot.G;
import soot.LabeledUnitPrinter;
import soot.Trap;
import soot.Unit;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph.ExceptionDest;
import soot.util.Chain;

public class GraphComparer {

    DirectedGraph g1;
    DirectedGraph g2;

    /**
     * Utility interface for keeping track of graph nodes which
     * are considered to represent equivalent nodes in
     * the two graphs being compared.  
     */
    interface EquivalenceRegistry {
	/**
	 * @param node a node in one graph.
	 * @return the equivalent node from the other graph.
	 */
	Object getEquiv(Object node);
    }

    EquivalenceRegistry equivalences = null;

    /**
     * {@link EquivalenceRegistry} for comparing two {@link UnitGraph}s
     * Since the same {@link Unit}s are stored as nodes in both graphs,
     * equivalence is the same as object equality.
     */
    class EquivalentUnitRegistry implements EquivalenceRegistry {
	/**
	 * @param node a graph node that represents a {@link Unit}.
	 * @return <tt>node</tt>.
	 */
	public Object getEquiv(Object node) {
	    return node;
	}
    }

    
    /**
     * {@link EquivalenceRegistry} for comparing two {@link BlockGraph}s.
     * Two blocks are considered equivalent if they contain exactly the same
     * list of {@link Unit}s, in the same order. 
     */
    static class EquivalentBlockRegistry implements EquivalenceRegistry {
	private Map equivalenceMap = new HashMap();

	/**
	 * Create an {@link EquivalentBlockRegistry} which records the
	 * equivalent blocks in two graphs whose nodes are blocks.  To
	 * allow the use of graphs that are loaded from alternate
	 * class paths, the parameters are not required to be instances of
	 * {@link BlockGraph}.  They just have to be {@link
	 * DirectedGraph}s whose nodes are instances of some class
	 * that has an <tt>iterator()</tt> method that iterates over
	 * the {@link Unit}s in that block.
	 *
	 * @param g1 The first graph to register.
	 * @param g2 The second graph to register.
	 * @throws IllegalArgumentException if a given {@link Unit} appears
	 * in more than one block of either of the graphs.
	 */
	 
	EquivalentBlockRegistry(DirectedGraph g1, DirectedGraph g2) {
	    Map g1UnitToBlock = blockGraphToUnitMap(g1); // We don't need this
							 // map, but we want
							 // to confirm that no
							 // Unit appears in
							 // multiple Blocks.
	    Map g2UnitToBlock = blockGraphToUnitMap(g2);
	    for (Iterator g1it = g1.iterator(); g1it.hasNext(); ) {
		Object g1Block = g1it.next();
		List g1Units = getUnits(g1Block);
		Object g2Block = g2UnitToBlock.get(g1Units.get(0));
		List g2Units = getUnits(g2Block);
		if (g1Units.equals(g2Units)) {
		    equivalenceMap.put(g1Block, g2Block);
		    equivalenceMap.put(g2Block, g1Block);
		}
	    }
	}


	/**
	 * @param node a graph node that represents a {@link Block}.
	 * @return the node from the other graph being compared which
	 *         represents the same block, or <tt>null</tt> if there
	 *         is no such node.
	 */
	public Object getEquiv(Object node) {
	    return equivalenceMap.get(node);
	}


	/**
	 * Return a map from a {@link Unit} in the body represented by
	 * a {@link BlockGraph} to the graph node representing the
	 * block containing that {@link Unit}.
	 *
	 * @param g a graph whose nodes represent lists of {@link Unit}s. 
	 *          The nodes must have an <tt>iterator()</tt> method which
	 *          will iterate over the {@link Unit}s represented by the
	 *          node.
	 * @return a {@link Map} from {@link Unit}s to {@link Object}s 
	 *          that are the graph nodes containing those {@link Unit}s.
	 * @throws IllegalArgumentException should any node of <tt>g</tt>
	 *         lack an <tt>iterator()</tt> method or should
	 *         any {@link Unit} appear in
	 *         more than one node of the graph.
	 */
	private static Map blockGraphToUnitMap(DirectedGraph g) 
	throws IllegalArgumentException {
	    Map result = new HashMap();
	    for (Iterator blockIt = g.iterator(); blockIt.hasNext(); ) {
		Object block = blockIt.next();
		List units = getUnits(block);
		for (Iterator unitIt = units.iterator(); unitIt.hasNext(); ) {
		    Unit unit = (Unit) unitIt.next();
		    if (result.containsKey(unit)) {
			throw new IllegalArgumentException("blockGraphToUnitMap(): adding " +
							   unit.toString() +
							   " twice");
		    }
		    result.put(unit, block);
		}
	    }
	    return result;
	}


	/**
	 * Return the {@link List} of {@link Unit}s represented by an
	 * object which has an <tt>iterator()</tt> method which 
	 * iterates over {@link Unit}s. 
	 * 
	 * @param block the object which contains a list of {@link Unit}s.
	 * @return the list of {@link Unit}s.
	 */
	private static List getUnits(Object block) {
	    Class blockClass = block.getClass();
	    Class[] emptyParams = new Class[0];
	    List result = new ArrayList();
	    try {
		Method iterMethod = blockClass.getMethod("iterator", emptyParams);
		for (Iterator it = (Iterator) iterMethod.invoke(block, new Object[0]); 
		     it.hasNext(); ) {
		    Unit unit = (Unit) it.next();
		    result.add(unit);
		}
	    } catch (NoSuchMethodException e) {
		throw new IllegalArgumentException("GraphComparer.getUnits(): node lacks iterator() method.");
	    } catch (IllegalAccessException e) {
		throw new IllegalArgumentException("GraphComparer.getUnits(): inaccessible iterator() method.");
	    } catch (java.lang.reflect.InvocationTargetException e) {
		throw new IllegalArgumentException("GraphComparer.getUnits(): failed iterator() invocation.");
	    }
	    return result;
	}
    }



    /**
     * Utility interface for checking whether two graphs of particular types
     * differ only in the ways we would expect from two graphs
     * of those types that represent the same {@link Body}.
     */
    interface TypedGraphComparer {
	/**
	 * @return true if the two graphs represented by this comparer
	 * differ only in expected ways.
	 */
	boolean onlyExpectedDiffs();
    }

    TypedGraphComparer subcomparer = null;

    /**
     * Return a class that implements <tt>TypedGraphComparer</tt> for
     * the two graphs being compared by this <tt>GraphComparer</tt>,
     * or <tt>null</tt>, if there is no comparer available to compare
     * their types.
     */
    TypedGraphComparer TypedGraphComparerFactory() {
	class TypeAssignments {
	    // Note that "Alt" graphs are loaded from an alternate
	    // class path, so we need ugly, fragile, special-purpose
	    // hacks to recognize them.

	    ExceptionalUnitGraph exceptionalUnitGraph = null;
	    boolean omitExceptingUnitEdges;

	    ClassicCompleteUnitGraph classicCompleteUnitGraph = null;
	    TrapUnitGraph trapUnitGraph = null;
	    BriefBlockGraph briefBlockGraph = null;
	    ClassicCompleteBlockGraph classicCompleteBlockGraph = null;
	    DirectedGraph altCompleteBlockGraph = null;
	    DirectedGraph altBriefBlockGraph = null;
	    ArrayRefBlockGraph arrayRefBlockGraph = null;
	    DirectedGraph altArrayRefBlockGraph = null;
	    ZonedBlockGraph zonedBlockGraph = null;
	    DirectedGraph altZonedBlockGraph = null;

	    TypeAssignments(DirectedGraph g1, DirectedGraph g2) {
		this.add(g1);
		this.add(g2);
	    }

	    // The type-specific add() routine provides a means to
	    // specify the value of omitExceptingUnitEdges
	    // for ExceptionalUnitGraph.  We are counting on the
	    // callers to keep straight whether the graph was created
	    // with a non-default value for
	    // omitExceptingUnitEdges, since it doesn't seem
	    // worth saving the value of the boolean used with each
	    // ExceptionalUnitGraph constructed.
	    void add(ExceptionalUnitGraph g, boolean omitExceptingUnitEdges) {
		this.exceptionalUnitGraph = g;
		this.omitExceptingUnitEdges
		    = omitExceptingUnitEdges;
	    }

	    void add(DirectedGraph g) {
		if (g instanceof CompleteUnitGraph) {
		    this.add((ExceptionalUnitGraph) g, false);
		} else if (g instanceof ExceptionalUnitGraph) {
		    this.add((ExceptionalUnitGraph) g,
			     Options.v().omit_excepting_unit_edges());
		} else if (g instanceof ClassicCompleteUnitGraph) {
		    classicCompleteUnitGraph = (ClassicCompleteUnitGraph) g;
		} else if (g.getClass().getName().endsWith(".CompleteUnitGraph")) {
		} else if (g instanceof TrapUnitGraph) {
		    trapUnitGraph = (TrapUnitGraph) g;
		} else if (g.getClass().getName().endsWith(".TrapUnitGraph")) {
		} else if (g instanceof BriefUnitGraph) {
		} else if (g.getClass().getName().endsWith(".BriefUnitGraph")) {
		} else if (g instanceof ExceptionalBlockGraph) {
		} else if (g instanceof ClassicCompleteBlockGraph) {
		    classicCompleteBlockGraph = (ClassicCompleteBlockGraph) g;
		} else if (g.getClass().getName().endsWith(".CompleteBlockGraph")) {
		    altCompleteBlockGraph = g;
		} else if (g instanceof BriefBlockGraph) {
		    briefBlockGraph = (BriefBlockGraph) g;
		} else if (g.getClass().getName().endsWith(".BriefBlockGraph")) {
		    altBriefBlockGraph = g;
		} else if (g instanceof ArrayRefBlockGraph) {
		    arrayRefBlockGraph = (ArrayRefBlockGraph) g;
		} else if (g.getClass().getName().endsWith(".ArrayRefBlockGraph")) {
		    altArrayRefBlockGraph = g;
		} else if (g instanceof ZonedBlockGraph) {
		    zonedBlockGraph = (ZonedBlockGraph) g;
		} else if (g.getClass().getName().endsWith(".ZonedBlockGraph")) {
		    altZonedBlockGraph = g;
		} else {
		    throw new IllegalStateException("GraphComparer.add(): don't know how to add(" 
						    + g.getClass().getName()
						    + ')');
		}
	    }
	}
	TypeAssignments subtypes = new TypeAssignments(g1, g2);

	if (subtypes.exceptionalUnitGraph != null && 
	    subtypes.classicCompleteUnitGraph != null) {
	    return new ExceptionalToClassicCompleteUnitGraphComparer(
		 subtypes.exceptionalUnitGraph, 
		 subtypes.classicCompleteUnitGraph,
		 subtypes.omitExceptingUnitEdges);
	}

	if (subtypes.exceptionalUnitGraph != null && 
	    subtypes.trapUnitGraph != null) {
	    return new ExceptionalToTrapUnitGraphComparer(subtypes.exceptionalUnitGraph, 
							  subtypes.trapUnitGraph,
							  subtypes.omitExceptingUnitEdges);
	}

	if (subtypes.classicCompleteBlockGraph != null && 
	    subtypes.altCompleteBlockGraph != null) {
	    return new BlockToAltBlockGraphComparer(subtypes.classicCompleteBlockGraph, 
						    subtypes.altCompleteBlockGraph);
	}

	if (subtypes.briefBlockGraph != null && 
	    subtypes.altBriefBlockGraph != null) {
	    return new BlockToAltBlockGraphComparer(subtypes.briefBlockGraph, 
						    subtypes.altBriefBlockGraph);
	}

	if (subtypes.arrayRefBlockGraph != null && 
	    subtypes.altArrayRefBlockGraph != null) {
	    return new BlockToAltBlockGraphComparer(subtypes.arrayRefBlockGraph, 
						    subtypes.altArrayRefBlockGraph);
	}

	if (subtypes.zonedBlockGraph != null && 
	    subtypes.altZonedBlockGraph != null) {
	    return new BlockToAltBlockGraphComparer(subtypes.zonedBlockGraph, 
						    subtypes.altZonedBlockGraph);
	}
	return null;
    }


    public GraphComparer(DirectedGraph g1, DirectedGraph g2) {
	// Since we may be comparing graphs of classes loaded
	// from alternate class paths, we'll use conventions in
	// the class names to recognize BlockGraphs.
	this.g1 = g1;
	this.g2 = g2;
	String g1ClassName = g1.getClass().getName();
	String g2ClassName = g2.getClass().getName();
	if (g1ClassName.endsWith("BlockGraph") &&
	    g2ClassName.endsWith("BlockGraph")) {
	    equivalences = new EquivalentBlockRegistry(g1, g2);
	} else {
	    equivalences = new EquivalentUnitRegistry();
	}
	subcomparer = TypedGraphComparerFactory();
    }


    /**
     * Checks that the two graphs in the GraphComparer differ only in 
     * ways that the GraphComparer expects that they should differ,
     * given the types of graphs they are, assuming that the two
     * graphs represent the same body.
     */
    public boolean onlyExpectedDiffs() {
	if (subcomparer == null) {
	    return equal();
	} else {
	    return subcomparer.onlyExpectedDiffs();
	}
    }
    

    /**
     * Checks if a graph's edge set is consistent.
     *
     * @param g the {@link DirectedGraph} whose edge set is to be 
     * checked.
     *
     * @return <tt>true</tt> if <tt>g</tt>'s edge set is consistent
     * (meaning that <tt>x</tt> is recorded as a predecessor of <tt>y</tt> 
     * if and only if <tt>y</tt> is recorded as a successor of <tt>x</tt>).
     */
    public static boolean consistentGraph(DirectedGraph g) {
	for (Iterator nodeIt = g.iterator(); nodeIt.hasNext(); ) {
	    Object node = nodeIt.next();
	    for (Iterator predIt = g.getPredsOf(node).iterator(); 
		 predIt.hasNext(); ) {
		Object pred = predIt.next();
		if (! g.getSuccsOf(pred).contains(node)) {
		    return false;
		}
	    }
	    for (Iterator succIt = g.getSuccsOf(node).iterator(); 
		 succIt.hasNext(); ) {
		Object succ = succIt.next();
		if (! g.getPredsOf(succ).contains(node)) {
		    return false;
		}
	    }
	}
	return true;
    }


    /**
     * Compares this {@link GraphComparer}'s two {@link DirectedGraph}s.
     *
     * @return <tt>true</tt> if the two graphs have
     * the same nodes, the same lists of heads and tails, and the
     * same sets of edges, <tt>false</tt> otherwise.
     */
    public boolean equal() {
	if ((! consistentGraph(g1)) || (! consistentGraph(g2))) {
	    throw new CompilationDeathException(
		CompilationDeathException.COMPILATION_ABORTED,
		"Cannot compare inconsistent graphs");
	}
	if (! equivLists(g1.getHeads(), g2.getHeads())) {
	    return false;
	}
	if (! equivLists(g1.getTails(), g2.getTails())) {
	    return false;
	}
	return equivPredsAndSuccs();
    }


    /**
     * <p>Compares the predecessors and successors of corresponding
     * nodes of this {@link GraphComparer}'s two {@link
     * DirectedGraph}s.  This is factored out for sharing by {@link
     * #equal()} and {@link BlockToAltBlockGraphComparer#onlyExpectedDiffs}.
     *
     * @return <tt>true</tt> if the predecessors and successors of each
     * node in one graph are equivalent to the predecessors and successors
     * of the equivalent node in the other graph.
     */
    protected boolean equivPredsAndSuccs() {
	if (g1.size() != g2.size()) {
	    return false;
	}
	// We know the two graphs have the same size, so we only need
	// to iterate through one's nodes to see if the two match.
	for (Iterator g1it = g1.iterator(); g1it.hasNext(); ) {
	    Object g1node = g1it.next();
	    try {
		if (! equivLists(g1.getSuccsOf(g1node), 
				 g2.getSuccsOf(equivalences.getEquiv(g1node)))) {
		    return false;
		}
		if (! equivLists(g1.getPredsOf(g1node), 
				 g2.getPredsOf(equivalences.getEquiv(g1node)))) {
		    return false;
		}
	    } catch (RuntimeException e) {
		if (e.getMessage() != null && 
		    e.getMessage().startsWith("Invalid unit ")) {
		    return false;
		} else {
		    throw e;
		}
	    }
	}
	return true;
    }


    /**
     * Report the differences between the two {@link DirectedGraph}s.
     *
     * @param graph1Label a string to be used to identify the first
     * graph (<tt>g1</tt> in the call to {@link
     * GraphComparer#GraphComparer()}) in the report of differences.
     *
     * @param graph2Label a string to be used to identify the second
     * graph (<tt>g2</tt> in the call to {@link
     * GraphComparer@GraphComparer()}) in the report of differences .
     *
     * @return a {@link String} summarizing the differences 
     * between the two graphs.
     */
    public String diff(String graph1Label, String graph2Label) {
	StringBuffer report = new StringBuffer();
	LabeledUnitPrinter printer1 = makeUnitPrinter(g1);
	LabeledUnitPrinter printer2 = makeUnitPrinter(g2);
	diffList(report, printer1, printer2, "HEADS", 
		 g1.getHeads(), g2.getHeads());
	diffList(report, printer1, printer2, "TAILS", 
		 g1.getTails(), g2.getTails());
	       
	for (Iterator g1it = g1.iterator(); g1it.hasNext(); ) {
	    Object g1node = g1it.next();
	    Object g2node = equivalences.getEquiv(g1node);
	    String g1string = nodeToString(g1node, printer1);
	    List g1succs = g1.getSuccsOf(g1node);
	    List g1preds = g1.getPredsOf(g1node);
	    List g2succs = null;
	    List g2preds = null;

	    try {
		if (g2node != null) {
		    g2preds = g2.getPredsOf(g2node);
		}
	    } catch (RuntimeException e) {
		if (e.getMessage() != null && 
		    e.getMessage().startsWith("Invalid unit ")) {
		    // No preds entry for g1node in g2
		} else {
		    throw e;
		}
	    }
	    diffList(report, printer1, printer2, g1string + " PREDS", 
		     g1preds, g2preds);

	    try {
		if (g2node != null) {
		    g2succs = g2.getSuccsOf(g2node);
		}
	    } catch (RuntimeException e) {
		if (e.getMessage() != null && 
		    e.getMessage().startsWith("Invalid unit ")) {
		    // No succs entry for g1node in g2
		} else {
		    throw e;
		}
	    }
	    diffList(report, printer1, printer2, g1string + " SUCCS", 
		     g1succs, g2succs);

	    
	}

	for (Iterator g2it = g2.iterator(); g2it.hasNext(); ) {
	    // In this loop we Only need to report the cases where
	    // there is no g1 entry at all, cases where there is an
	    // entry in both graphs but with differing lists of preds
	    // or succs will have already been reported by the loop over
	    // g1's nodes.
	    Object g2node = g2it.next();
	    Object g1node = equivalences.getEquiv(g2node);
	    String g2string = nodeToString(g2node, printer2);
	    List g2succs = g2.getSuccsOf(g2node);
	    List g2preds = g2.getPredsOf(g2node);

	    try {
		if (g1node != null) {
		    g1.getPredsOf(g1node);
		}
	    } catch (RuntimeException e) {
		if (e.getMessage() != null && 
		    e.getMessage().startsWith("Invalid unit ")) {
		    diffList(report, printer1, printer2, 
			     g2string  + " PREDS",
			     null, g2preds);
		} else {
		    throw e;
		}
	    }

	    try {
		if (g1node != null) {
		    g1.getSuccsOf(g1node);
		}
	    } catch (RuntimeException e) {
		if (e.getMessage() != null && 
		    e.getMessage().startsWith("Invalid unit ")) {
		    diffList(report, printer1, printer2, 
			     g2string + " SUCCS" ,
			     null, g2succs);
		} else {
		    throw e;
		}
	    }
	}

	if (report.length() > 0) {
	    String leader1 = "*** ";
	    String leader2 = "\n--- ";
	    String leader3 = "\n";
	    StringBuffer result = new StringBuffer(leader1.length() + 
						   graph1Label.length() + 
						   leader2.length() +
						   graph2Label.length() +
						   leader3.length() +
						   report.length());
	    result.append(leader1)
		.append(graph1Label)
		.append(leader2)
		.append(graph2Label)
		.append(leader3)
		.append(report);
	    return result.toString();
	} else {
	    return "";
	}
    }


    /**
     * Class for comparing a {@link TrapUnitGraph} to an {@link
     * ExceptionalUnitGraph}. 
     */
    class ExceptionalToTrapUnitGraphComparer implements TypedGraphComparer {
	protected ExceptionalUnitGraph exceptional;
	protected TrapUnitGraph cOrT;	// ClassicCompleteUnitGraph Or TrapUnitGraph.
	protected boolean omitExceptingUnitEdges;

	/**
	 * Indicates whether {@link #predOfTrappedThrower()} should
	 * return false for edges from head to tail when the only one
	 * of head's successors which throws an exception caught by
	 * tail is the first unit trapped by tail (this distinguishes
	 * ClassicCompleteUnitGraph from TrapUnitGraph).
	 */
	protected boolean predOfTrappedThrowerScreensFirstTrappedUnit; 

	ExceptionalToTrapUnitGraphComparer(ExceptionalUnitGraph exceptional, 
					   TrapUnitGraph cOrT,
					   boolean omitExceptingUnitEdges) {
	    this.exceptional = exceptional;
	    this.cOrT = cOrT;
	    this.omitExceptingUnitEdges
		= omitExceptingUnitEdges;
	    this.predOfTrappedThrowerScreensFirstTrappedUnit = false;
	}

	public boolean onlyExpectedDiffs() {
            if (exceptional.size() != cOrT.size()) {
		if (Options.v().verbose()) 
		    G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): sizes differ" + exceptional.size() + " " + cOrT.size());
	        return false;
	    }

	    if (! (exceptional.getHeads().containsAll(cOrT.getHeads())
		   && cOrT.getHeads().containsAll(exceptional.getHeads())) ) {
		if (Options.v().verbose()) 
		    G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): heads differ");
		return false;
	    }

	    if (! (exceptional.getTails().containsAll(cOrT.getTails()))) {
		return false;
	    }

	    for (Iterator<Unit> tailIt = exceptional.getTails().iterator(); 
		 tailIt.hasNext(); ) {
		Unit tail = tailIt.next();
		if ((! cOrT.getTails().contains(tail)) &&
		    (! trappedReturnOrThrow(tail))) {
		    if (Options.v().verbose()) 
			G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + tail.toString() + " is not a tail in cOrT, but not a trapped Return or Throw either");
		    return false;
		}
	    }

	    // Since we've already confirmed that the two graphs have
	    // the same number of nodes, we only need to iterate through
	    // the nodes of one of them --- if they don't have exactly the same
	    // set of nodes, this single iteration will reveal some
	    // node in cOrT that is not in exceptional.
	    for (Iterator nodeIt = cOrT.iterator(); nodeIt.hasNext(); ) {
		Unit node = (Unit) nodeIt.next();
		try {
		    List cOrTSuccs = cOrT.getSuccsOf(node);
		    List exceptionalSuccs = exceptional.getSuccsOf(node);
		    for (Iterator it = cOrTSuccs.iterator(); it.hasNext(); ) {
			Unit cOrTSucc = (Unit) it.next();
			if ((! exceptionalSuccs.contains(cOrTSucc)) &&
			    (! cannotReallyThrowTo(node, cOrTSucc))) {
			    if (Options.v().verbose()) 
				G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + cOrTSucc.toString() + " is not exceptional successor of " + node.toString() + " even though " + node.toString() + " can throw to it");
			    return false;
			}
		    }
		    for (Iterator it = exceptionalSuccs.iterator(); it.hasNext(); ) {
			Unit exceptionalSucc = (Unit) it.next();
			if ((! cOrTSuccs.contains(exceptionalSucc)) &&
			    (! predOfTrappedThrower(node, exceptionalSucc))) {
			    if (Options.v().verbose()) 
				G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + exceptionalSucc.toString() + " is not TrapUnitGraph successor of " + node.toString() + " even though " + node.toString() + " is not a predOfTrappedThrower or predOfTrapBegin");
			    return false;
			}
		    }

		    List cOrTPreds = cOrT.getPredsOf(node);
		    List exceptionalPreds = exceptional.getPredsOf(node);
		    for (Iterator it = cOrTPreds.iterator(); it.hasNext(); ) {
			Unit cOrTPred = (Unit) it.next();
			if ((! exceptionalPreds.contains(cOrTPred)) &&
			    (! cannotReallyThrowTo(cOrTPred, node))) {
			    if (Options.v().verbose())
				G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + cOrTPred.toString() + " is not ExceptionalUnitGraph predecessor of " + node.toString() + " even though " + cOrTPred.toString() + " can throw to " + node.toString());
			    return false;
			}
		    }
		    for (Iterator it = exceptionalPreds.iterator(); it.hasNext(); ) {
			Unit exceptionalPred = (Unit) it.next();
			if ((! cOrTPreds.contains(exceptionalPred)) &&
			    (! predOfTrappedThrower(exceptionalPred, node))) {
			    if (Options.v().verbose()) 
				G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + exceptionalPred.toString() + " is not COrTUnitGraph predecessor of " + node.toString() + " even though " + exceptionalPred.toString() + " is not a predOfTrappedThrower");
			    return false;
			}
		    }
			
		} catch (RuntimeException e) {
		    e.printStackTrace(System.err);
		    if (e.getMessage() != null && 
			e.getMessage().startsWith("Invalid unit ")) {
			if (Options.v().verbose()) 
			    G.v().out.println("ExceptionalToTrapUnitComparer.onlyExpectedDiffs(): " + node.toString() + " is not in ExceptionalUnitGraph at all");
			// node is not in exceptional graph.
			return false;
		    } else {
			throw e;
		    }
		}
	    }
	    return true;
	}


	/**
	 * <p>A utility method for confirming that a node which is
	 * considered a tail by a {@link ExceptionalUnitGraph} but not by the
	 * corresponding {@link TrapUnitGraph} or {@link
	 * ClassicCompleteUnitGraph} is a return instruction or throw
	 * instruction with a {@link Trap} handler as its successor in the
	 * {@link TrapUnitGraph} or {@link ClassicCompleteUnitGraph}.</p>
	 *
	 * @param node the node which is considered a tail by
	 * <tt>exceptional</tt> but not by <tt>cOrT</tt>.
	 *
	 * @return <tt>true</tt> if <tt>node</tt> is a return instruction
	 * which has a trap handler as its successor in <tt>cOrT</tt>.\
	 */
	protected boolean trappedReturnOrThrow(Unit node) {
	    if (! ((node instanceof soot.jimple.ReturnStmt) ||
		   (node instanceof soot.jimple.ReturnVoidStmt) ||
		   (node instanceof soot.baf.ReturnInst) ||
		   (node instanceof soot.baf.ReturnVoidInst) ||
		   (node instanceof soot.jimple.ThrowStmt) ||
		   (node instanceof soot.baf.ThrowInst))) {
		return false;
	    }
	    List succsUnaccountedFor = new ArrayList(cOrT.getSuccsOf(node));
	    if (succsUnaccountedFor.size() <= 0) {
		return false;
	    }
	    for (Iterator trapIt = cOrT.getBody().getTraps().iterator();
		 trapIt.hasNext(); ) {
		Trap trap = (Trap) trapIt.next();
		succsUnaccountedFor.remove(trap.getHandlerUnit());
	    }
	    return (succsUnaccountedFor.size() == 0);
	}


	/**
	 * A utility method for confirming that an edge which is in a
	 * {@link TrapUnitGraph} or {@link ClassicCompleteUnitGraph} but
	 * not the corresponding {@link ExceptionalUnitGraph} satisfies the
	 * criterion we would expect of such an edge: that it leads from a
	 * unit to a handler, that the unit might be expected to trap to
	 * the handler based solely on the range of Units trapped by the
	 * handler, but that there is no way for the particular exception
	 * that the handler catches to be thrown at this point.
	 *
	 * @param head the graph node that the edge leaves.
	 * @param tail the graph node that the edge leads to.
	 * @return true if <tt>head</tt> cannot really be associated with
	 * an exception that <tt>tail</tt> catches, false otherwise.
	 */
	protected boolean cannotReallyThrowTo(Unit head, Unit tail) {
	    List tailsTraps = returnHandlersTraps(tail);

	    // Check if head is in one of the traps' protected
	    // area, but either cannot throw an exception that the
	    // trap catches, or is not included in ExceptionalUnitGraph
	    // because it has no side-effects:
	    Collection headsDests = exceptional.getExceptionDests(head);
	    for (Iterator it = tailsTraps.iterator(); it.hasNext(); ) {
		Trap tailsTrap = (Trap) it.next();
		if (amongTrappedUnits(head, tailsTrap) 
		    && ((! destCollectionIncludes(headsDests, tailsTrap)) 
			|| ((! ExceptionalUnitGraph.mightHaveSideEffects(head))
			    && omitExceptingUnitEdges))) {
		    return true;
		}
		
	    }
	    return false;
	}


	/**
	 * A utility method for determining if a {@link Unit} is among
	 * those protected by a particular {@link Trap}..
	 *
	 * @param unit the {@link Unit} being asked about.
	 *
	 * @param trap the {@link Trap} being asked about.
	 *
	 * @return <tt>true</tt> if <tt>unit</tt> is protected by
	 * <tt>trap</tt>, false otherwise.
	 */
	protected boolean amongTrappedUnits(Unit unit, Trap trap) {
	    Chain units = exceptional.getBody().getUnits();
	    for (Iterator it = units.iterator(trap.getBeginUnit(),
					      units.getPredOf(trap.getEndUnit()));
		 it.hasNext(); ) {
		Unit u = (Unit) it.next();
		if (u == unit) {
		    return true;
		}
	    }
	    return false;
	}


	/**
	 * A utility method for confirming that an edge which is in a
	 * {@link CompleteUnitGraph} but not the corresponding {@link
	 * TrapUnitGraph} satisfies the criteria we would expect of such
	 * an edge: that the tail of the edge is a {@link Trap} handler,
	 * and that the head of the edge is not itself in the
	 * <tt>Trap</tt>'s protected area, but is the predecessor of a
	 * {@link Unit}, call it <tt>u</tt>, such that <tt>u</tt> is a
	 * <tt>Unit</tt> in the <tt>Trap</tt>'s protected area and
	 * <tt>u</tt> might throw an exception that the <tt>Trap</tt>
	 * catches.
	 *
	 * @param head the graph node that the edge leaves.
	 * @param tail the graph node that the edge leads to.
	 * @return a {@link List} of {@link Trap}s such that <tt>head</tt> is
	 * a predecessor of a {@link Unit} that might throw an exception
	 * caught by {@link Trap}.
	 */
	protected boolean predOfTrappedThrower(Unit head, Unit tail) {
	    // First, ensure that tail is a handler.
	    List tailsTraps = returnHandlersTraps(tail);
	    if (tailsTraps.size() == 0) {
		if (Options.v().verbose()) 
		    G.v().out.println("trapsReachedViaEdge(): " + tail.toString() + " is not a trap handler");
		return false;
	    }

	    // Build a list of Units, other than head itself, which, if
	    // they threw a caught exception, would cause
	    // CompleteUnitGraph to add an edge from head to the tail:
	    List possibleThrowers = new ArrayList(exceptional.getSuccsOf(head));
	    possibleThrowers.remove(tail); // This method wouldn't have been
	    possibleThrowers.remove(head); // called if tail was not in
	    // g.getSuccsOf(head).
	    
	    // We need to screen out Traps that catch exceptions from
	    // head itself, since they should be in both CompleteUnitGraph
	    // and TrapUnitGraph.
	    List headsCatchers = new ArrayList();
	    for (Iterator it = exceptional.getExceptionDests(head).iterator(); 
		 it.hasNext(); ) {
		ExceptionDest dest = (ExceptionDest) it.next();
		headsCatchers.add(dest.getTrap());
	    }

	    // Now ensure that one of the possibleThrowers might throw
	    // an exception caught by one of tail's Traps,
	    // screening out combinations where possibleThrower is the
	    // first trapped Unit, if screenFirstTrappedUnit is true.
	    for (Iterator throwerIt = possibleThrowers.iterator(); 
		 throwerIt.hasNext(); ) {
		Unit thrower = (Unit) throwerIt.next();
		Collection dests = exceptional.getExceptionDests(thrower);
		for (Iterator destIt = dests.iterator(); destIt.hasNext(); ) {
		    ExceptionDest dest = (ExceptionDest) destIt.next();
		    Trap trap = dest.getTrap();
		    if (tailsTraps.contains(trap)) {
			if (headsCatchers.contains(trap)) {
			    throw new RuntimeException("trapsReachedViaEdge(): somehow there is no TrapUnitGraph edge from " + head + " to " + tail + " even though the former throws an exception caught by the latter!");
			} else if ((! predOfTrappedThrowerScreensFirstTrappedUnit) ||
				   (thrower != trap.getBeginUnit())) {
			    return true;
			}
		    }
		}
	    }
	    return false;
	}


	/**
	 * Utility method that returns all the {@link Trap}s whose
	 * handlers begin with a particular {@link Unit}.
	 *
	 * @param handler the handler {@link Unit} whose {@link Trap}s are
	 * to be returned.
	 * @return a {@link List} of {@link Trap}s with <code>handler</code>
	 * as their handler's first {@link Unit}. The list may be empty.
	 */
	protected List returnHandlersTraps(Unit handler) {
	    Body body = exceptional.getBody();
	    List result = null;
	    for (Iterator it = body.getTraps().iterator(); it.hasNext(); ) {
		Trap trap = (Trap) it.next();
		if (trap.getHandlerUnit() == handler) {
		    if (result == null) {
			result = new ArrayList();
		    }
		    result.add(trap);
		}
	    }
	    if (result == null) {
		result = Collections.EMPTY_LIST;
	    }
	    return result;
	}
    }

    /**
     * Class for comparing an {@link ExceptionalUnitGraph} to a {@link
     * ClassicCompleteUnitGraph} . Since {@link
     * ClassicCompleteUnitGraph} is a subclass of {@link
     * TrapUnitGraph}, this class makes only minor adjustments to its
     * parent.
     */
    class ExceptionalToClassicCompleteUnitGraphComparer 
	extends ExceptionalToTrapUnitGraphComparer  {

	ExceptionalToClassicCompleteUnitGraphComparer(ExceptionalUnitGraph exceptional, 
						      ClassicCompleteUnitGraph trap,
						      boolean omitExceptingUnitEdges) {
	    super(exceptional, trap, omitExceptingUnitEdges);
	    this.predOfTrappedThrowerScreensFirstTrappedUnit = true;
	}

	protected boolean cannotReallyThrowTo(Unit head, Unit tail) {
	    if (Options.v().verbose()) 
		G.v().out.println("ExceptionalToClassicCompleteUnitGraphComparer.cannotReallyThrowTo() called.");
	    if (super.cannotReallyThrowTo(head, tail)) {
		return true;
	    } else {
		// A ClassicCompleteUnitGraph will consider the predecessors
		// of the first trapped Unit as predecessors of the Trap's
		// handler.
		List headsSuccs = exceptional.getSuccsOf(head);
		List tailsTraps = returnHandlersTraps(tail);
		for (Iterator it = tailsTraps.iterator(); it.hasNext(); ) {
		    Trap tailsTrap = (Trap) it.next();
		    Unit tailsFirstTrappedUnit = tailsTrap.getBeginUnit();
		    if (headsSuccs.contains(tailsFirstTrappedUnit)) {
			Collection succsDests = exceptional.getExceptionDests(tailsFirstTrappedUnit);
			if ((! destCollectionIncludes(succsDests, tailsTrap)) ||
			    (! CompleteUnitGraph.mightHaveSideEffects(tailsFirstTrappedUnit))) {
			    return true;
			}
		    }
		}
		return false;
	    }
	}
    }


    /**
     * Class for Comparing a {@link BriefBlockGraph}, {@link
     * ArrayRefBlockGraph}, or {@link ZonedBlockGraph} to an {@link
     * AltBriefBlockGraph}, {@link AltArrayRefBlockGraph}, or {@link
     * AltZonedBlockGraph}, respectively. The two should differ only
     * in that the <tt>Alt</tt> variants have empty tail lists.
     */
    class BlockToAltBlockGraphComparer implements TypedGraphComparer {
	DirectedGraph reg;	// The regular BlockGraph.
	DirectedGraph alt;	// The Alt BlockGraph.

	BlockToAltBlockGraphComparer(DirectedGraph reg, DirectedGraph alt) {
	    this.reg = reg;
	    this.alt = alt;
	}

	public boolean onlyExpectedDiffs() {
	    if (reg.size() != alt.size()) {
		return false;
	    }
	    if (! equivLists(reg.getHeads(), alt.getHeads())) {
		return false;
	    }
	    if (alt.getTails().size() != 0) {
		return false;
	    }	
	    return equivPredsAndSuccs();
	}
    }


    /**
     * A utility method for determining if a {@link Collection}
     * of {@link CompleteUnitGraph#ExceptionDest} contains
     * one which leads to a specified {@link Trap}.
     *
     * @param dests the {@link Collection} of {@link
     * CompleteUnitGraph#ExceptionDest}s to search.
     *
     * @param trap the {@link Trap} to search for.
     *
     * @return <tt>true</tt> if <tt>dests</tt> contains 
     * <tt>trap</tt> as a destination, false otherwise.
     */
    private static boolean destCollectionIncludes(Collection dests, Trap trap) {
	for (Iterator destIt = dests.iterator(); destIt.hasNext(); ) {
	    ExceptionDest dest = (ExceptionDest) destIt.next();
	    if (dest.getTrap() == trap) {
		return true;
	    }
	}
	return false;
    }
	    
    /**
     * Utility method to return the {@link Body} associated with a 
     * {@link DirectedGraph}, if there is one.
     *
     * @param g the graph for which to return a {@link Body}.
     *
     * @return the {@link Body} represented by <tt>g</tt>, if <tt>g</tt>
     * is a control flow graph, or <tt>null</tt> if <tt>g</tt> is not a
     * control flow graph.
     */
    private static Body getGraphsBody(DirectedGraph g) {
	Body result = null;
	if (g instanceof UnitGraph) {
	    result = ((UnitGraph) g).getBody();
	} else if (g instanceof BlockGraph) {
	    result = ((BlockGraph) g).getBody();
	}
	return result;
    }


    /**
     * Utility method that returns a {@link LabeledUnitPrinter} for printing
     * the {@link Unit}s in graph.
     *
     * @param g the graph for which to return a {@link LabeledUnitPrinter}.
     * @return A {@link LabeledUnitPrinter} for printing the {@link Unit}s in
     * <tt>g</tt> if <tt>g</tt> is a control flow graph. Returns 
     * <tt>null</tt> if <tt>g</tt> is not a control flow graph.
     */
    private static LabeledUnitPrinter makeUnitPrinter(DirectedGraph g) {
	Body b = getGraphsBody(g);
	if (b == null) {
	    return null;
	} else {
	    BriefUnitPrinter printer = new BriefUnitPrinter(b);
	    printer.noIndent();
	    return printer;
	}
    }


    /**
     * Utility method to return a {@link String} representation of a
     * graph node.
     *
     * @param node an {@link Object} representing a node in a
     *             {@link DirectedGraph}.
     *
     * @param printer either a {@link LabeledUnitPrinter} for printing the
     * {@link Unit}s in the graph represented by <tt>node</tt>'s
     * graph, if node is part of a control flow graph, or <tt>null</tt>, if
     * <tt>node</tt> is not part of a control flow graph.
     *
     * @return a {@link String} representation of <tt>node</tt>.
     */
    private static String nodeToString(Object node, 
				       LabeledUnitPrinter printer) {
	String result = null;
	if (printer == null) {
	    result = node.toString();
	} else if (node instanceof Unit) {
	    ((Unit) node).toString(printer);
	    result = printer.toString();
	} else if (node instanceof Block) {
	    StringBuffer buffer = new StringBuffer();
	    Iterator units = ((Block) node).iterator();
	    while (units.hasNext()) {
		Unit unit = (Unit) units.next();
		String targetLabel = (String) printer.labels().get(unit);
		if (targetLabel != null) {
		    buffer.append(targetLabel)
			.append(": ");
		}
		unit.toString(printer);
		buffer.append(printer.toString()).append("; ");
	  }
	  result = buffer.toString();
	}
	return result;
    }


    /**
     * A utility method for reporting the differences between two lists 
     * of graph nodes. The lists are usually the result of calling
     * <tt>getHeads()</tt>, <tt>getTails()</tt>, <tt>getSuccsOf()</tt>
     * or <tt>getPredsOf()</tt> on each of two graphs being compared.
     *
     * @param buffer a {@link StringBuffer} to which to append the 
     * description of differences.
     *
     * @param printer1 a {@link LabeledUnitPrinter} to be used to format
     * any {@link Unit}s found in <tt>list1</tt>.
     *
     * @param printer2 a {@link LabeledUnitPrinter} to be used to format
     * any {@link Unit}s found in <tt>list2</tt>.
     * 
     * @param label a string characterizing these lists.
     *
     * @param list1 the list from the first graph, or <tt>null</tt> if
     * this list is missing in the first graph.
     *
     * @param list2 the list from the second graph, or <tt>null</tt> if
     * this list is missing in the second graph.
     */
    private void diffList(StringBuffer buffer, 
			  LabeledUnitPrinter printer1, 
			  LabeledUnitPrinter printer2,
			  String label, List list1, List list2) {
	if (! equivLists(list1, list2)) {
	    buffer.append("*********\n");
	    if (list1 == null) {
		buffer.append("+ ");
		list1 = Collections.EMPTY_LIST;
	    } else if (list2 == null) {
		buffer.append("- ");
		list2 = Collections.EMPTY_LIST;
	    } else {
		buffer.append("  ");
	    }
	    buffer.append(label)
		.append(":\n");
	    for (Iterator it = list1.iterator(); it.hasNext(); ) {
		Object list1Node = it.next();
		Object list2Node = equivalences.getEquiv(list1Node);
		if (list2.contains(list2Node)) {
		    buffer.append("      ");
		} else {
		    buffer.append("-     ");
		}
		buffer.append(nodeToString(list1Node, printer1)).append("\n");
	    }
	    for (Iterator it = list2.iterator(); it.hasNext(); ) {
		Object list2Node = it.next();
		Object list1Node = equivalences.getEquiv(list2Node);
		if (! list1.contains(list1Node)) {
		    buffer.append("+     ")
			.append(nodeToString(list2Node,printer2))
			.append("\n");
		}
	    }
	    buffer.append("---------\n");
	}

    }


    /**
     * Utility method that determines if two lists of nodes are equivalent.
     * 
     * @param list1 The first list of nodes.
     * @param list2 The second list of nodes.
     * @return <tt>true</tt> if the equivalent of each node in <tt>list1</tt>
     * is found in <tt>list2</tt>, and vice versa.
     */
    private boolean equivLists(List list1, List list2) {
	if (list1 == null) {
	    return (list2 == null);
	} else if (list2 == null) {
	    return false;
	}

	if (list1.size() != list2.size()) {
	    return false;
	}

	for (Iterator i = list1.iterator(); i.hasNext(); ) {
	    if (! list2.contains(equivalences.getEquiv(i.next()))) {
		return false;
	    }
	}

	// Since getEquiv() should be symmetric, and we've confirmed that
	// the lists are the same size, the next loop shouldn't really
	// be necessary. But just in case something is fouled up, we 
	// include this loop as an assertion check.
	for (Iterator i = list2.iterator(); i.hasNext(); ) {
	    if (! list1.contains(equivalences.getEquiv(i.next()))) {
		throw new IllegalArgumentException("equivLists: " + 
						   list2.toString() +
						   " contains all the  equivalents of " +
						   list1.toString() + 
						   ", but the reverse is not true.");
	    }
	}
	return true;
    }
}
