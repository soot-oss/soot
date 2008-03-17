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

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** 
   Example to instrument a classfile to produce goto counts. 
 */
public class MyMain
{    
    public static void main(String[] args) 
    {
        if(args.length == 0)
        {
            System.out.println("Syntax: MyMain [soot options]");
            System.exit(0);
        }            
        
        PackManager.v().getPack("jtp").add(new Transform("jtp.instrumenter", GotoInstrumenter.v()));

        // Just in case, resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);
        soot.Main.main(args);
    }
}

/**
    InstrumentClass example.
    
    Instruments the given class to print out the number of Jimple goto 
    statements executed.

    To enable this class, enable the given PackAdjuster by compiling it 
    separately, into the soot package.
 */

class GotoInstrumenter extends BodyTransformer
{
    private static GotoInstrumenter instance = new GotoInstrumenter();
    private GotoInstrumenter() {}

    public static GotoInstrumenter v() { return instance; }

    private boolean addedFieldToMainClassAndLoadedPrintStream = false;
    private SootClass javaIoPrintStream;

    private Local addTmpRef(Body body)
    {
        Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
        body.getLocals().add(tmpRef);
        return tmpRef;
    }
     
    private Local addTmpLong(Body body)
    {
        Local tmpLong = Jimple.v().newLocal("tmpLong", LongType.v()); 
        body.getLocals().add(tmpLong);
        return tmpLong;
    }

    private void addStmtsToBefore(Chain units, Stmt s, SootField gotoCounter, Local tmpRef, Local tmpLong)
    {
        // insert "tmpRef = java.lang.System.out;" 
        units.insertBefore(Jimple.v().newAssignStmt( 
                      tmpRef, Jimple.v().newStaticFieldRef( 
                      Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), s);

        // insert "tmpLong = gotoCounter;" 
        units.insertBefore(Jimple.v().newAssignStmt(tmpLong, 
                      Jimple.v().newStaticFieldRef(gotoCounter.makeRef())), s);
            
        // insert "tmpRef.println(tmpLong);" 
        SootMethod toCall = javaIoPrintStream.getMethod("void println(long)");                    
        units.insertBefore(Jimple.v().newInvokeStmt(
                      Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpLong)), s);
    }

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        SootClass sClass = body.getMethod().getDeclaringClass();
        SootField gotoCounter = null;
        boolean addedLocals = false;
        Local tmpRef = null, tmpLong = null;
        Chain units = body.getUnits();
        
        // Add code at the end of the main method to print out the 
        // gotoCounter (this only works in simple cases, because you may have multiple returns or System.exit()'s )
        synchronized(this)
        {
            if (!Scene.v().getMainClass().
                    declaresMethod("void main(java.lang.String[])"))
                throw new RuntimeException("couldn't find main() in mainClass");

            if (addedFieldToMainClassAndLoadedPrintStream)
                gotoCounter = Scene.v().getMainClass().getFieldByName("gotoCount");
            else
            {
                // Add gotoCounter field
                gotoCounter = new SootField("gotoCount", LongType.v(), 
                                                Modifier.STATIC);
                Scene.v().getMainClass().addField(gotoCounter);

                javaIoPrintStream = Scene.v().getSootClass("java.io.PrintStream");

                addedFieldToMainClassAndLoadedPrintStream = true;
            }
        }
            
        // Add code to increase goto counter each time a goto is encountered
        {
            boolean isMainMethod = body.getMethod().getSubSignature().equals("void main(java.lang.String[])");

            Local tmpLocal = Jimple.v().newLocal("tmp", LongType.v());
            body.getLocals().add(tmpLocal);
                
            Iterator stmtIt = units.snapshotIterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();

                if(s instanceof GotoStmt)
                {
                    AssignStmt toAdd1 = Jimple.v().newAssignStmt(tmpLocal, 
                                 Jimple.v().newStaticFieldRef(gotoCounter.makeRef()));
                    AssignStmt toAdd2 = Jimple.v().newAssignStmt(tmpLocal,
                                 Jimple.v().newAddExpr(tmpLocal, LongConstant.v(1L)));
                    AssignStmt toAdd3 = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(gotoCounter.makeRef()), 
                                                                 tmpLocal);

                    // insert "tmpLocal = gotoCounter;"
                    units.insertBefore(toAdd1, s);
                        
                    // insert "tmpLocal = tmpLocal + 1L;" 
                    units.insertBefore(toAdd2, s);

                    // insert "gotoCounter = tmpLocal;" 
                    units.insertBefore(toAdd3, s);
                }
                else if (s instanceof InvokeStmt)
                {
                    InvokeExpr iexpr = (InvokeExpr) ((InvokeStmt)s).getInvokeExpr();
                    if (iexpr instanceof StaticInvokeExpr)
                    {
                        SootMethod target = ((StaticInvokeExpr)iexpr).getMethod();
                        
                        if (target.getSignature().equals("<java.lang.System: void exit(int)>"))
                        {
                            if (!addedLocals)
                            {
                                tmpRef = addTmpRef(body); tmpLong = addTmpLong(body);
                                addedLocals = true;
                            }
                            addStmtsToBefore(units, s, gotoCounter, tmpRef, tmpLong);
                        }
                    }
                }
                else if (isMainMethod && (s instanceof ReturnStmt || s instanceof ReturnVoidStmt))
                {
                    if (!addedLocals)
                    {
                        tmpRef = addTmpRef(body); tmpLong = addTmpLong(body);
                        addedLocals = true;
                    }
                    addStmtsToBefore(units, s, gotoCounter, tmpRef, tmpLong);
                }
            }
        }
    }
}