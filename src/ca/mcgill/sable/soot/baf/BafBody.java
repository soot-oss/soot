/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
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

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;

public class BafBody implements Body
{
    SootMethod method;
    List locals = new ArrayList();
    List instList;
    List traps = new ArrayList();

    public BafBody(Body body)
    {
        JimpleBody jimpleBody;
        SootMethod method = body.getMethod();
           
        if(body instanceof JimpleBody)
            jimpleBody = (JimpleBody) body;
        else
            throw new RuntimeException("Can only construct BafBody's directly"
              + " from JimpleBody's.");
       
        this.method = method;
        instList = new ArrayList();
     
        JimpleToBafContext context = new JimpleToBafContext(jimpleBody.getLocalCount());
           
        // Convert all locals
        {
            Iterator localIt = jimpleBody.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Type t = l.getType();
                Local newLocal;
                
                newLocal = Baf.v().newLocal(l.getName(), UnknownType.v());
                
                if(t.equals(DoubleType.v()) || t.equals(LongType.v()))
                    newLocal.setType(DoubleWordType.v());
                else
                    newLocal.setType(WordType.v());
        
                context.setBafLocalOfJimpleLocal(l, newLocal);            
                addLocal(newLocal);
            }
        }
    
        Map stmtToFirstInstruction = new HashMap();
            
        // Convert all jimple instructions
        {
            Iterator stmtIt = jimpleBody.getStmtList().iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                List conversionList = new ArrayList();

                System.out.println("converting: " + s);                
                ((ConvertToBaf) s).convertToBaf(context, conversionList);
                
                System.out.println("become: " + conversionList.get(0));
                
                stmtToFirstInstruction.put(s, conversionList.get(0));
                instList.addAll(conversionList);
            }
        }
        
        // Change all place holders
        {
            Iterator boxIt = getUnitBoxes().iterator();
            
            while(boxIt.hasNext())
            {
                UnitBox box = (UnitBox) boxIt.next();
                
                if(box.getUnit() instanceof PlaceholderInst)
                {
                    Unit source = ((PlaceholderInst) box.getUnit()).getSource();
                    box.setUnit((Unit) stmtToFirstInstruction.get(source));
                }
            }
        }
    }

    public SootMethod getMethod()
    {
        return method;
    }

    public void printTo(PrintWriter out)
    {
        printTo(out, 0);
    }

    public List getUnitList()
    {
        return instList;
    }
    
    public void printTo(PrintWriter out, int printBodyOptions)
    {
        boolean isPrecise = !PrintBafBodyOption.useAbbreviations(printBodyOptions);
                /*
        if(PrintJimpleBodyOption.debugMode(printBodyOptions))
        {
            print_debug(out);
            return;
        }
        */
        
        //System.out.println("Constructing the graph of " + getName() + "...");

        List instList = this.getUnitList();

        Map instToName = new HashMap(instList.size() * 2 + 1, 0.7f);

        out.println("    " + method.getDeclaration());        
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
 
                    String typeName = (isPrecise) ? local.getType().toString() : local.getType().toBriefString();
                    
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

                    out.print("        " + type + " ");

                    for(int k = 0; k < locals.length; k++)
                    {
                        if(k != 0)
                            out.print(", ");

                        out.print(((Local) locals[k]).getName());
                    }

                    out.println("");
                }
            }


            if(!typeToLocals.isEmpty())
                out.println();
        }

        // Print out statements
            printStatementsInBody(out, isPrecise);

        out.println("    }");
    }

    void printStatementsInBody(java.io.PrintWriter out, boolean isPrecise)
    {
        List instList = this.getUnitList();

        Map instToName = new HashMap(instList.size() * 2 + 1, 0.7f);

        // Create statement name table
        {
            Iterator boxIt = this.getUnitBoxes().iterator();

            Set labelInsts = new HashSet();

            // Build labelInsts
            {
                while(boxIt.hasNext())
                {
                    UnitBox box = (UnitBox) boxIt.next();
                    Inst inst = (Inst) box.getUnit();

                    labelInsts.add(inst);
                }
            }

            // Traverse the insts and assign a label if necessary
            {
                int labelCount = 0;

                Iterator instIt = instList.iterator();

                while(instIt.hasNext())
                {
                    Inst s = (Inst) instIt.next();

                    if(labelInsts.contains(s))
                        instToName.put(s, "label" + (labelCount++));
                }
            }
        }

        for(int j = 0; j < instList.size(); j++)
        {
            Inst s = ((Inst) instList.get(j));

            // Put an empty line if the previous node was a branch node, the current node is a join node
            //   or the previous statement does not have this statement as a successor, or if
            //   this statement has a label on it
            {
                if(j != 0)
                {
                    Inst previousInst = (Inst) instList.get(j - 1);

                    if(instToName.containsKey(s))
                        out.println();
                }
            }

            if(instToName.containsKey(s))
                out.println("     " + instToName.get(s) + ":");

            if(isPrecise)
                out.print(s.toString(instToName, "        "));
            else
                out.print(s.toBriefString(instToName, "        "));

            out.print("");
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

                out.println("        catch '" + trap.getException().getName() + "' from " +
                    instToName.get(trap.getBeginUnit()) + " to " + instToName.get(trap.getEndUnit()) +
                    " with " + instToName.get(trap.getHandlerUnit()) + ";");
            }
        }
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

            if(local.getName().equals(name))
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

            if(local.getName().equals(localName))
                return true;
        }

        return false;
    }
    
    public List getUnitBoxes()
    {
        List instBoxes = new ArrayList();

        // Put in all statement boxes from the statements
            Iterator instIt = instList.iterator();

            while(instIt.hasNext())
            {
                Inst inst = (Inst) instIt.next();

                Iterator boxIt = inst.getUnitBoxes().iterator();

                while(boxIt.hasNext())
                    instBoxes.add(boxIt.next());
            }

        // Put in all statement boxes from the trap table
        {
            Iterator trapIt = traps.iterator();

            while(trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();
                instBoxes.addAll(trap.getUnitBoxes());
            }
        }

        return instBoxes;
    }

}








