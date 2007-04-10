package dk.brics.soot.analyses;

import java.util.*;

import dk.brics.soot.flowsets.ValueArraySparseSet;

import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class SimpleVeryBusyExpressions implements VeryBusyExpressions
{
	private Map unitToExpressionsAfter;
	private Map unitToExpressionsBefore;

	public SimpleVeryBusyExpressions(DirectedGraph graph) {
		SimpleVeryBusyAnalysis analysis = new SimpleVeryBusyAnalysis(graph);

		unitToExpressionsAfter = new HashMap(graph.size() * 2 + 1, 0.7f);
		unitToExpressionsBefore = new HashMap(graph.size() * 2 + 1, 0.7f);

		Iterator unitIt = graph.iterator();

		while (unitIt.hasNext()) {
			Unit s = (Unit) unitIt.next();

			FlowSet set = (FlowSet) analysis.getFlowBefore(s);
			unitToExpressionsBefore.put(s,
					Collections.unmodifiableList(set.toList()));

			set = (FlowSet) analysis.getFlowAfter(s);
			unitToExpressionsAfter.put(s,
					Collections.unmodifiableList(set.toList()));
		}
	}

	public List getBusyExpressionsAfter(Unit s) {
		return (List) unitToExpressionsAfter.get(s);
	}

	public List getBusyExpressionsBefore(Unit s) {
		List foo = (List) unitToExpressionsBefore.get(s);
		return foo;
	}
}

/**
 * Performs a naiive version of a very-busy expressions analysis.
 * 
 * @author Árni Einarsson
 */
class SimpleVeryBusyAnalysis extends BackwardFlowAnalysis
{
	/**
	 * Just for clarity. 
	 */
	private FlowSet emptySet;
	
	public SimpleVeryBusyAnalysis(DirectedGraph g) {
		// First obligation
		super(g);
		
		// NOTE: Would have expected to get the same results using a
		// regular ArraySparseSet, perhaps containing duplicates of
		// equivalent expressions. But that was not the case, results
		// are incorrect if using ArraySparseSet. This is because
		// we use intersection instead to merge sets,
		// and the ArraySparseSet.intersection implementation uses the method
		// contains to determine whether both sets contain the same element.
		// The contains method only compares references for equality.
		// (i.e. only if both sets contain the same reference is it included
		// in the intersecting set)
		emptySet = new ValueArraySparseSet();

		// NOTE: It's possible to build up the kill and gen sets of each of
		// the nodes in the graph in advance. But in order to do so we
		// would have to build the universe set first. This requires an
		// extra pass through the unit graph, so we generate the kill and
		// gen sets lazily instead.
		
		// Second obligation
		doAnalysis();
	}
	
	/**
	 * This method performs the actual joining of successor nodes
	 * (i.e. doAnalysis calls this method for each successor,
	 * if there are more than one, of the current node). Since very
	 * busy expressions is a <u>must</u> analysis we join by intersecting.
	 * @param in1 a flow set flowing into the node
	 * @param in2 a flow set flowing into the node
	 * @param out the merged set
	 */
	@Override
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inSet1 = (FlowSet)in1,
				inSet2 = (FlowSet)in2,
				outSet = (FlowSet)out;
		inSet1.intersection(inSet2, outSet);
	}

	@Override
	protected void copy(Object source, Object dest) {
		FlowSet srcSet = (FlowSet)source,
				destSet = (FlowSet)dest;
		srcSet.copy(destSet);
	}
	
	/**
	 * Used to initialize the in and out sets for each node. In
	 * our case we want to build up the sets as we go, so we
	 * initialize with the empty set.
	 * </p><p>
	 * Note: If we had information about all the possible values
	 * the sets could contain, we could initialize with that and
	 * then remove values during the analysis.
	 * @return an empty set 
	 */
	@Override
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * Returns a flow set representing the initial set of the entry
	 * node. In our case the entry node is the last node and it
	 * should contain the empty set.
	 * @return an empty set
	 */
	@Override
	protected Object entryInitialFlow() {
		return emptySet.clone();
	}
	
	/**
	 * Adds to the out set the values that flow through the node
	 * d from the in set.
	 * </p><p>
	 * This method has two phases, a kill phase and a gen phase.
	 * The kill phase performs the following:<br />
	 * out = (in - expressions containing a reference to any local
	 * defined in the node) union out.<br />
	 * The gen phase performs the following:<br />
	 * out = out union binary operator expressions used in the
	 * node.
	 * @param in the in-set of the current node
	 * @param node the current node of the control flow graph
	 * @param out the out-set of the current node
	 */
	@Override
	protected void flowThrough(Object in, Object node, Object out) {
		FlowSet inSet = (FlowSet)in,
				outSet = (FlowSet)out;
		Unit u = (Unit)node;
		// out <- (in - expr containing locals defined in d) union out 
		kill(inSet, u, outSet);
		// out <- out union expr used in d
		gen(outSet, u);
	}
	
	/**
	 * Performs kills by generating a killSet and then performing<br/>
	 * outSet <- inSet - killSet<br/>
	 * The kill set is generated by iterating over the def-boxes
	 * of the unit. For each local defined in the unit we iterate
	 * over the binopExps in the inSet, and check whether they use
	 * that local. If so, it is added to the kill set.
	 * @param inSet the set flowing into the unit
	 * @param u the unit being flown through
	 * @param outSet the set flowing out of the unit
	 */
	private void kill(FlowSet inSet, Unit u, FlowSet outSet) {
		FlowSet kills = (FlowSet)emptySet.clone();
		Iterator defIt = u.getDefBoxes().iterator();
		while (defIt.hasNext()) {
			ValueBox defBox = (ValueBox)defIt.next();

			if (defBox.getValue() instanceof Local) {
				Iterator inIt = inSet.iterator();
				while (inIt.hasNext()) {
					BinopExpr e = (BinopExpr)inIt.next();
					Iterator eIt = e.getUseBoxes().iterator();
					while (eIt.hasNext()) {
						ValueBox useBox = (ValueBox)eIt.next();
						if (useBox.getValue() instanceof Local &&
								useBox.getValue().equivTo(defBox.getValue()))
							kills.add(e);
					}
				}
			}
		}
		inSet.difference(kills, outSet);
	}
	
	/**
	 * Performs gens by iterating over the units use-boxes.
	 * If the value of a use-box is a binopExp then we add
	 * it to the outSet.
	 * @param outSet the set flowing out of the unit
	 * @param u the unit being flown through
	 */
	private void gen(FlowSet outSet, Unit u) {
		Iterator useIt = u.getUseBoxes().iterator();
		while (useIt.hasNext()) {
			ValueBox useBox = (ValueBox)useIt.next();
			
			if (useBox.getValue() instanceof BinopExpr)
				outSet.add(useBox.getValue());
		}
	}
}