/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

package soot.jbco.jimpleTransformations;

import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.*;
import soot.jimple.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 10-Jul-2006 
 */
public class New2InitFlowAnalysis extends BackwardFlowAnalysis<Unit,FlowSet> {

  FlowSet emptySet = new ArraySparseSet();
  
  public New2InitFlowAnalysis(DirectedGraph<Unit> graph) {
    super(graph);
    
    doAnalysis();
  }

  @Override
  protected void flowThrough(FlowSet in, Unit d, FlowSet out) {    
    in.copy(out);
    
    if (d instanceof DefinitionStmt) {
      DefinitionStmt ds = (DefinitionStmt)d;
      if (ds.getRightOp() instanceof NewExpr) {
        Value v = ds.getLeftOp();
        if (v instanceof Local && in.contains(v))
          out.remove(v);
      }
    }    
    else {
    	for (ValueBox useBox : d.getUseBoxes()) {
    		Value v = useBox.getValue();
    		if (v instanceof Local) {
    			out.add(v);
    		}
    	}
    }
    /*else if (d instanceof InvokeStmt) {
        InvokeExpr ie = ((InvokeStmt)d).getInvokeExpr();
        if (ie instanceof SpecialInvokeExpr) {
          Value v = ((SpecialInvokeExpr)ie).getBase();
          if (v instanceof Local && !inf.contains(v))
            outf.add(v);
        }
    }*/
  }

  @Override
  protected FlowSet newInitialFlow() {
    return emptySet.clone();
  }
  
  @Override
  protected FlowSet entryInitialFlow() {
    return emptySet.clone();
  }

  @Override
  protected void merge(FlowSet in1, FlowSet in2, FlowSet out) {
    in1.union(in2, out);
  }

  @Override
  protected void copy(FlowSet source, FlowSet dest) {
    source.copy(dest);
  }
}
