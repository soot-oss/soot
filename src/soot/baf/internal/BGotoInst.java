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





package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BGotoInst extends AbstractBranchInst implements GotoInst
{
    public BGotoInst(Unit target)
    {
        super(Baf.v().newInstBox(target));
    }
    
  

    public Object clone()
    {
        return new BGotoInst(getTarget());
    }

    public int getInMachineCount()
    {
        return 0;
    }

    public boolean branches()
    {
        return true;
    }
    
    public int getInCount()
    {
        return 0;
    }
    
    public int getOutCount()
    {
        return 0;
    }

    public int getOutMachineCount()
    {
        return 0;
    }

    public String getName() { return "goto"; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseGotoInst(this);
    }    
   
    public boolean fallsThrough()
    {
        return false;
    }
}

