/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package soot.jimple.toolkits.annotation.parity;

import soot.*;
import java.util.*;

import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.options.*;

import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.*;

// STEP 1: What are we computing?
// SETS OF PAIRS of form (X, T) => Use ArraySparseSet.
//
// STEP 2: Precisely define what we are computing.
// For each statement compute the parity of all variables 
// in the program.
// 
// STEP 3: Decide whether it is a backwards or forwards analysis.
// FORWARDS
//
//
public class ParityAnalysis extends ForwardFlowAnalysis<Unit,Map<Value, ParityAnalysis.Parity>> {
	public enum Parity {
		TOP,
		BOTTOM,
		EVEN,
		ODD;
				
		static Parity valueOf (int v) {
			return (v % 2) == 0 ? EVEN : ODD;
		}
		
		static Parity valueOf (long v) {
			return (v % 2) == 0 ? EVEN : ODD;
		}
	}
	
    private UnitGraph g;

    private LiveLocals filter;
    
    public ParityAnalysis(UnitGraph g, LiveLocals filter)
    {
        super(g);
        this.g = g;

        this.filter = filter;
        
        filterUnitToBeforeFlow = new HashMap<Unit, Map<Value, Parity>>();
        buildBeforeFilterMap();
        
        filterUnitToAfterFlow = new HashMap<Unit, Map<Value, Parity>>();
        
        doAnalysis();
        
    }

    public ParityAnalysis(UnitGraph g){
        super(g);
        this.g = g;

        doAnalysis();
    }
    
    private void buildBeforeFilterMap(){
        
        for (Unit s : g.getBody().getUnits()) {
            //if (!(s instanceof DefinitionStmt)) continue;
            //Value left = ((DefinitionStmt)s).getLeftOp();
            //if (!(left instanceof Local)) continue;
        
            //if (!((left.getType() instanceof IntegerType) || (left.getType() instanceof LongType))) continue;

            Map<Value, Parity> map = new HashMap<Value, Parity>();            
            for (Local l : filter.getLiveLocalsBefore(s)) {
            	map.put(l, BOTTOM);
            }
                        
            filterUnitToBeforeFlow.put(s, map);
        } 
        //System.out.println("init filtBeforeMap: "+filterUnitToBeforeFlow);           
    }

// STEP 4: Is the merge operator union or intersection?
// 
// merge  | bottom | even   | odd   | top
// -------+--------+--------+-------+--------
// bottom | bottom | even   | odd   | top
// -------+--------+--------+-------+--------
// even   | even   | even   | top   | top
// -------+--------+--------+-------+--------
// odd    | odd    | top    | odd   | top  
// -------+--------+--------+-------+--------
// top    | top    | top    | top   | top
//

    @Override
    protected void merge(Map<Value, Parity> inMap1, Map<Value, Parity> inMap2, Map<Value, Parity> outMap)
    {
	for (Value var1 : inMap1.keySet()) {
        //System.out.println(var1);
		Parity inVal1 = inMap1.get(var1);
        //System.out.println(inVal1);
		Parity inVal2 = inMap2.get(var1);
        //System.out.println(inVal2);
       // System.out.println("before out "+outMap.get(var1));

        if (inVal2 == null){
            outMap.put(var1, inVal1);
        }
	    else if (inVal1.equals(BOTTOM)) {
			outMap.put(var1, inVal2);
		}
		else if (inVal2.equals(BOTTOM)) {
		        outMap.put(var1, inVal1);
		}
		else if ((inVal1.equals(EVEN)) &&
		   	(inVal2.equals(EVEN))) {
			outMap.put(var1, EVEN);
		}
		else if ((inVal1.equals(ODD)) &&
		   	(inVal2.equals(ODD))) {
			outMap.put(var1, ODD);
		}
		else {
			outMap.put(var1, TOP);
		}
	}
	
    }

// STEP 5: Define flow equations.
// in(s) = ( out(s) minus defs(s) ) union uses(s)
//

    @Override
    protected void copy(Map<Value, Parity> sourceIn, Map<Value, Parity> destOut) {
        destOut.clear();
        destOut.putAll(sourceIn);
    }
   
    // Parity Tests: 	even + even = even
    // 			even + odd = odd 
    // 			odd + odd = even
    //
    // 			even * even = even
    // 			even * odd = even
    // 			odd * odd = odd
    //
    // 			constants are tested mod 2
    //

