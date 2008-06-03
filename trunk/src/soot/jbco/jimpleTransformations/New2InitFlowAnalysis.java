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
import java.util.*;
import soot.toolkits.scalar.*;
import soot.*;
import soot.jimple.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 10-Jul-2006 
 */
public class New2InitFlowAnalysis extends BackwardFlowAnalysis {

  FlowSet emptySet = new ArraySparseSet();
  
  public New2InitFlowAnalysis(DirectedGraph graph) {
    super(graph);
    
    doAnalysis();
  }

  protected void flowThrough(Object in, Object d, Object out) {
    FlowSet inf = (FlowSet)in;
    FlowSet outf = (FlowSet)out;
    
    inf.copy(outf);
    
    if (d instanceof DefinitionStmt) {
      DefinitionStmt ds = (DefinitionStmt)d;
      if (ds.getRightOp() instanceof NewExpr) {
        Value v = ds.getLeftOp();
        if (v instanceof Local && inf.contains(v))
          outf.remove(v);
      }
    } 
    
    else {
      Iterator it = ((Unit)d).getUseBoxes().iterator();
      while (it.hasNext()) {
        Value v = ((ValueBox)it.next()).getValue();
        if (v instanceof Local)
          outf.add(v);
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

  protected Object newInitialFlow() {
    return emptySet.clone();
  }

  protected Object entryInitialFlow() {
    return emptySet.clone();
  }

  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = (FlowSet) in1,
    inSet2 = (FlowSet) in2;
    
    FlowSet outSet = (FlowSet) out;

    inSet1.union(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source,
    destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}
