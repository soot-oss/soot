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

    
    public ParityAnalysis(UnitGraph g)
    {
        super(g);
        this.g = g;

        doAnalysis();
        
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
	HashMap outMap = (HashMap) out;

	Set keys = inMap1.keySet();
	Iterator it = keys.iterator();
	while (it.hasNext()) {
	 	Object var1 = it.next();
		String inVal1 = (String)inMap1.get(var1);
		String inVal2 = (String)inMap2.get(var1);
		if (inVal1.compareTo(BOTTOM) == 0) {
			outMap.put(var1, inVal2);
		}
		else if (inVal2.compareTo(BOTTOM) == 0) {
		        outMap.put(var1, inVal1);
		}
		else if ((inVal1.compareTo(EVEN) == 0) &&
		   	(inVal2.compareTo(EVEN) == 0)) {
			outMap.put(var1, EVEN);
		}
		else if ((inVal1.compareTo(ODD) == 0) &&
		   	(inVal2.compareTo(ODD) == 0)) {
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

    private String getParity(HashMap in, Value val) {
      if ((val instanceof AddExpr) | (val instanceof SubExpr)) {
	String resVal1 = getParity(in, ((BinopExpr)val).getOp1());
	String resVal2 = getParity(in, ((BinopExpr)val).getOp2());
	if (resVal1.equals(TOP) | resVal2.equals(TOP)) {
	  return TOP;
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
	else if (resVal1.equals(ODD) && resVal2.equals(ODD)) {
	  return ODD;
	}
	else {
	  return EVEN;
	}
      }
      else if (in.containsKey(val)) {
      	return (String)in.get(val);
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
     else {
       return TOP;
     }
     
    }
    
    
    protected void flowThrough(Object inValue, Object unit,
            Object outValue)
    {
        HashMap in  = (HashMap) inValue;
        HashMap out = (HashMap) outValue;
        Stmt    s   = (Stmt)    unit;

	// copy in to out 
	out.putAll(in);
	
        // for each stmt where leftOp is defintionStmt find the parity
	// of rightOp and update parity to EVEN, ODD or TOP

	if (s instanceof DefinitionStmt) {
	  Value left = ((DefinitionStmt)s).getLeftOp();
	  if (left instanceof Local) {
	  	Value right = ((DefinitionStmt)s).getRightOp();
		out.put(left, getParity(out, right));
	  }
	  else {
	    out.put(left, TOP);
	  }
	}
	
    }

// STEP 6: Determine value for start/end node, and
// initial approximation.
//
// start node: locals with BOTTOM
// initial approximation: locals with BOTTOM
    protected Object entryInitialFlow()
    {
	HashMap initMap = new HashMap();
	
	Chain locals = g.getBody().getLocals();
	Iterator it = locals.iterator();
	while (it.hasNext()) {
	  initMap.put(it.next(), BOTTOM);
	}
        return initMap;
    }
        
    protected Object newInitialFlow()
    {
	HashMap initMap = new HashMap();
	
	Chain locals = g.getBody().getLocals();
	Iterator it = locals.iterator();
	while (it.hasNext()) {
	  initMap.put(it.next(), BOTTOM);
	}
        return initMap;

    }
        

}