    private Parity getParity(Map<Value, Parity> in, Value val) {
        //System.out.println("get Parity in: "+in);
        if ((val instanceof AddExpr) | (val instanceof SubExpr)) {
        	Parity resVal1 = getParity(in, ((BinopExpr)val).getOp1());
        	Parity resVal2 = getParity(in, ((BinopExpr)val).getOp2());
        	
	        if (resVal1.equals(TOP) | resVal2.equals(TOP)) {
                return TOP;
	        }  
            if (resVal1.equals(BOTTOM) | resVal2.equals(BOTTOM)){
                return BOTTOM;
            }
	        if (resVal1.equals(resVal2)) {
                return EVEN;
	        }
	        
	            return ODD;
        }
        
        if (val instanceof MulExpr) {
        	Parity resVal1 = getParity(in, ((BinopExpr)val).getOp1());
        	Parity resVal2 = getParity(in, ((BinopExpr)val).getOp2());
	        if (resVal1.equals(TOP) | resVal2.equals(TOP)) {
	            return TOP;
	        }
            if (resVal1.equals(BOTTOM) | resVal2.equals(BOTTOM)){
                return BOTTOM;
            }
	        if (resVal1.equals(resVal2)) {
	            return resVal1;
	        }
	        
	        return EVEN;
        }
        if (val instanceof IntConstant) {
	        int value = ((IntConstant)val).value;
	        return valueOf(value);
        }
        if (val instanceof LongConstant) {
	        long value = ((LongConstant)val).value;
	        return valueOf(value);
        }
        
        Parity p = in.get(val);
        if (p == null)
        	return TOP; 
        return p;
    }
    
    @Override
    protected void flowThrough(Map<Value, Parity> in, Unit s,
    		Map<Value, Parity> out)
    {

	    // copy in to out 
	    out.putAll(in);
	
        // for each stmt where leftOp is defintionStmt find the parity
	    // of rightOp and update parity to EVEN, ODD or TOP

        //boolean useS = false;
        
	    if (s instanceof DefinitionStmt) {
	        Value left = ((DefinitionStmt)s).getLeftOp();
	        if (left instanceof Local) {
                if ((left.getType() instanceof IntegerType) || (left.getType() instanceof LongType)){
                    //useS = true;
	  	            Value right = ((DefinitionStmt)s).getRightOp();
		            out.put(left, getParity(out, right));
                }
	        }
	    }

        // get all use and def boxes of s 
        // if use or def is int or long constant add their parity
        for (ValueBox next : s.getUseAndDefBoxes()) {
            Value val = next.getValue();
            //System.out.println("val: "+val.getClass());
            if (val instanceof ArithmeticConstant){
                out.put(val, getParity(out, val));
                //System.out.println("out map: "+out);
            }
        }
        
        //if (useS){
        if (Options.v().interactive_mode()){
            buildAfterFilterMap(s);
            updateAfterFilterMap(s);
        }
        //}
    }
    
    private void buildAfterFilterMap(Unit s){
        
        Map<Value, Parity> map = new HashMap<Value, Parity>();
        for (Local local :  filter.getLiveLocalsAfter(s)) {
            map.put(local, BOTTOM);
        }
        filterUnitToAfterFlow.put(s, map);
        //System.out.println("built afterflow filter map: "+filterUnitToAfterFlow);
    }


// STEP 6: Determine value for start/end node, and
// initial approximation.
//
// start node: locals with BOTTOM
// initial approximation: locals with BOTTOM
    @Override
    protected Map<Value, Parity> entryInitialFlow()
    {
	/*HashMap initMap = new HashMap();
	
	Chain locals = g.getBody().getLocals();
	Iterator it = locals.iterator();
	while (it.hasNext()) {
	  initMap.put(it.next(), BOTTOM);
	}
        return initMap;*/

        return newInitialFlow();
    }

    private void updateBeforeFilterMap() {    
        for (Unit s : filterUnitToBeforeFlow.keySet()) {
            Map<Value, Parity> allData = getFlowBefore(s);            
            Map<Value, Parity> filterData = filterUnitToBeforeFlow.get(s);
            filterUnitToBeforeFlow.put(s, updateFilter(allData, filterData));            
        }
    }
        
    private void updateAfterFilterMap(Unit s) {    
    	Map<Value, Parity> allData = getFlowAfter(s);            
    	Map<Value, Parity> filterData = filterUnitToAfterFlow.get(s);
        filterUnitToAfterFlow.put(s, updateFilter(allData, filterData));            
    }
        
    private Map<Value, Parity> updateFilter(Map<Value, Parity> allData, Map<Value, Parity> filterData){

        if (allData == null) 
        	return filterData;

        for (Value v : filterData.keySet()) {
        	Parity d = allData.get(v);
            if (d == null) {
            	filterData.remove(v);
            } else {
                filterData.put(v, d);
            }
        }

        return filterData;
    }
    
    @Override
    protected Map<Value, Parity> newInitialFlow()
    {
	    Map<Value, Parity> initMap = new HashMap<Value, Parity>();
	
	    for (Local l : g.getBody().getLocals()) {
	    	Type t = l.getType();
            //System.out.println("next local: "+next);
            if ((t instanceof IntegerType) || (t instanceof LongType)) {
	            initMap.put(l, BOTTOM);
            }
	    }
    
        for (ValueBox vb : g.getBody().getUseAndDefBoxes()) {
            Value val = vb.getValue();
            if (val instanceof ArithmeticConstant) {
                initMap.put(val, getParity(initMap, val));
            }
        }

        if (Options.v().interactive_mode()){
            updateBeforeFilterMap();
        }
        
        return initMap;

    }
        

}
