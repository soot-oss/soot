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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

/* Reference Version: $SootVersion: 1.2.5.dev.5 $ */

package soot.jimple.toolkits.base;
import soot.options.*;
import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.util.*;

import java.util.*;

public class Aggregator extends BodyTransformer
{
    public Aggregator( Singletons.Global g ) {}
    public static Aggregator v() { return G.v().soot_jimple_toolkits_base_Aggregator(); }

    /** Traverse the statements in the given body, looking for
      *  aggregation possibilities; that is, given a def d and a use u,
      *  d has no other uses, u has no other defs, collapse d and u. 
      * 
      * option: only-stack-locals; if this is true, only aggregate variables
                        starting with $ */
    protected void internalTransform(Body b, String phaseName, Map<String, String> options)
    {
        StmtBody body = (StmtBody)b;
        boolean onlyStackVars = PhaseOptions.getBoolean(options, "only-stack-locals"); 

		if (Options.v().time()) 
			Timers.v().aggregationTimer.start();
		
        int aggregateCount = 1;

        boolean changed = false;

        Map<ValueBox, Zone> boxToZone = new HashMap<ValueBox, Zone>(body.getUnits().size() * 2 + 1, 0.7f);

        // Determine the zone of every box
        {
            Zonation zonation = new Zonation(body);
            
            for (Unit u : body.getUnits()) {
                Zone zone = zonation.getZoneOf(u);
                
                for (ValueBox box : u.getUseBoxes()) {
                    boxToZone.put(box, zone);
                }
                
                for (ValueBox box : u.getDefBoxes()) {
                    boxToZone.put(box, zone);
                }   
            }
        }        
        
                     
        do {
            if(Options.v().verbose())
                G.v().out.println("[" + body.getMethod().getName() + "] Aggregating iteration " + aggregateCount + "...");
        
            // body.printTo(new java.io.PrintWriter(G.v().out, true));
            
            changed = internalAggregate(body, boxToZone, onlyStackVars);
            
            aggregateCount++;
        } while(changed);

		if (Options.v().time()) 
			Timers.v().aggregationTimer.end();
        
    }
  
  private static boolean internalAggregate(StmtBody body, Map<ValueBox, Zone> boxToZone, boolean onlyStackVars)
    {
      boolean hadAggregation = false;
      Chain<Unit> units = body.getUnits();
      
      ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body);
      LocalDefs localDefs = LocalDefs.Factory.newLocalDefs(graph);
      LocalUses localUses = LocalUses.Factory.newLocalUses(body, localDefs);
      
