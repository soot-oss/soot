/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;
import soot.jimple.toolkits.scalar.*;

public class ThisInliner extends BodyTransformer{

    public void internalTransform(Body b, String phaseName, Map options){
        
        // assure body is a constructor
        if (!b.getMethod().getName().equals("<init>")) return;

        // if the first invoke is a this() and not a super() inline the this()
        InvokeStmt invokeStmt = getFirstSpecialInvoke(b);
        if (invokeStmt == null) return; 
        SpecialInvokeExpr specInvokeExpr = (SpecialInvokeExpr)invokeStmt.getInvokeExpr();
        if (specInvokeExpr.getMethod().getDeclaringClass().equals(b.getMethod().getDeclaringClass())){
            
            // put locals from inlinee into container
            if (!specInvokeExpr.getMethod().hasActiveBody()){
                specInvokeExpr.getMethod().retrieveActiveBody();
            }

            HashMap oldLocalsToNew = new HashMap();
            
            Iterator localsIt = specInvokeExpr.getMethod().getActiveBody().getLocals().iterator();
            while (localsIt.hasNext()){
                Local l = (Local)localsIt.next();
                Local newLocal = (Local)l.clone();
                b.getLocals().add(newLocal);
                oldLocalsToNew.put(l, newLocal);
            }
           
            //find identity stmt of original method
            IdentityStmt origIdStmt = findIdentityStmt(b);
           
            HashMap oldStmtsToNew = new HashMap();
            
            //System.out.println("locals: "+b.getLocals());
            Chain containerUnits = b.getUnits();
            Iterator inlineeIt = specInvokeExpr.getMethod().getActiveBody().getUnits().iterator();
            while (inlineeIt.hasNext()){
                Stmt inlineeStmt = (Stmt)inlineeIt.next();
               
                // handle identity stmts
                if (inlineeStmt instanceof IdentityStmt){
                    IdentityStmt idStmt = (IdentityStmt)inlineeStmt;
                    
                    if (idStmt.getRightOp() instanceof ThisRef) {
                        Stmt newThis = Jimple.v().newAssignStmt((Local)oldLocalsToNew.get(idStmt.getLeftOp()), origIdStmt.getLeftOp());         
                        containerUnits.insertBefore(newThis, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newThis);
                    }
                    
                    else if (idStmt.getRightOp() instanceof CaughtExceptionRef){
                        Stmt newInlinee = (Stmt)inlineeStmt.clone();
                        Iterator localsToPatch = newInlinee.getUseAndDefBoxes().iterator();
                        while (localsToPatch.hasNext()){
                            ValueBox next = (ValueBox)localsToPatch.next();
                            if (next.getValue() instanceof Local){
                                next.setValue((Local)oldLocalsToNew.get(next.getValue()));
                            }
                        }
                    
                       
                        containerUnits.insertBefore(newInlinee, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newInlinee);
                    }
                    else if (idStmt.getRightOp() instanceof ParameterRef) {
                        Stmt newParam = Jimple.v().newAssignStmt((Local)oldLocalsToNew.get(idStmt.getLeftOp()), specInvokeExpr.getArg(((ParameterRef)idStmt.getRightOp()).getIndex()));         
                        containerUnits.insertBefore(newParam, invokeStmt);
                        oldStmtsToNew.put(inlineeStmt, newParam);
                    }
                }

                // handle return void stmts (cannot return anything else 
                // from a constructor)
                else if (inlineeStmt instanceof ReturnVoidStmt){
                    Stmt newRet = Jimple.v().newGotoStmt((Stmt)containerUnits.getSuccOf(invokeStmt));
                    containerUnits.insertBefore(newRet, invokeStmt);
                    System.out.println("adding to stmt map: "+inlineeStmt+" and "+newRet);
                    oldStmtsToNew.put(inlineeStmt, newRet);
                }

                else {
                    Stmt newInlinee = (Stmt)inlineeStmt.clone();
                    Iterator localsToPatch = newInlinee.getUseAndDefBoxes().iterator();
                    while (localsToPatch.hasNext()){
                        ValueBox next = (ValueBox)localsToPatch.next();
                        if (next.getValue() instanceof Local){
                            next.setValue((Local)oldLocalsToNew.get(next.getValue()));
                        }
                    }

                       
                    containerUnits.insertBefore(newInlinee, invokeStmt);
                    oldStmtsToNew.put(inlineeStmt, newInlinee);
                }
                
            }
                
            // handleTraps
            Iterator trapsIt = specInvokeExpr.getMethod().getActiveBody().getTraps().iterator();
            while (trapsIt.hasNext()){
                Trap t = (Trap)trapsIt.next();
                System.out.println("begin: "+t.getBeginUnit());
                Stmt newBegin = (Stmt)oldStmtsToNew.get(t.getBeginUnit());
                System.out.println("end: "+t.getEndUnit());
                Stmt newEnd = (Stmt)oldStmtsToNew.get(t.getEndUnit());
                System.out.println("handler: "+t.getHandlerUnit());
                Stmt newHandler = (Stmt)oldStmtsToNew.get(t.getHandlerUnit());

                if (newBegin == null || newEnd == null || newHandler == null)
                    throw new RuntimeException("couldn't map trap!");

                b.getTraps().add(Jimple.v().newTrap(t.getException(), newBegin, newEnd, newHandler));
            }

            // patch gotos
            inlineeIt = specInvokeExpr.getMethod().getActiveBody().getUnits().iterator();
            while (inlineeIt.hasNext()){
                Stmt inlineeStmt = (Stmt)inlineeIt.next();
                if (inlineeStmt instanceof GotoStmt){
                    System.out.println("inlinee goto target: "+((GotoStmt)inlineeStmt).getTarget());
                    ((GotoStmt)oldStmtsToNew.get(inlineeStmt)).setTarget((Stmt)oldStmtsToNew.get(((GotoStmt)inlineeStmt).getTarget()));
                }
                       
            }
                
            // remove original invoke
            containerUnits.remove(invokeStmt);
               
            // resolve name collisions
            LocalNameStandardizer.v().transform(b, "ji.lns");

            
        }
        //System.out.println("locals: "+b.getLocals());
        //System.out.println("units: "+b.getUnits());
    }

    private InvokeStmt getFirstSpecialInvoke(Body b){
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            if (!(s instanceof InvokeStmt)) continue;

            InvokeExpr invokeExpr = ((InvokeStmt)s).getInvokeExpr();
            if (!(invokeExpr instanceof SpecialInvokeExpr)) continue;
    
            return (InvokeStmt)s;        
        }
        // but there will always be either a call to this() or to super()
        // from the constructor
        return null;
    }

    private IdentityStmt findIdentityStmt(Body b){
        Iterator it = b.getUnits().iterator();
        while (it.hasNext()){
            Stmt s = (Stmt)it.next();
            if ((s instanceof IdentityStmt) && (((IdentityStmt)s).getRightOp() instanceof ThisRef)){
                return (IdentityStmt)s;
            }
        }
        return null;
    }
}
