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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.DoubleType;
import soot.G;
import soot.Local;
import soot.LongType;
import soot.PackManager;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnknownType;
import soot.baf.internal.BafLocal;
import soot.jimple.ConvertToBaf;
import soot.jimple.JimpleBody;
import soot.jimple.JimpleToBafContext;
import soot.jimple.Stmt;
import soot.options.Options;

public class BafBody extends Body
{
    private JimpleToBafContext jimpleToBafContext;

    public JimpleToBafContext getContext() {
    	return jimpleToBafContext;
    }
    
	@Override
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
        this.jimpleToBafContext = context;
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
                
                //We cannot use the context for the purpose of saving the old Jimple locals, because
                //some transformers in the bb-pack, which is called at the end of the method
                //copy the locals, thus invalidating the information in a map.
                ((BafLocal) newLocal).setOriginalLocal(l); 
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
