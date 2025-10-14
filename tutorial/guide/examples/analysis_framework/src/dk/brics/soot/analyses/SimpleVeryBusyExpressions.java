package dk.brics.soot.analyses;

import java.util.*;

import dk.brics.soot.flowsets.ValueArraySparseSet;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.jimple.internal.AbstractBinopExpr;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

public class SimpleVeryBusyExpressions implements VeryBusyExpressions
{
	private Map<Unit, List<AbstractBinopExpr>> unitToExpressionsAfter;
	private Map<Unit, List<AbstractBinopExpr>> unitToExpressionsBefore;

	public SimpleVeryBusyExpressions(DirectedGraph<Unit> graph) {
		SimpleVeryBusyAnalysis analysis = new SimpleVeryBusyAnalysis(graph);

		unitToExpressionsAfter = new HashMap<Unit, List<AbstractBinopExpr>>(graph.size() * 2 + 1, 0.7f);
		unitToExpressionsBefore = new HashMap<Unit, List<AbstractBinopExpr>>(graph.size() * 2 + 1, 0.7f);

		for (Unit s : graph) {

			FlowSet set = (FlowSet) analysis.getFlowBefore(s);
			unitToExpressionsBefore.put(s,
					Collections.unmodifiableList(set.toList()));

			set = (FlowSet) analysis.getFlowAfter(s);
			unitToExpressionsAfter.put(s,
					Collections.unmodifiableList(set.toList()));
		}
	}

	public List<AbstractBinopExpr> getBusyExpressionsAfter(Unit s) {
		return unitToExpressionsAfter.get(s);
	}

	public List<AbstractBinopExpr> getBusyExpressionsBefore(Unit s) {
		List<AbstractBinopExpr> foo = unitToExpressionsBefore.get(s);
		return foo;
	}
}

/**
 * Performs a naiive version of a very-busy expressions analysis.
 * 
 * @author Árni Einarsson
 */
class SimpleVeryBusyAnalysis extends BackwardFlowAnalysis<Unit, FlowSet>
{
	/**
	 * Just for clarity. 
	 */
	private FlowSet emptySet;
	
	public SimpleVeryBusyAnalysis(DirectedGraph<Unit> g) {
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
	protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
		in1.intersection(in2, out);
	}

	@Override
	protected void copy(FlowSet source, FlowSet dest) {
		source.copy(dest);
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
	protected FlowSet newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * Returns a flow set representing the initial set of the entry
	 * node. In our case the entry node is the last node and it
	 * should contain the empty set.
	 * @return an empty set
	 */
	@Override
	protected FlowSet entryInitialFlow() {
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
	protected void flowThrough(FlowSet in, Unit node, FlowSet out) {
		// out <- (in - expr containing locals defined in d) union out 
		kill(in, node, out);
		// out <- out union expr used in d
		gen(out, node);
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
		FlowSet kills = emptySet.clone();
		for (ValueBox defBox : u.getDefBoxes()) {

			if (defBox.getValue() instanceof Local) {
				Iterator<BinopExpr> inIt = inSet.iterator();
				while (inIt.hasNext()) {
					BinopExpr e = inIt.next();
					Iterator<ValueBox> eIt = e.getUseBoxes().iterator();
					while (eIt.hasNext()) {
						ValueBox useBox = eIt.next();
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
		for (ValueBox useBox : u.getUseBoxes()) {

			if (useBox.getValue() instanceof BinopExpr)
				outSet.add(useBox.getValue());
		}
	}
}