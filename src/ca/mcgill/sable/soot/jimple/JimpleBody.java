/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:
   
 - Modified on October 4, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Errors in type inference now throws an exception (disabled with -debug).
   
 - Modified on September 3, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Sets coffi pointers to null after usage for memory release.
 
 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added code to hande the "noSplitting" global option.

 - Modified on July 29, 1998 by Etienne Gagnon (gagnon@sable.mcgill.ca). (*)
   Added code to hande the "noSplitting" global option.
   
 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Added a constructor for StmtListBody.
   And other misc. changes. 

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.baf.*;

public class JimpleBody implements Body
{
    List locals = new ArrayList();
    SootMethod method;
        
    Local thisLocal;
    StmtList stmtList;
    List traps = new ArrayList();
    
    /** 
     * Builds the JimpleBody for this method from the Baf InstList.
     */
     
    JimpleBody(SootMethod m)
    {
        this((InstBody) Baf.v().getBodyOf(m));
    }
                
    JimpleBody(InstBody instBody)
    {
        super();
        
        this.method = instBody.getMethod();
        this.stmtList = new StmtList(this);
                
        ca.mcgill.sable.soot.coffi.ClassFile coffiClass = instBody.coffiClass;
        ca.mcgill.sable.soot.coffi.method_info coffiMethod = instBody.coffiMethod;
        
        instBody.coffiClass = null;
        instBody.coffiMethod = null;
        
        if(Main.isVerbose)
            System.out.println("[" + method.getName() + "] Jimplifying...");
        
        if(Modifier.isAbstract(method.getModifiers()) || Modifier.isNative(method.getModifiers()))
        {
            return;
        }
        else {
            if(Main.isProfilingOptimization)
                Main.conversionTimer.start();
            
            if(coffiMethod.cfg == null)
            {
                if(Main.isVerbose)
                    System.out.println("[" + method.getName() + 
                        "]     Building Coffi CFG...");
            
                new ca.mcgill.sable.soot.coffi.CFG(coffiMethod);
                    
                if(Main.isVerbose)
                    System.out.println("[" + method.getName() + 
                        "]     Coffi CFG complete.");    
                            
            }
        
            if(Main.isVerbose)
                System.out.println("[" + method.getName() + 
                    "]      Producing naive Jimple...");
                
            coffiMethod.cfg.jimplify(coffiClass.constant_pool, 
                coffiClass.this_class, this);
                
            if(Main.isProfilingOptimization)
            {
                Main.conversionTimer.end();
                Main.conversionLocalCount += getLocalCount();
                Main.conversionStmtCount += stmtList.size();
            }   
            
             if(Main.isVerbose)
                System.out.println("[" + method.getName() + 
                    "]      Naive typeless Jimple produced.");
        }

        // Jimple.printStmtList_debug(this, System.out);

        if(!Main.noCleanUp)
        {
            if(Main.isProfilingOptimization)
                Main.cleanup1Timer.start();
                
            Transformations.cleanupCode(this);
            Transformations.removeUnusedLocals(this);
            
            if(Main.isProfilingOptimization)
            {
                Main.cleanup1Timer.end();
                Main.cleanup1LocalCount += getLocalCount();
                Main.cleanup1StmtCount += stmtList.size();
            }
        }   
        
        if(!Main.noSplitting)
        {
            if(Main.isProfilingOptimization)
                Main.splitTimer.start();
                    
            Transformations.splitLocals(this);
            
            if(Main.isProfilingOptimization)
            {
                Main.splitTimer.end();
                Main.splitLocalCount += getLocalCount();
                Main.splitStmtCount += stmtList.size();
            }
            
            if(!Main.typelessJimple)
            {
                if(Main.isProfilingOptimization)
                    Main.assignTimer.start();
            
                // Jimple.printStmtListBody_debug(this, System.out);
                Transformations.assignTypesToLocals(this);
                
                // Check to see if any locals are untyped
                {
                    Iterator localIt = this.getLocals().iterator();
                    
                    while(localIt.hasNext())
                    {
                        Local l = (Local) localIt.next();
                        
                        if(l.getType().equals(UnknownType.v()) ||
                            l.getType().equals(ErroneousType.v()))
                        {
                            if(!Main.isInDebugMode)
                                throw new RuntimeException("type inference failed!");
                        }
                    }
                }
                
                if(Main.isProfilingOptimization)
                {   
                    Main.assignLocalCount += getLocalCount();
                    Main.assignStmtCount += stmtList.size();
                
                    Main.assignTimer.end();
                }
            }
  
            Transformations.renameLocals(this);
            
            if(!Main.noLocalPacking)
            {
                if(Main.isProfilingOptimization)
                    Main.packTimer.start();
                
                Transformations.packLocals(this);
                Transformations.removeUnusedLocals(this);
                
                if(Main.isProfilingOptimization)
                {
                    Main.packLocalCount += getLocalCount();
                    Main.packStmtCount += stmtList.size();
                
                    Main.packTimer.end();
                }
            }
                
            /*
            if(!Main.noCleanUp) 
            {
                if(Main.isProfilingOptimization)
                    Main.cleanup2Timer.start();
                    
                Transformations.cleanupCode(this);
                Transformations.removeUnusedLocals(this);
                
                if(Main.isProfilingOptimization)
                {
                    Main.cleanup2LocalCount += getLocalCount();
                    Main.cleanup2StmtCount += stmtList.size();
                
                    Main.cleanup2Timer.end();
                }
            }   
            */
            
            Transformations.renameLocals(this);
         }
    }

