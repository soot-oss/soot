/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot.jimple;

import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.scalar.*;
import soot.jimple.toolkits.typing.*;
import soot.jimple.toolkits.base.*;
import soot.util.*;
import java.util.*;
import soot.baf.*;
import java.io.*;

public class JimpleBody extends StmtBody
{
    /**
        Construct an empty JimpleBody 
     **/
    
    JimpleBody(SootMethod m)
    {
        super(m);
    }

    /**
       Construct an extremely empty JimpleBody, for parsing into.
    **/

    JimpleBody() 
    {
    }

    /**
        Constructs a JimpleBody from the given Body.
     */  
    
//      public JimpleBody(Body body, Map options)
//      {
//          super(body.getMethod());

//          applyPhaseOptions(options);
//      }

    /** Clones the current body, making deep copies of the contents. */

    public Object clone()
    {
        Body b = new JimpleBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    public void applyPhaseOptions(Map options) 
    { 
	boolean noSplitting = Options.getBoolean(options, "no-splitting");
	boolean noTyping = Options.getBoolean(options, "no-typing");
	boolean aggregateAllLocals = Options.getBoolean(options, "aggregate-all-locals");
        boolean noAggregating = Options.getBoolean(options, "no-aggregating");
        boolean useOriginalNames = Options.getBoolean(options, "use-original-names");
        boolean usePacking = Options.getBoolean(options, "pack-locals");
        boolean noCopyPropagating = Options.getBoolean(options, "no-cp");
        
        boolean noNopElimination = Options.getBoolean(options, "no-nop-elimination");

        boolean verbatim = Options.getBoolean(options, "verbatim");

        if (verbatim)
            return;

	if(!noSplitting)
        {
            if(Main.isProfilingOptimization)
                Main.splitTimer.start();

            LocalSplitter.v().transform(this, "jb.ls");

            if(!noTyping)
            {
                if(Main.isProfilingOptimization)
                    Main.assignTimer.start();

                TypeAssigner.v().transform(this, "jb.tr");

                if(typingFailed())
                {
                    patchForTyping();
                    
                    TypeAssigner.v().transform(this, "jb.trp");
                    
                    if(typingFailed())
                        throw new RuntimeException("type inference failed!");
                        
                }
            }
        }
	
        
        if(aggregateAllLocals)
        {
            Aggregator.v().transform(this, "jb.a");
            UnusedLocalEliminator.v().transform(this, "jb.ule");
        }
        else if(!noAggregating)
        {
            Aggregator.v().transform(this, "jb.asv", "only-stack-locals");
            UnusedLocalEliminator.v().transform(this, "jb.ule");
        }

        if(!useOriginalNames)
            LocalNameStandardizer.v().transform(this, "jb.lns");
        else
        {   
            LocalPacker.v().transform(this, "jb.ulp", "unsplit-original-locals");
            LocalNameStandardizer.v().transform(this, "jb.lns", "only-stack-locals");
        }

        if(!noCopyPropagating)
        {
            CopyPropagator.v().transform(this, "jb.cp", "only-stack-locals");
            DeadAssignmentEliminator.v().transform(this, "jp.dae", "only-stack-locals");
            UnusedLocalEliminator.v().transform(this, "jb.cp-ule");
        }
        
        //printDebugTo(new PrintWriter(System.out, true));
        
        if(usePacking)
        {
            LocalPacker.v().transform(this, "jb.lp");
        }


        if(!noNopElimination)
            NopEliminator.v().transform(this, "jb.ne");
                    
        if(soot.Main.isProfilingOptimization)
            soot.Main.stmtCount += getUnits().size();
    }


    /** Make sure that the JimpleBody is well formed.  If not, throw an exception.
        Right now, performs only a handful of checks.
      */
      
    public void validate()
    {
        super.validate();
        // Check validity of traps.
        {
            Iterator it = getTraps().iterator();
            
            while(it.hasNext())
            {
                Trap t = (Trap) it.next();
                
                Stmt s = (Stmt) t.getHandlerUnit();
                
		
                if(!(s instanceof IdentityStmt) || !(((IdentityStmt) s).getRightOp() instanceof CaughtExceptionRef)){
		    System.out.println(s);
                    throw new RuntimeException("Trap handler is not of the form x := caughtexceptionref");
		}
            }
        }
    }
    
    
    /** Temporary patch to get the typing algorithm working.
      */
      
    private void patchForTyping()
    {
        int localCount = 0;
        Local newObjectLocal = null;
        
        CopyPropagator.v().transform(this, "jb.pft.cp");
        DeadAssignmentEliminator.v().transform(this, "jb.pft.dae");
        UnusedLocalEliminator.v().transform(this, "jb.pft.ule");
     
        List unitList = new ArrayList(); 
        unitList.addAll(getUnits());

        Iterator it = unitList.iterator();
        for (; it.hasNext(); )
          {
            Stmt s = (Stmt)it.next();
                    
            if(s instanceof AssignStmt)
            {
                AssignStmt as = (AssignStmt) s;
                
                if(as.getRightOp() instanceof NewExpr &&
                   as.getLeftOp() instanceof Local)
                {
                    // Add new local
                        Local tmpLocal = Jimple.v().newLocal("tmp" + localCount, 
                            UnknownType.v());
                        getLocals().add(tmpLocal);
                            
                        localCount++;
                    
                    // Change left hand side of new
                        newObjectLocal = (Local) as.getLeftOp();
                        as.setLeftOp(tmpLocal);
                    
                    // Find matching special invoke
                    {
                        Iterator matchIt = getUnits().iterator(getUnits().getSuccOf(s));
                        boolean foundMatch = false;
                               
                        while(matchIt.hasNext())
                        {   
                            Stmt r = (Stmt) matchIt.next();
                            
                            if(r instanceof InvokeStmt)
                            {
                               InvokeExpr expr = (InvokeExpr) ((InvokeStmt) r).getInvokeExpr();
                                
                                if(expr instanceof SpecialInvokeExpr &&
                                    ((SpecialInvokeExpr) expr).getBase() == newObjectLocal)
                                {
                                    // Set base of special invoke
                                        ((SpecialInvokeExpr) expr).setBase(tmpLocal);
                                    
                                    // Add copy newObjectLocal = tmpLocal
                                    getUnits().insertAfter(Jimple.v().newAssignStmt(newObjectLocal,
                                        tmpLocal), r);
                                 
                                    foundMatch = true;
                                    break;       
                                }
                            }
                        }
                        
                        if(!foundMatch)
                            throw new RuntimeException("unable to patch code"); 
                    }
                }
            }
        }
    }
    
    private boolean typingFailed()
    {
        // Check to see if any locals are untyped
        {
            Iterator localIt = this.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();

                  if(l.getType().equals(UnknownType.v()) ||
                    l.getType().equals(ErroneousType.v()))
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    public void insertIdentityStmts()
    {
        int i = 0;

        if (!getMethod().isStatic())
         {
             Local l = Jimple.v().newLocal("this", 
                                           RefType.v(getMethod().getDeclaringClass()));
             getLocals().add(l);
             getUnits().add(Jimple.v().newIdentityStmt(l, Jimple.v().newThisRef((RefType)l.getType())));
         }

        Iterator parIt = getMethod().getParameterTypes().iterator();
        while (parIt.hasNext())
        {
            Type t = (Type)parIt.next();
            Local l = Jimple.v().newLocal("parameter"+i, t);
            getLocals().add(l);
            getUnits().add(Jimple.v().newIdentityStmt(l, Jimple.v().newParameterRef(l.getType(), i)));
            i++;
        }
    }

    public Stmt getFirstNonIdentityStmt()
    {
        Iterator it = getUnits().iterator();
        Object o = null;
        while (it.hasNext())
            if (!((o = it.next()) instanceof IdentityStmt))
                break;
        if (o == null)
            throw new RuntimeException("no non-id statements!");
        return (Stmt)o;
    }

    public Local getThisLocal()
    {
        // Look for the first identity stmt assigning from @this.
        {
            Iterator unitsIt = getUnits().iterator();
            while (unitsIt.hasNext())
            {
                Stmt s = (Stmt)unitsIt.next();
                if (s instanceof IdentityStmt && 
                    ((IdentityStmt)s).getRightOp() instanceof ThisRef)
                    return (Local)(((IdentityStmt)s).getLeftOp());
            }
        }

        throw new RuntimeException("couldn't find identityref!");
    }

    public Local getParameterLocal(int i)
    {
        // Look for the first identity stmt assigning from @this.
        {
            Iterator unitsIt = getUnits().iterator();
            while (unitsIt.hasNext())
            {
                Stmt s = (Stmt)unitsIt.next();
                if (s instanceof IdentityStmt && 
                    ((IdentityStmt)s).getRightOp() instanceof ParameterRef)
                {
                    IdentityStmt is = (IdentityStmt)s;
                    ParameterRef pr = (ParameterRef)is.getRightOp();
                    if (pr.getIndex() == i)
                        return (Local)is.getLeftOp();
                }
            }
        }

        throw new RuntimeException("couldn't find parameterref!");
    }



    public void printTo(PrintWriter out, int printBodyOptions)
    {
      	
        boolean isPrecise = !PrintJimpleBodyOption.useAbbreviations(printBodyOptions);
        boolean isNumbered = PrintJimpleBodyOption.numbered(printBodyOptions);
        
        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);


	String decl = getMethod().getDeclaration();


        out.println("    " + decl);        
	
	
        out.println("    {");


        // Print out local variables
        {
            Map typeToLocals = new DeterministicHashMap(this.getLocalCount() * 2 + 1, 0.7f);

            // Collect locals
            {
                Iterator localIt = this.getLocals().iterator();

                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();

                    List localList;
 
		    String typeName;
		    Type t = local.getType();
		    if(Jimple.isJavaKeywordType(t))		   
		      typeName = (isPrecise) ? "." + t.toString() : "." + t.toBriefString();
                    else
		      typeName = (isPrecise) ?  t.toString() :  t.toBriefString();

                    if(typeToLocals.containsKey(typeName))
                        localList = (List) typeToLocals.get(typeName);
                    else
                    {
                        localList = new ArrayList();
                        typeToLocals.put(typeName, localList);
                    }

                    localList.add(local);
                }
            }

            // Print locals
            {
                Iterator typeIt = typeToLocals.keySet().iterator();

                while(typeIt.hasNext())
                {
                    String type = (String) typeIt.next();

                    List localList = (List) typeToLocals.get(type);
                    Object[] locals = localList.toArray();
                    if (soot.Main.isVerbose)
                        System.out.println("type: " + type);
                    out.print("        "  + type + " ");
		    
                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println(";");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
	printStatementsInBody(out, isPrecise, isNumbered);

	
        out.println("    }");
    }
    

    void printStatementsInBody(java.io.PrintWriter out, boolean isPrecise, boolean isNumbered)
    {

        Map stmtToName = new HashMap(unitChain.size() * 2 + 1, 0.7f);
        soot.toolkits.graph.UnitGraph unitGraph = new soot.toolkits.graph.BriefUnitGraph(this);


        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

            Set labelStmts = new HashSet();

            // Build labelStmts
            {
                if(!isNumbered)
                    while(boxIt.hasNext())
                    {
                        UnitBox box = (UnitBox) boxIt.next();
                        Unit stmt = (Unit) box.getUnit();
    
                        labelStmts.add(stmt);
                    }
                else
                    labelStmts.addAll(unitChain);

            }

            // Traverse the stmts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator stmtIt = unitChain.iterator();
		
		
                while(stmtIt.hasNext())
                {
                    Unit s = (Unit) stmtIt.next();

                    if(labelStmts.contains(s))
                    {
                        if(isNumbered)
                            stmtToName.put(s, new Integer(labelCount++).toString());
                        else
                            stmtToName.put(s, "label" + (labelCount++));
                    }
                }
            }
        }	


        
        Iterator unitIt = unitChain.iterator();
        Unit currentStmt = null, previousStmt;
        String indent = (isNumbered) ? "    " : "        ";
        
        while(unitIt.hasNext()) {
            
            previousStmt = currentStmt;
            currentStmt = (Unit) unitIt.next();
            
            // Print appropriate header.
                if(isNumbered)
                    out.print("  " + stmtToName.get(currentStmt) + ":");
                else            
                {
                    // Put an empty line if the previous node was a branch node, the current node is a join node
                    //   or the previous statement does not have this statement as a successor, or if
                    //   this statement has a label on it

		    if(currentStmt != unitChain.getFirst()) 
                        {       
                            if(unitGraph.getSuccsOf(previousStmt).size() != 1 ||
                               unitGraph.getPredsOf(currentStmt).size() != 1 ||
                               stmtToName.containsKey(currentStmt))
                                out.println();
                            else {
                                // Or if the previous node does not have this statement as a successor.
                                
                                List succs = unitGraph.getSuccsOf(previousStmt);
                                
                                if(succs.get(0) != currentStmt)
                                    out.println();
                            }
                        }
                    
                     if(stmtToName.containsKey(currentStmt))
			 out.println("     " + stmtToName.get(currentStmt) + ":");
                }
                   
              
		if(isPrecise)
		  out.print(currentStmt.toString(stmtToName, indent));
            else
	      out.print(currentStmt.toBriefString(stmtToName, indent));

            out.print(";"); 
            out.println();
        }
	


        // Print out exceptions
        {
            Iterator trapIt = this.getTraps().iterator();

            if(trapIt.hasNext())
                out.println();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                out.println("        .catch " + trap.getException().getName() + " .from " +
                    stmtToName.get(trap.getBeginUnit()) + " .to " + stmtToName.get(trap.getEndUnit()) +
                    " .with " + stmtToName.get(trap.getHandlerUnit()) + ";");
            }
        }

    }
















//      public void printDebugTo(java.io.PrintWriter out)
//      {   
//          StmtBody stmtBody = this; 
//          Chain units = stmtBody.getUnits();
//          Map stmtToName = new HashMap(units.size() * 2 + 1, 0.7f);
//          //CompleteUnitGraph stmtGraph = new CompleteUnitGraph(units);
        
//          //LocalDefs localDefs = new SimpleLocalDefs(stmtGraph);

//          System.out.println("debug output for " + getMethod().getSignature());
//          /*
//          LocalUses localUses = new LocalUses(stmtGraph, localDefs);
//  */
        
//          //LocalCopies localCopies = new SimpleLocalCopies(stmtGraph);
//          // LiveLocals liveLocals = new SimpleLiveLocals(stmtGraph);
//          //EqualLocals equalLocals = new SimpleEqualLocals(stmtGraph);
        
//          // Create statement name table
//          {
//             int labelCount = 0;

//              Iterator stmtIt = units.iterator();

//              while(stmtIt.hasNext())
//              {
//                  Stmt s = (Stmt) stmtIt.next();

//                  stmtToName.put(s, new Integer(labelCount++).toString());
//              }
//          }

//          Zonation zonation = new Zonation(this);

//          // must re-introduce iterator here.
//          for(int j = 0; j < units.size(); j++)
//          {
//              Stmt s = ((Stmt) stmtList.get(j));

//              out.print("    " + stmtToName.get(s) + ": ");

//              out.print(s.toBriefString(stmtToName, "        "));
//              out.print(";");

//              out.print(zonation.getZoneOf(s));
            
//          /*               
//              // Print info about live locals
//              {
//                  out.print(liveLocals.getLiveLocalsAfter(s));
//              } */
            
//              /*
//              // Print info about local copies
//              {
//                  out.print(localCopies.getCopiesBefore(s));
//              }
//              */
//              /*
//              // Print info about local equalities
//              {
//                  out.print(equalLocals.getCopiesAt(s));
//              }
//  */

//               /*
//               // Print info about uses
//                  if(s instanceof DefinitionStmt)
//                  {
//                      Iterator useIt = localUses.getUsesOf((DefinitionStmt) s).iterator();

//                      out.print("   (");

//                      while(useIt.hasNext())
//                      {
//                          if(k != 0)
//                              out.print(", ");

//                          out.print(stmtToName.get(useIt.next()));
//                      }

//                      out.print(")");
//                  }
//              */
//          /*
//              // Print info about defs
//              {
//                  Iterator boxIt = s.getUseBoxes().iterator();

//                  while(boxIt.hasNext())
//                  {
//                      ValueBox useBox = (ValueBox) boxIt.next();

//                      if(useBox.getValue() instanceof Local)
//                      {
//                          Iterator defIt = localDefs.getDefsOfAt((Local) useBox.getValue(), s).iterator();

//                          out.print("  " + useBox.getValue() + " = {");

//                          while(defIt.hasNext())
//                          {
//                              out.print(stmtToName.get((Stmt) defIt.next()));

//                              if(defIt.hasNext())
//                                  out.print(", ");
//                          }

//                          out.print("}");
//                      }
//                  }
//              } 
//            */
            
//              /*
//              // Print info about successors
//              {
//                  Iterator succIt = stmtGraph.getSuccsOf(s).iterator();

//                  out.print("    [");

//                  if(succIt.hasNext())
//                  {
//                      out.print(stmtToName.get(succIt.next()));

//                      while(succIt.hasNext())
//                      {
//                          Stmt stmt = (Stmt) succIt.next();

//                          out.print(", " + stmtToName.get(stmt));
//                      }
//                  }

//                  out.print("]");
//              }
//                  */
//              /*
//              // Print info about predecessors
//              {
//                  Stmt[] preds = stmtGraph.getPredsOf(s);

//                  out.print("    {");

//                  for(int k = 0; k < preds.length; k++)
//                  {
//                      if(k != 0)
//                          out.print(", ");

//                      out.print(stmtToName.get(preds[k]));
//                  }

//                  out.print("}");
//              }
//              */
//              out.println();
//          }

//          // Print out exceptions
//          {
//              Iterator trapIt = stmtBody.getTraps().iterator();

//              while(trapIt.hasNext())
//              {
//                  Trap trap = (Trap) trapIt.next();

//                  out.println(".catch " + trap.getException().getName() + " from " +
//                      stmtToName.get(trap.getBeginUnit()) + " to " + stmtToName.get(trap.getEndUnit()) +
//                      " with " + stmtToName.get(trap.getHandlerUnit()));
//              }
//          }
//      }
}

