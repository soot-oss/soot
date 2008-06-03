/*
 * Copyright (C) 2000 Janus
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

     LocalRefVarsAnalysisWrapper class

     Simple wrapper for BranchedRefVarsAnalysis, to be used by
     the statement printer.

     1) Compute lists of (ref, value) that have been analyzed: 
     * before a statement
     * after a statement fall through
     * after a statement branches

     Note: (ref, kTop) are discareded to improve readability.
     (This behavior can be turned off at compile time.)

     
     2) Compute lists of references that need to be checked by
        a null pointer check at a given statement.

        Compute lists of references that DO NOT need to be 
        checked by a null pointer check at a given statement.
     
	Notes: 
	a) that computation can be turned off at compile time
	b) lists all references that (do not) need to be checked
	   not just the ones that have been analyzed.
     
*/


package soot.jimple.toolkits.annotation.nullcheck;

import soot.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.util.*;

/**
 * @deprecated uses deprecated type {@link BranchedRefVarsAnalysis} and seems of no use
 * for Soot so marked for future deletion, unless clients object
 */
@Deprecated
public class LocalRefVarsAnalysisWrapper
{
    // compilation options
    private static final boolean computeChecks = true;
    private static final boolean discardKTop = true;

    Map<Unit, List<RefIntPair>> unitToVarsBefore;
    Map<Unit, List<RefIntPair>> unitToVarsAfterFall;
    Map<Unit, List<List<RefIntPair>>> unitToListsOfVarsAfterBranches;
    Map<Unit, List<Object>> unitToVarsNeedCheck;
    Map<Unit, List<RefIntPair>> unitToVarsDontNeedCheck;
    
    BranchedRefVarsAnalysis analysis;

    // utility method to build lists of (ref, value) pairs for a given flow set
    // optionally discard (ref, kTop) pairs.
    private final List<RefIntPair> buildList(FlowSet set)
    {
	List<RefIntPair> lst = new ArrayList<RefIntPair>();
	Iterator<EquivalentValue> it = analysis.refTypeValues.iterator();
	while (it.hasNext()) {
	     EquivalentValue r = it.next();
	    int refInfo = analysis.refInfo(r, set);
	    if (!(discardKTop && (refInfo == BranchedRefVarsAnalysis.kTop)))
		lst.add(analysis.getKRefIntPair(r, refInfo));
	    // remove tops from the list that will be printed for readability
	}
	return lst;
    } // buildList


    // constructor, where we do all our computations
    public LocalRefVarsAnalysisWrapper(ExceptionalUnitGraph graph)
    {
        analysis = new BranchedRefVarsAnalysis(graph);
        
	unitToVarsBefore = new HashMap<Unit, List<RefIntPair>>(graph.size() * 2 + 1, 0.7f);
	unitToVarsAfterFall = new HashMap<Unit, List<RefIntPair>>(graph.size() * 2 + 1, 0.7f);
	unitToListsOfVarsAfterBranches = new HashMap<Unit, List<List<RefIntPair>>>(graph.size() * 2 + 1, 0.7f);
	unitToVarsNeedCheck = new HashMap<Unit, List<Object>>(graph.size() * 2 + 1, 0.7f);
	unitToVarsDontNeedCheck = new HashMap<Unit, List<RefIntPair>>(graph.size() * 2 + 1, 0.7f);
	
	Iterator unitIt = graph.iterator();
	
	while(unitIt.hasNext()) {
	    
	    FlowSet set;
	    Unit s = (Unit) unitIt.next();
	    
	    set = (FlowSet) analysis.getFallFlowAfter(s);
	    unitToVarsAfterFall.put(s, Collections.unmodifiableList(buildList(set)));
	    
	    // we get a list of flow sets for branches, iterate over them
	    {
		List branchesFlowsets = analysis.getBranchFlowAfter(s);
		List<List<RefIntPair>> lst = new ArrayList<List<RefIntPair>>(branchesFlowsets.size());
		
		Iterator it = branchesFlowsets.iterator();
		while (it.hasNext()) {
		    set = (FlowSet) it.next();
		    lst.add(Collections.unmodifiableList(buildList(set)));
		}
		unitToListsOfVarsAfterBranches.put(s, lst);
	    } // done with branches

	    set = (FlowSet) analysis.getFlowBefore(s);
	    unitToVarsBefore.put(s, Collections.unmodifiableList(buildList(set)));
	    // NOTE: that set is used in the compute check bellow too
	    
	    if (computeChecks) {

		ArrayList<RefIntPair> dontNeedCheckVars = new ArrayList<RefIntPair>();
		ArrayList<Object> needCheckVars = new ArrayList<Object>();
		
		HashSet allChecksSet = new HashSet(5, 0.7f);
		allChecksSet.addAll(analysis.unitToArrayRefChecksSet.get(s));
		allChecksSet.addAll(analysis.unitToInstanceFieldRefChecksSet.get(s));
		allChecksSet.addAll(analysis.unitToInstanceInvokeExprChecksSet.get(s));
		allChecksSet.addAll(analysis.unitToLengthExprChecksSet.get(s));
		// set of all references that are subject to a null pointer check at this statement
		
		Iterator it = allChecksSet.iterator();
		
		while (it.hasNext()) {

		    Value v = (Value) it.next();
		    int vInfo = analysis.anyRefInfo(v, set);

		    if (vInfo == BranchedRefVarsAnalysis.kTop) {
			// since it's a check, just print the name of the variable, we know it's top
			needCheckVars.add(v);
		    } else if (vInfo == BranchedRefVarsAnalysis.kBottom) {
			// this could happen in some rare cases; cf known limitations
			// in the BranchedRefVarsAnalysis implementation notes
			needCheckVars.add(analysis.getKRefIntPair(new EquivalentValue(v), vInfo));
		    } else {
			// no check, print the pair (ref, value), so we know why we are not doing the check
			dontNeedCheckVars.add(analysis.getKRefIntPair(new EquivalentValue(v), vInfo));
		    }
		}
		
		unitToVarsNeedCheck.put(s, Collections.unmodifiableList(needCheckVars));
		unitToVarsDontNeedCheck.put(s, Collections.unmodifiableList(dontNeedCheckVars));
	    } // end if computeChecks
	    
	} // end while           

    } // end constructor & computations

    
    /*

        Accesor methods.

	Public accessor methods to the various class fields containing the results
	of the computations.

     */


    public List getVarsBefore(Unit s)
    {
	return unitToVarsBefore.get(s);
    } // end getVarsBefore


    public List getVarsAfterFall(Unit s)
    {
	return unitToVarsAfterFall.get(s);
    } // end getVarsAfterFall


    public List getListsOfVarsAfterBranch(Unit s)
    {
	return unitToListsOfVarsAfterBranches.get(s);
    } // end getListsOfVarsAfterBranch

    public List getVarsNeedCheck(Unit s)
    {
	if (computeChecks)
	    return unitToVarsNeedCheck.get(s);
	else
	    return new ArrayList();
    } // end getVarsNeedCheck

    public List getVarsDontNeedCheck(Unit s)
    {
	if (computeChecks)
	    return unitToVarsDontNeedCheck.get(s);
	else
	    return new ArrayList();
    } // end getVarsNeedCheck


} // end class LocalRefVarsAnalysisWrapper