      List<Unit> unitList = new PseudoTopologicalOrderer<Unit>().newList(graph,false);
      for (Unit u : unitList) {
          if (!(u instanceof AssignStmt))
            continue;
          AssignStmt s = (AssignStmt) u;
          
          Value lhs = s.getLeftOp();
          if (!(lhs instanceof Local))
            continue;
          Local lhsLocal = (Local) lhs;
    
          if(onlyStackVars && !lhsLocal.getName().startsWith("$"))
            continue;
            
          List<UnitValueBoxPair> lu = localUses.getUsesOf(s);
          if (lu.size() != 1)
            continue;
          
          UnitValueBoxPair usepair = lu.get(0);
          Unit use = usepair.unit;
          ValueBox useBox = usepair.valueBox;
              
          List<Unit> ld = localDefs.getDefsOfAt(lhsLocal, use);
          if (ld.size() != 1)
            continue;
   
          // Check to make sure aggregation pair in the same zone
            if(boxToZone.get(s.getRightOpBox()) != boxToZone.get(usepair.valueBox))
            {
                continue;
            }  
             
          /* we need to check the path between def and use */
          /* to see if there are any intervening re-defs of RHS */
          /* in fact, we should check that this path is unique. */
          /* if the RHS uses only locals, then we know what
             to do; if RHS has a method invocation f(a, b,
             c) or field access, we must ban field writes, other method
             calls and (as usual) writes to a, b, c. */
                      
          boolean cantAggr = false;
          boolean propagatingInvokeExpr = false;
          boolean propagatingFieldRef = false;
          boolean propagatingArrayRef = false;
          List<FieldRef> fieldRefList = new ArrayList<FieldRef>();
      
          List<Value> localsUsed = new ArrayList<Value>();
          for (ValueBox vb : s.getUseBoxes()) {
              Value v = vb.getValue();
                if (v instanceof Local) {
                    localsUsed.add(v);
                } else if (v instanceof InvokeExpr) {
                    propagatingInvokeExpr = true;
                }    
                else if(v instanceof ArrayRef) {
                    propagatingArrayRef = true;
                }
                else if(v instanceof FieldRef)
                {
                    propagatingFieldRef = true;
                    fieldRefList.add((FieldRef) v);
                }
            }
          
          // look for a path from s to use in graph.
          // only look in an extended basic block, though.

          List<Unit> path = graph.getExtendedBasicBlockPathBetween(s, use);
      
          if (path == null)
            continue;

          Iterator<Unit> pathIt = path.iterator();

          // skip s.
          if (pathIt.hasNext())
            pathIt.next();

          while (pathIt.hasNext() && !cantAggr)
          {
              Stmt between = (Stmt)(pathIt.next());
          
              if(between != use)    
              {
                // Check for killing definitions
                
            	  for (ValueBox vb : between.getDefBoxes()) {
                      Value v = vb.getValue();
                      if (localsUsed.contains(v))
                      { 
                            cantAggr = true; 
                            break; 
                      }
                      
                      if (propagatingInvokeExpr || propagatingFieldRef || propagatingArrayRef)
                      {
                          if (v instanceof FieldRef)
                          {
                              if(propagatingInvokeExpr)
                              {
                                  cantAggr = true; 
                                  break;
                              }
                              else if(propagatingFieldRef)
                              {
                                  // Can't aggregate a field access if passing a definition of a field 
                                  // with the same name, because they might be aliased
                            	  for (FieldRef fieldRef : fieldRefList) {
                                      if(((FieldRef) v).getField() == fieldRef.getField())
                                      {
                                          cantAggr = true;
                                          break;
                                      } 
                                  }
                              } 
                           }
                           else if(v instanceof ArrayRef)
                           {
                                if(propagatingInvokeExpr)
                                {   
                                    // Cannot aggregate an invoke expr past an array write
                                    cantAggr = true;
                                    break;
                                }
                                else if(propagatingArrayRef)
                                {
                                    // cannot aggregate an array read past a write
                                    // this is somewhat conservative
                                    // (if types differ they may not be aliased)
                                    
                                    cantAggr = true;
                                    break;
                                }
                           }
                      }
                  }
                  
                  // Make sure not propagating past a {enter,exit}Monitor
                    if(propagatingInvokeExpr && between instanceof MonitorStmt)
                        cantAggr = true;
              }  
                            
              // Check for intervening side effects due to method calls
                if(propagatingInvokeExpr || propagatingFieldRef || propagatingArrayRef)
                    {
                      for( final ValueBox box : between.getUseBoxes() ) {
                          if(between == use && box == useBox)
                          {
                                // Reached use point, stop looking for
                                // side effects
                                break;
                          }
                          
                          Value v = box.getValue();
                          
                            if (v instanceof InvokeExpr || 
                                (propagatingInvokeExpr && (v instanceof FieldRef || v instanceof ArrayRef)))
                            {
                                cantAggr = true;
                                break;
                            }
                            
                        }
                    }
            }

          // we give up: can't aggregate.
          if (cantAggr)
          {
            continue;
          }
          /* assuming that the d-u chains are correct, */
          /* we need not check the actual contents of ld */
          
          Value aggregatee = s.getRightOp();
          
          if (usepair.valueBox.canContainValue(aggregatee))
            {
              boolean wasSimpleCopy = isSimpleCopy( usepair.unit );
              usepair.valueBox.setValue(aggregatee);
              units.remove(s);
              hadAggregation = true;
              // clean up the tags. If s was not a simple copy, the new statement should get
              // the tags of s.
              // OK, this fix was wrong. The condition should not be
              // "If s was not a simple copy", but rather "If usepair.unit
              // was a simple copy". This way, when there's a load of a constant
              // followed by an invoke, the invoke gets the tags.
              if( wasSimpleCopy ) {
                  //usepair.unit.removeAllTags();
                  usepair.unit.addAllTagsOf( s );
              }
            }
          else
            {/*
            if(Options.v().verbose())
            {
                G.v().out.println("[debug] failed aggregation");
                  G.v().out.println("[debug] tried to put "+aggregatee+
                                 " into "+usepair.stmt + 
                                 ": in particular, "+usepair.valueBox);
                  G.v().out.println("[debug] aggregatee instanceof Expr: "
                                 +(aggregatee instanceof Expr));
            }*/
            }
        }
      return hadAggregation;
    }
  private static boolean isSimpleCopy( Unit u ) {
      if( !(u instanceof DefinitionStmt) ) return false;
      DefinitionStmt defstmt = (DefinitionStmt) u;
      if( !( defstmt.getRightOp() instanceof Local ) ) return false;
      if( !( defstmt.getLeftOp() instanceof Local ) ) return false;
      return true;
  }
        
}

