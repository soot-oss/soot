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
import soot.util.*;
import java.util.*;

import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.options.*;

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
public class ParityAnalysis extends ForwardFlowAnalysis {

    private UnitGraph g;
    private final static String TOP = "top";
    private final static String BOTTOM = "bottom";
    private final static String EVEN = "even";
    private final static String ODD = "odd";

    private LiveLocals filter;
    
    public ParityAnalysis(UnitGraph g, LiveLocals filter)
    {
        super(g);
        this.g = g;

        this.filter = filter;
        
        filterUnitToBeforeFlow = new HashMap<Stmt, HashMap>();
        buildBeforeFilterMap();
        
        filterUnitToAfterFlow = new HashMap<Stmt, HashMap>();
        
        doAnalysis();
        
    }

    public ParityAnalysis(UnitGraph g){
        super(g);
        this.g = g;

        doAnalysis();
    }
    
    private void buildBeforeFilterMap(){
        
        Iterator it = g.getBody().getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            //if (!(s instanceof DefinitionStmt)) continue;
            //Value left = ((DefinitionStmt)s).getLeftOp();
            //if (!(left instanceof Local)) continue;
        
            //if (!((left.getType() instanceof IntegerType) || (left.getType() instanceof LongType))) continue;
            List list = filter.getLiveLocalsBefore(s);
            HashMap map = new HashMap(); 
            Iterator listIt = list.iterator();
            while (listIt.hasNext()){
                map.put(listIt.next(), BOTTOM);
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

    protected void merge(Object in1, Object in2, Object out)
    {
	HashMap inMap1 = (HashMap) in1;
	HashMap inMap2 = (HashMap) in2;
	HashMap<Object, String> outMap = (HashMap<Object, String>) out;

	Set keys = inMap1.keySet();
	Iterator it = keys.iterator();
	while (it.hasNext()) {
	 	Object var1 = it.next();
        //System.out.println(var1);
		String inVal1 = (String)inMap1.get(var1);
        //System.out.println(inVal1);
		String inVal2 = (String)inMap2.get(var1);
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

    protected void copy(Object source, Object dest) {

        HashMap sourceIn = (HashMap)source;
        HashMap destOut = (HashMap)dest;
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

    private String getParity(HashMap<Value, String> in, Value val) {
        //System.out.println("get Parity in: "+in);
        if ((val instanceof AddExpr) | (val instanceof SubExpr)) {
        	String resVal1 = getParity(in, ((BinopExpr)val).getOp1());
	        String resVal2 = getParity(in, ((BinopExpr)val).getOp2());
	        if (resVal1.equals(TOP) | resVal2.equals(TOP)) {
                return TOP;
	        }  
            else if (resVal1.equals(BOTTOM) | resVal2.equals(BOTTOM)){
                return BOTTOM;
            }
	        else if (resVal1.equals(resVal2)) {
                return EVEN;
	        }
	        else {
	            return ODD;
	        }
        }
        else if (val instanceof MulExpr) {
	        String resVal1 = getParity(in, ((BinopExpr)val).getOp1());
	        String resVal2 = getParity(in, ((BinopExpr)val).getOp2());
	        if (resVal1.equals(TOP) | resVal2.equals(TOP)) {
	            return TOP;
	        }
            else if (resVal1.equals(BOTTOM) | resVal2.equals(BOTTOM)){
                return BOTTOM;
            }
	        else if (resVal1.equals(ODD) && resVal2.equals(ODD)) {
	            return ODD;
	        }
	        else {
	            return EVEN;
	        }
        }
        else if (val instanceof IntConstant) {
	        int value = ((IntConstant)val).value;
	        if ((value % 2) == 0) {
                return EVEN;
	        }
	        else {
	            return ODD;
	        }
        }
        else if (val instanceof LongConstant) {
	        long value = ((LongConstant)val).value;
	        if ((value % 2) == 0) {
	            return EVEN;
	        }
	        else {
	            return ODD;
	        }
        }
        else if (in.containsKey(val)) {
      	    return in.get(val);
        }
        else {
            return TOP;
        }
     
    }
    
    
    protected void flowThrough(Object inValue, Object unit,
            Object outValue)
    {
        HashMap in  = (HashMap) inValue;
        HashMap<Value, String> out = (HashMap<Value, String>) outValue;
        Stmt    s   = (Stmt)    unit;

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
    
    private void buildAfterFilterMap(Stmt s){
        
        List list = filter.getLiveLocalsAfter(s);
        HashMap map = new HashMap(); 
        Iterator listIt = list.iterator();
        while (listIt.hasNext()){
            map.put(listIt.next(), BOTTOM);
        }
        filterUnitToAfterFlow.put(s, map);
        //System.out.println("built afterflow filter map: "+filterUnitToAfterFlow);
    }


// STEP 6: Determine value for start/end node, and
// initial approximation.
//
// start node: locals with BOTTOM
// initial approximation: locals with BOTTOM
    protected Object entryInitialFlow()
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

    private void updateBeforeFilterMap(){
    
        Iterator<Stmt> filterIt = filterUnitToBeforeFlow.keySet().iterator();
        while (filterIt.hasNext()){
            Stmt s = filterIt.next();
            HashMap allData = (HashMap)unitToBeforeFlow.get(s);
            
            HashMap filterData = (HashMap) filterUnitToBeforeFlow.get(s);

            filterUnitToBeforeFlow.put(s, updateFilter(allData, filterData));
            
        }
    }
        
    private void updateAfterFilterMap(Stmt s){
    
        HashMap allData = (HashMap)unitToAfterFlow.get(s);
            
        HashMap filterData = (HashMap) filterUnitToAfterFlow.get(s);

        filterUnitToAfterFlow.put(s, updateFilter(allData, filterData));
            
    }
        
    private HashMap updateFilter(HashMap allData, HashMap filterData){

        if (allData == null) return filterData;
        Iterator<Value> filterVarsIt = filterData.keySet().iterator();
        ArrayList<Value> toRemove = new ArrayList<Value>();
        while (filterVarsIt.hasNext()){
            Value v = filterVarsIt.next();
            if (allData.get(v) == null){
                toRemove.add(v);
                //filterData.put(v, new HashMap());
            }
            else {
                filterData.put(v, allData.get(v));
            }
        }
        Iterator<Value> removeIt = toRemove.iterator();
        while (removeIt.hasNext()){
            filterData.remove(removeIt.next());
        }

        return filterData;
    }

    protected Object newInitialFlow()
    {
	    HashMap<Value, String> initMap = new HashMap<Value, String>();
	
	    Chain locals = g.getBody().getLocals();
	    Iterator it = locals.iterator();
	    while (it.hasNext()) {
            Local next = (Local)it.next();
            //System.out.println("next local: "+next);
            if ((next.getType() instanceof IntegerType) || (next.getType() instanceof LongType)){
	            initMap.put(next, BOTTOM);
            }
	    }
    
        Iterator boxIt = g.getBody().getUseAndDefBoxes().iterator();
        while (boxIt.hasNext()){
            Value val = ((ValueBox)boxIt.next()).getValue();
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
