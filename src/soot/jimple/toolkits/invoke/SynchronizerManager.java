/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

package soot.jimple.toolkits.invoke;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class SynchronizerManager
{
    /** Wraps stmt around a monitor associated with local l. 
     * When inlining or static method binding, this is the former
     * base of the invoke expression. */
    public static void synchronizeStmtOn(Stmt stmt, JimpleBody b, Local lock)
    {
        Chain units = b.getUnits();

//          TrapManager.splitTrapsAgainst(b, stmt, (Stmt)units.getSuccOf(stmt));

        units.insertBefore(Jimple.v().newEnterMonitorStmt(lock), stmt);

        Stmt exitMon = Jimple.v().newExitMonitorStmt(lock);
        units.insertAfter(exitMon, stmt);

        // Ok.  That was the easy part.
        // We also need to modify exception blocks to exit the monitor
        //   (they have conveniently been pre-split)
        // Actually, we don't need to do this.
//          {
//              List traps = TrapManager.getTrapsAt(stmt, b);
//              Iterator trapsIt = traps.iterator();

//              while (trapsIt.hasNext())
//              {
//                  Trap t = (Trap)trapsIt.next();

//                  Stmt s = (Stmt)units.getLast();
//                  Stmt newCaughtRef = (Stmt)t.getHandlerUnit().clone();

//                  List l = new ArrayList();

//                  l.add(newCaughtRef);
//                  l.add(exitMon.clone());
//                  l.add(Jimple.v().newGotoStmt((Stmt)units.getSuccOf((Stmt)t.getHandlerUnit())));

//                  units.insertAfter(l, s);
//                  t.setHandlerUnit(newCaughtRef);
//              }
//          }

        // and also we must add a catch Throwable exception block in the appropriate place.
        {
            Stmt newGoto = Jimple.v().newGotoStmt((Stmt)units.getSuccOf(exitMon));
            units.insertAfter(newGoto, exitMon);

            List l = new ArrayList();
            Local eRef = Jimple.v().newLocal("__exception", RefType.v("java.lang.Throwable"));
            b.getLocals().add(eRef);
            Stmt handlerStmt = Jimple.v().newIdentityStmt(eRef, Jimple.v().newCaughtExceptionRef());
            l.add(handlerStmt);
            l.add(exitMon.clone());
            l.add(Jimple.v().newThrowStmt(eRef));
            units.insertAfter(l, newGoto);

            Trap newTrap = Jimple.v().newTrap(Scene.v().getSootClass("java.lang.Throwable"), 
                                              stmt, (Stmt)units.getSuccOf(stmt),
                                              handlerStmt);
            b.getTraps().addFirst(newTrap);
        }
    }
}
