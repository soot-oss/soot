/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.graph;







import soot.*;
import soot.util.*;
import java.util.*;

public class UnitGraph implements DirectedGraph
{
    List heads;
    List tails;

    private Map unitToSuccs;
    private Map unitToPreds;        
    SootMethod method;
    int size;


    Body body;
    Chain unitChain;
    

    public Body getBody()
    {
        return body;
    }

    UnitGraph(Body unitBody, boolean addExceptionEdges)
    {
        body = unitBody;
        unitChain = body.getUnits();
        method = getBody().getMethod();
        
        if(Main.isVerbose)
            System.out.println("[" + method.getName() + 
                               "]     Constructing UnitGraph...");
      
        if(Main.isProfilingOptimization)
            Main.graphTimer.start();
      
        
        // Build successors
        {
            Map classToHandler = new HashMap(); // list of exceptions being caught, and their handlers
            
            unitToSuccs = new HashMap(size * 2 + 1, 0.7f);
            unitToPreds = new HashMap(size * 2 + 1, 0.7f);


            // Add regular successors
            {
                Iterator unitIt = unitChain.iterator();
                Unit currentUnit, nextUnit;
                
                nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                
                while(nextUnit != null) {
                    currentUnit = nextUnit;
                    nextUnit = unitIt.hasNext() ? (Unit) unitIt.next(): null;
                    
                    List successors = new ArrayList();
                    
                    
                    // Put the next statement as the successor
                    if( currentUnit.fallsThrough() ) {
                        if(nextUnit != null)
                            successors.add(nextUnit);
//                          else
//                              throw new RuntimeException("last unit of a method should not fall through: " + currentUnit + 
//                                  " " + body.getMethod().getSignature());
                    }
                        
                    if( currentUnit.branches() ) {
                        Iterator targetIt = currentUnit.getUnitBoxes().iterator();
                        while(targetIt.hasNext()) {
                            successors.add(((UnitBox) targetIt.next()).getUnit());
                        }
                    }
                    

                    // Store away successors
                    unitToSuccs.put(currentUnit, successors);
                }
            }

            // Add exception based successors
            if(addExceptionEdges)
                {
                    Map beginToHandler = new HashMap();
                    
                    Iterator trapIt = body.getTraps().iterator();
                    
                    while(trapIt.hasNext())
                    {
                        Trap trap = (Trap) trapIt.next();

                        Unit beginUnit = (Unit) trap.getBeginUnit();
                        Unit handlerUnit = (Unit) trap.getHandlerUnit();
                        Unit endUnit = (Unit) trap.getEndUnit();
                        Iterator unitIt = unitChain.iterator(beginUnit);
                        
                        beginToHandler.put(beginUnit, handlerUnit);

                        
                        Unit u;

                        

                        do  {
                            u = (Unit) unitIt.next();
                            
                            ((List) unitToSuccs.get(u)).add(handlerUnit);
                            
                        } while(u != endUnit);
                        
                        
                    }

                    
                    // Add edges from the predecessors of begin statements directly to the handlers
                    // This is necessary because sometimes the first statement of try block
                    // is not even fully executed before an exception is thrown
                    {
                        Iterator unitIt = body.getUnits().iterator();
                        
                        while(unitIt.hasNext())
                        {
                            Unit u = (Unit) unitIt.next();
                            
                            List succs = ((List) unitToSuccs.get(u));
                            
                            List succsClone = new ArrayList();
                            succsClone.addAll(succs);
                                // need to clone it because you are potentially 
                                // modifying it
                                
                            Iterator succIt = succsClone.iterator();
                                
                            while(succIt.hasNext())
                            {
                                Unit succ = (Unit) succIt.next();
                                
                                if(beginToHandler.containsKey(succ))
                                {
                                    // Add an edge from s to succ
                                    
                                    Unit handler = (Unit) beginToHandler.get(succ);
                                    
                                    if(!succs.contains(handler))
                                        succs.add(handler);
                                }
                            }
                        }
                    }
                }

            // Make successors unmodifiable
            {
                Iterator unitIt = body.getUnits().iterator();
                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
        
                    unitToSuccs.put(s, Collections.unmodifiableList((List) unitToSuccs.get(s)));
                }
            }
        }


