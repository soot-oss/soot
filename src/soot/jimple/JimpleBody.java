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

/** Implementation of the Body class for the Jimple IR. */
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

    /** Clones the current body, making deep copies of the contents. */
    public Object clone()
    {
        Body b = new JimpleBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    /** Applies the transformations corresponding to the given options. */
    public void applyPhaseOptions(Map options) 
    { 
        Options.checkOptions(options, "jb", "no-splitting no-typing "+
                             "aggregate-all-locals no-aggregating "+
                             "use-original-names pack-locals no-cp "+
                             "no-nop-elimination verbatim");

        boolean noSplitting = Options.getBoolean(options, "no-splitting");
        boolean noTyping = Options.getBoolean(options, "no-typing");
        boolean aggregateAllLocals = Options.getBoolean(options, "aggregate-all-locals");
        boolean noAggregating = Options.getBoolean(options, "no-aggregating");
        boolean useOriginalNames = Options.getBoolean(options, "use-original-names");
        boolean usePacking = Options.getBoolean(options, "pack-locals");
        boolean noCopyPropagating = Options.getBoolean(options, "no-cp");
        
        boolean noNopElimination = Options.getBoolean(options, "no-nop-elimination");
        boolean noUcElimination = Options.getBoolean(options, "no-unreachable-code-elimination");

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
		  throw new RuntimeException("type inference failed!");
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

        if (!noUcElimination)
            UnreachableCodeEliminator.v().transform(this, "jb.uce");
                    
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

    /** Inserts usual statements for handling this & parameters into body. */
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

    /** Returns the first non-identity stmt in this body. */
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

    /* Return LHS of the first identity stmt assigning from \@this. */
    public Local getThisLocal()
    {
        Iterator unitsIt = getUnits().iterator();
        while (unitsIt.hasNext())
        {
            Stmt s = (Stmt)unitsIt.next();
            if (s instanceof IdentityStmt && 
                ((IdentityStmt)s).getRightOp() instanceof ThisRef)
                return (Local)(((IdentityStmt)s).getLeftOp());
        }

        throw new RuntimeException("couldn't find identityref!");
    }

    /* Return LHS of the first identity stmt assigning from \@parameter i. */
    public Local getParameterLocal(int i)
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

        throw new RuntimeException("couldn't find parameterref!");
    }

    
}



