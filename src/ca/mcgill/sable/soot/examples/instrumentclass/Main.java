/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
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

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   First release.
*/

package ca.mcgill.sable.soot.examples.instrumentclass;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.io.*;
import java.util.*;

/**
    InstrumentClass example.
    
    Instruments the given class to print out the number of Jimple goto statements
    executed.
 */

public class Main
{
    public static void main(String[] args)
    {
        Scene cm = Scene.v();
        SootClass sClass = cm.loadClassAndSupport(args[0]);
        boolean printClass = false;
        
        System.out.println("Warning: This code does not work properly!");
        
        // Parse args
            if(args.length != 2)
            {
                System.out.println("Usage: java InstrumentClass <classname> [ --print | --write ]");
                System.exit(0);
            }
            else 
            {
                printClass = args[1].equals("--print");
            }       
        
        // Add gotoCounter field
            SootField gotoCounter = new SootField("gotoCount", LongType.v(), 
                Modifier.STATIC);
            sClass.addField(gotoCounter);
            
        // Add code to increase goto counter each time a goto is encountered
            Iterator methodIt = sClass.getMethods().iterator();
            
            while(methodIt.hasNext())
            {
                SootMethod m = (SootMethod) methodIt.next();
                
                JimpleBody body = new JimpleBody(new ClassFileBody(m));

                m.setActiveBody(body);
                                                
                Local tmpLocal = Jimple.v().newLocal("tmp", LongType.v());
                body.getLocals().add(tmpLocal);
                
                Chain units = body.getUnits();

                List l = new ArrayList();
                l.addAll(units);

                Iterator stmtIt = l.iterator();
                
                while(stmtIt.hasNext())
                {
                    Stmt s = (Stmt) stmtIt.next();
                    
                    if(s instanceof GotoStmt)
                    {
                        AssignStmt toAdd1 = Jimple.v().newAssignStmt(tmpLocal, 
                                Jimple.v().newStaticFieldRef(gotoCounter));
                        AssignStmt toAdd2 = Jimple.v().newAssignStmt(
                                Jimple.v().newStaticFieldRef(gotoCounter), 
                                Jimple.v().newAddExpr(tmpLocal, LongConstant.v(1L)));

                        // insert "tmpLocal = gotoCounter;"
                            units.insertBefore(toAdd1, s);
                        
                        // insert "gotoCounter = tmpLocal + 1L;" 
                            units.insertBefore(toAdd2, s);
                    }
                }
            }
        
        // Add code at the end of the main method to print out the 
        // gotoCounter (this only works in simple cases, because you may have multiple returns or System.exit()'s )
        {
            SootMethod m = sClass.getMethod("void main(java.lang.String[])");
                
            JimpleBody body = (JimpleBody) m.getActiveBody();
            Chain units = body.getUnits();
            
            Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
            body.getLocals().add(tmpRef);
            
            Local tmpLong = Jimple.v().newLocal("tmpLong", LongType.v()); 
            body.getLocals().add(tmpLong);
            
            // insert "tmpRef = java.lang.System.out;"
                units.addLast(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>"))));
            
            // insert "tmpLong = gotoCounter;"
                units.addLast(Jimple.v().newAssignStmt(tmpLong, Jimple.v().newStaticFieldRef(
                    gotoCounter)));
            
            // insert "tmpRef.println(tmpLong);"
            {
                SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: void println(long)>");                    
                units.addLast(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall, tmpLong)));
            }
        }
        
        // Write/Print out the instrumented class
            if(printClass)
                sClass.printTo(new PrintWriter(System.out, true));
            else
                sClass.write();
    }
}