    public StmtList getStmtList()
    {
        return stmtList;
    }
            
    void redirectJumps(Stmt oldLocation, Stmt newLocation)
    {
        List boxesPointing = oldLocation.getBoxesPointingToThis();
        
        Object[] boxes = boxesPointing.toArray();
            // important to change this to an array to have a static copy
                        
        for(int i = 0; i < boxes.length; i++)
        {
            StmtBox box = (StmtBox) boxes[i];

            if(box.getUnit() != oldLocation)
                throw new RuntimeException("Something weird's happening");
                
            box.setUnit(newLocation);
        }
        
    }
    
    void eliminateBackPointersTo(Stmt oldLocation)
    {
        Iterator boxIt = oldLocation.getUnitBoxes().iterator();
        
        while(boxIt.hasNext())
        {
            StmtBox box = (StmtBox) boxIt.next();
            Stmt stmt = (Stmt) box.getUnit();
            
            stmt.getBoxesPointingToThis().remove(oldLocation);
        }
    }
    
    public int getLocalCount()
    {
        return locals.size();
    }

    /**
     * Returns a backed list of locals.
     */
         
    public List getLocals()
    {
        return locals;
    }
    
    public void addLocal(Local l) throws AlreadyDeclaredException
    {
        locals.add(l);
    }
    
    public void removeLocal(Local l) throws IncorrectDeclarerException
    {
        locals.remove(l);
    }
    
    public Local getLocal(String name) throws ca.mcgill.sable.soot.jimple.NoSuchLocalException
    {
        Iterator localIt = getLocals().iterator();
        
        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();
            
            if(local.name.equals(name))
                return local;
        }        
        
        throw new ca.mcgill.sable.soot.jimple.NoSuchLocalException();
    }

       
    public boolean declaresLocal(String localName)
    {
        Iterator localIt = getLocals().iterator();
        
        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();
            
            if(local.name.equals(localName))
                return true;
        }        
        
        return false;
    }
    
    public SootMethod getMethod()
    {
        return method;
    }
    
    public List getUnitBoxes()
    {
        List stmtBoxes = new ArrayList();
     
        // Put in all statement boxes from the statements   
            Iterator stmtIt = stmtList.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt stmt = (Stmt) stmtIt.next();
                
                Iterator boxIt = stmt.getUnitBoxes().iterator();
                
                while(boxIt.hasNext())
                    stmtBoxes.add(boxIt.next());
            }
        
        // Put in all statement boxes from the trap table
        {
            Iterator trapIt = traps.iterator();
            
            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();
                stmtBoxes.addAll(trap.getUnitBoxes());
            }
        }

        return stmtBoxes;        
    }

    public List getTraps()
    {
        return traps;
    }
    
    public void addTrap(Trap t)
    {
        traps.add(t);
    }
    
    public void removeTrap(Trap t)
    {
        traps.remove(t);
    }
}    
    
