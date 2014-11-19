/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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





package soot.baf;
import soot.options.*;
import soot.*;
import soot.jimple.*;

import java.util.*;

public class BafBody extends Body
{
    public Object clone()
    {
        Body b = new BafBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    BafBody(SootMethod m)
    {
        super(m);
    }

    public BafBody(Body body, Map<String,String> options)
    {
        super(body.getMethod());

        if(Options.v().verbose())
            G.v().out.println("[" + getMethod().getName() + "] Constructing BafBody...");

        if (!(body instanceof JimpleBody))
            throw new RuntimeException("Can only construct BafBody's directly"
              + " from JimpleBody's.");

        JimpleBody jimpleBody = (JimpleBody) body;
        jimpleBody.validate();
               
        JimpleToBafContext context = new JimpleToBafContext(jimpleBody.getLocalCount());
           
        // Convert all locals
        {
            for (Local l : jimpleBody.getLocals()) {
                Type t = l.getType();
                Local newLocal = Baf.v().newLocal(l.getName(), UnknownType.v());
                
                if(t.equals(DoubleType.v()) || t.equals(LongType.v()))
                    newLocal.setType(DoubleWordType.v());
                else
                    newLocal.setType(WordType.v());
        
                context.setBafLocalOfJimpleLocal(l, newLocal);            
                getLocals().add(newLocal);
            }
        }
    
        Map<Stmt, Unit> stmtToFirstInstruction = new HashMap<Stmt, Unit>();
            
        // Convert all jimple instructions
        {
            for (Unit u : jimpleBody.getUnits()) {
            	Stmt s = (Stmt) u;
                List<Unit> conversionList = new ArrayList<Unit>();

                context.setCurrentUnit(s);
                ((ConvertToBaf) s).convertToBaf(context, conversionList);
               
                stmtToFirstInstruction.put(s, conversionList.get(0));
                getUnits().addAll(conversionList);
            }
        }
        
        // Change all place holders
        {            
            for (UnitBox box : getAllUnitBoxes())
            {                
                if(box.getUnit() instanceof PlaceholderInst)
                {
                    Unit source = ((PlaceholderInst) box.getUnit()).getSource();
                    box.setUnit(stmtToFirstInstruction.get(source));
                }
            }
        }

        // Convert all traps
        {
            for (Trap trap : jimpleBody.getTraps())
            {
                getTraps().add(Baf.v().newTrap(trap.getException(),
                     stmtToFirstInstruction.get(trap.getBeginUnit()),
                     stmtToFirstInstruction.get(trap.getEndUnit()),
                     stmtToFirstInstruction.get(trap.getHandlerUnit())));
            }
        }
        
        PackManager.v().getPack( "bb" ).apply( this );
    }
}
