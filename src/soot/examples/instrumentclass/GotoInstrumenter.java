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





package soot.examples.instrumentclass;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.io.*;
import java.util.*;

/**
    InstrumentClass example.
    
    Instruments the given class to print out the number of Jimple goto 
    statements executed.

    To enable this class, enable the given PackAdjuster by compiling it 
    separately, into the soot package.
 */

public class GotoInstrumenter extends BodyTransformer
{
    private static GotoInstrumenter instance = new GotoInstrumenter();
    private GotoInstrumenter() {}

    public static GotoInstrumenter v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions(); }

    private boolean addedPrinterToMainClass = false;

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        SootClass sClass = body.getMethod().getDeclaringClass();
        SootField gotoCounter = null;
        
        // Add code at the end of the main method to print out the 
        // gotoCounter (this only works in simple cases, because you may have multiple returns or System.exit()'s )
        synchronized(this)
        {
            if (!Scene.v().getMainClass().
                    declaresMethod(".void main(java.lang.String[])"))
                throw new RuntimeException("couldn't find main() in mainClass");

            if (addedPrinterToMainClass)
                gotoCounter = Scene.v().getMainClass().getFieldByName("gotoCount");
            else
            {
                // Add gotoCounter field
                gotoCounter = new SootField("gotoCount", LongType.v(), 
                                            Modifier.STATIC);
                Scene.v().getMainClass().addField(gotoCounter);
                
                addedPrinterToMainClass = true;
                
                // Add code to main to print out the counter.
                SootMethod m = Scene.v().getMainClass().getMethod(".void main(java.lang.String[])");
                
                // (that is, if it has an active body.  Hopefully it'll get one, 
                // sooner or later.)
                if (m.hasActiveBody())
                {
                    body = (JimpleBody) m.getActiveBody();
                    Chain units = body.getUnits();
                
                    Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
                    body.getLocals().add(tmpRef);
                    
                    Local tmpLong = Jimple.v().newLocal("tmpLong", LongType.v()); 
                    body.getLocals().add(tmpLong);
                    
                    Unit ret = (Unit) units.getLast();
                    units.removeLast();
                    
                    // insert "tmpRef = java.lang.System.out;"
                    units.addLast(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
                                    Scene.v().getField("<java.lang.System: java.io.PrintStream out>"))));
            
                    // insert "tmpLong = gotoCounter;"
                    units.addLast(Jimple.v().newAssignStmt(tmpLong, Jimple.v().newStaticFieldRef(
                                    gotoCounter)));
                
                    // insert "tmpRef.println(tmpLong);"
                    {
                        SootMethod toCall = Scene.v().getMethod("<java.io.PrintStream: .void println(.long)>");                    
                        units.addLast(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall, tmpLong)));
                    }
                
                    units.addLast(ret);
                }
            }
        }
            
        // Add code to increase goto counter each time a goto is encountered
        {
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
                    AssignStmt toAdd2 = Jimple.v().newAssignStmt(tmpLocal,
                                 Jimple.v().newAddExpr(tmpLocal, LongConstant.v(1L)));
                    AssignStmt toAdd3 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(gotoCounter), 
                                                                 tmpLocal);

                    // insert "tmpLocal = gotoCounter;"
                    units.insertBefore(toAdd1, s);
                        
                    // insert "tmpLocal = tmpLocal + 1L;" 
                    units.insertBefore(toAdd2, s);

                    // insert "gotoCounter = tmpLocal;" 
                    units.insertBefore(toAdd3, s);
                }
            }
        }
    }
}