        // Build predecessors
        {
            Map stmtToPredList = new HashMap(size * 2 + 1, 0.7f);

            // initialize the pred sets to empty
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    stmtToPredList.put(unitIt.next(), new ArrayList());
                }
            }

            // Modify preds set for each successor for this statement
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
                    Iterator succIt = ((List) unitToSuccs.get(s)).iterator();

                    while(succIt.hasNext())
                    {
			Unit successor = (Unit) succIt.next();
                        List predList = (List) stmtToPredList.get(successor);
			try {
			    predList.add(s);
			} catch(NullPointerException e) {
			    System.out.println(s + "successor: " + successor);
			    throw e;
			}
                    }
                }
            }


            // Convert pred lists to arrays
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();

                    List predList = (List) stmtToPredList.get(s);
                    unitToPreds.put(s, Collections.unmodifiableList(predList));
                }
            }

        }

        // Build tails
        {
            List tailList = new ArrayList();

            // Build the set
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();

                    List succs = (List) unitToSuccs.get(s);

                    if(succs.size() == 0)
                        tailList.add(s);
                }
            }

            tails = Collections.unmodifiableList(tailList);
        }

        // Build heads
        {
            List headList = new ArrayList();

            // Build the set
            {
                Iterator unitIt = body.getUnits().iterator();

                while(unitIt.hasNext())
                {
                    Unit s = (Unit) unitIt.next();
                    List preds = (List) unitToPreds.get(s);

                    if(preds.size() == 0)
                        headList.add(s);
                }
            }

            heads = Collections.unmodifiableList(headList);
        }

        if(Main.isProfilingOptimization)
            Main.graphTimer.end();
    }

    public List getHeads()
    {
        return heads;
    }

    public List getTails()
    {
        return tails;
    }

    public List getPredsOf(Object s)
    {
        if(!unitToPreds.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);

        return (List) unitToPreds.get(s);
    }

    public List getSuccsOf(Object s)
    {
        if(!unitToSuccs.containsKey(s))
            throw new RuntimeException("Invalid stmt" + s);

        return (List) unitToSuccs.get(s);
    }

    public Iterator iterator()
    {
        return unitChain.iterator();
    }

    public int size()
    {
        return size;
    }  

  /** Look for a path, in g, from def to use. 
   * This path has to lie inside an extended basic block 
   * (and this property implies uniqueness.) */
  /* The path returned includes from and to.
     returns null if there is no such path */
  
  public List getExtendedBasicBlockPathBetween(Unit from, Unit to)
    {
        UnitGraph g = this;
        
      // if this holds, we're doomed to failure!!!
      if (g.getPredsOf(to).size() > 1)
        return null;

      // pathStack := list of succs lists
      // pathStackIndex := last visited index in pathStack
      LinkedList pathStack = new LinkedList();
      LinkedList pathStackIndex = new LinkedList();

      pathStack.add(from);
      pathStackIndex.add(new Integer(0));

      int psiMax = (g.getSuccsOf((Unit)pathStack.get(0))).size();
      int level = 0;
      while (((Integer)pathStackIndex.get(0)).intValue() != psiMax)
        {
          int p = ((Integer)(pathStackIndex.get(level))).intValue();

          List succs = g.getSuccsOf((Unit)(pathStack.get(level)));
          if (p >= succs.size())
            {
              // no more succs - backtrack to previous level.

              pathStack.remove(level);
              pathStackIndex.remove(level);

              level--;
              int q = ((Integer)pathStackIndex.get(level)).intValue();
              pathStackIndex.set(level, new Integer(q+1));
              continue;
            }

          Unit betweenUnit = (Unit)(succs.get(p));

          // we win!
          if (betweenUnit == to)
            {
              pathStack.add(to);
              return pathStack;
            }

          // check preds of betweenUnit to see if we should visit its kids.
          if (g.getPredsOf(betweenUnit).size() > 1)
            {
              pathStackIndex.set(level, new Integer(p+1));
              continue;
            }

          // visit kids of betweenUnit.
          level++;
          pathStackIndex.add(new Integer(0));
          pathStack.add(betweenUnit);
        }
      return null;
    }       
}
