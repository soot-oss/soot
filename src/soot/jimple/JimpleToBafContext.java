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





package soot.jimple;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;
import java.io.*;

public class JimpleToBafContext
{
    private Map jimpleLocalToBafLocal = new HashMap();
    private BafBody bafBody;
    private Unit mCurrentUnit;

    /**
       An approximation of the local count is required in order to allocate a reasonably sized hash map. 
     */
     
    public JimpleToBafContext(int localCount)
    {
       jimpleLocalToBafLocal = new HashMap(localCount * 2 + 1, 0.7f);
    }


    public void setCurrentUnit(Unit u )
    {
	mCurrentUnit = u;
    }

    public Unit getCurrentUnit()
    {
	return mCurrentUnit;
    }

    
    public Local getBafLocalOfJimpleLocal(Local jimpleLocal)
    {
        return (Local) jimpleLocalToBafLocal.get(jimpleLocal);
    }
    
    public void setBafLocalOfJimpleLocal(Local jimpleLocal, Local bafLocal)
    {
        jimpleLocalToBafLocal.put(jimpleLocal, bafLocal);
    }       
    
    public BafBody getBafBody()
    {
        return bafBody;
    }
    
    public void setBafBody(BafBody bafBody)
    {
        this.bafBody = bafBody;
    }
    
}



