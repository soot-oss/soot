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

package soot.jbco.bafTransformations;

import java.util.*;

import soot.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.Rand;
import soot.toolkits.graph.BriefUnitGraph;
import soot.baf.internal.*;
import soot.baf.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 31-Mar-2006 
 */
public class MoveLoadsAboveIfs extends BodyTransformer  implements IJbcoTransform {

  int movedloads = 0;
  
  public static String dependancies[] = new String[] {"bb.jbco_rlaii", "bb.jbco_ful", "bb.lp"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_rlaii";
  
  public String getName() {
    return name;
  }
  
  public void outputSummary() {
    out.println("Moved Loads Above Ifs: "+movedloads);
  }

  protected void internalTransform(Body b, String phaseName, Map<String,String> options) {
    
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    BriefUnitGraph bug = new BriefUnitGraph(b);
    
    List<Unit> candidates = new ArrayList<Unit>();
    List<Unit> visited = new ArrayList<Unit>();
    List<Unit>worklist = new ArrayList<Unit>();
    worklist.addAll(bug.getHeads());
    
    while(worklist.size()>0) {
      Unit u = (Unit)worklist.remove(0);
      if (visited.contains(u)) 
        continue;
      
      visited.add(u);
      List<Unit> succs = bug.getSuccsOf(u);
      if (u instanceof TargetArgInst) {
	      if (checkCandidate(succs,bug))
	        candidates.add(u);
      }
      
      for (int i = 0; i < succs.size(); i++) {
        Unit o = succs.get(i);
        if (!visited.contains(o))
          worklist.add(o);
      }
    }
    
    int orig = movedloads;
    boolean changed = false;
    PatchingChain<Unit> units = b.getUnits();
    
    for (int i = 0; i < candidates.size(); i++) {
      Unit u = candidates.get(i);
      List<Unit> succs = bug.getSuccsOf(u);
      BLoadInst clone = (BLoadInst)((BLoadInst)succs.get(0)).clone();
     
      if (u instanceof IfNonNullInst || u instanceof IfNullInst) {
        if (category(clone.getOpType())==2 || Rand.getInt(10) > weight)
          continue;

        units.insertBefore(clone,u);
        units.insertBefore(Baf.v().newSwapInst(RefType.v(),clone.getOpType()),u);

        //units.insertAfter(clone,p);
        //units.insertAfter(Baf.v().newSwapInst(RefType.v(),clone.getOpType()),clone);
      } else if (u instanceof OpTypeArgInst) {
        Type t = ((OpTypeArgInst)u).getOpType();
        if (category(t)==2 || Rand.getInt(10) > weight)
          continue;

        units.insertBefore(clone,u);
        Type t2 = clone.getOpType();
        Unit dup;
        if (category(t2)==2) {
          dup = Baf.v().newDup2_x2Inst(t2,null,t,t);
        } else {
          dup = Baf.v().newDup1_x2Inst(t2,t,t);
        }
        units.insertBefore(dup,u);
        units.insertBefore(Baf.v().newPopInst(t2),u);
        /*units.insertAfter(clone,p);
        Type t2 = clone.getOpType();
        Unit dup;
        if (category(t2)==2) {
          dup = Baf.v().newDup2_x2Inst(t2,null,t,t);
        } else {
          dup = Baf.v().newDup1_x2Inst(t2,t,t);
        }
        units.insertAfter(dup,clone);
        units.insertAfter(Baf.v().newPopInst(t2),dup);*/
      } else {
        if (category(clone.getOpType())==2 || Rand.getInt(10) > weight)
          continue;

        units.insertBefore(clone,u);
        units.insertBefore(Baf.v().newSwapInst(IntType.v(),clone.getOpType()),u);
        
        //units.insertAfter(clone,p);
        //units.insertAfter(Baf.v().newSwapInst(IntType.v(),clone.getOpType()),clone);
      }
      
      movedloads++;
      
      // remove old loads after the jump
      for (int j = 0; j < succs.size(); j++) {
        Unit suc = (Unit)succs.get(j);
        List<Unit> sucPreds = bug.getPredsOf(suc);
        
        if (sucPreds.size() > 1) {
          if (suc == ((TargetArgInst)u).getTarget())
            ((TargetArgInst)u).setTarget((Unit)bug.getSuccsOf(suc).get(0));
          else {
            units.insertAfter(Baf.v().newGotoInst((Unit)bug.getSuccsOf(suc).get(0)),u);
          }
        } else {
          units.remove(suc);
        }
      }
      
      if (i < candidates.size() - 1)
        bug = new BriefUnitGraph(b);
      
      changed = true;
    }
    
    if(changed) {
      if (output)
        out.println((movedloads - orig) + " loads moved above ifs in "+b.getMethod().getSignature());
      if (debug)
        StackTypeHeightCalculator.calculateStackHeights(b);
    }
  }
  
  private boolean checkCandidate(List<Unit> succs, BriefUnitGraph bug) {
    if (succs.size() < 2)
      return false;
    
    Object o = succs.get(0);
    for (int i = 1; i < succs.size(); i++) {
      if (succs.get(i).getClass() != o.getClass()) 
        return false;
    }
    
    if (o instanceof BLoadInst) {
      BLoadInst bl = (BLoadInst)o;
      Local l = bl.getLocal();
      for (int i = 1; i < succs.size(); i++) {
        BLoadInst bld = (BLoadInst)succs.get(i);
        if (bld.getLocal() != l || bug.getPredsOf(bld).size() > 1)
          return false;
      }
      return true;
    }
    
    return false;
  }
  
  private int category(Type t) {
    return ((t instanceof LongType || t instanceof DoubleType) ? 2 : 1);
  }
}