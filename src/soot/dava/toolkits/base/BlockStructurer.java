/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Jerome Miecznikowski
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


package soot.dava.toolkits.base;

import soot.*;
import soot.dava.*;
import java.util.*;

public class BlockStructurer extends BodyTransformer
{
    private static BlockStructurer instance = new BlockStructurer();
    private BlockStructurer() {}

    public static BlockStructurer v() { return instance; }

    protected void internalTransform(Body b, String phaseName, Map options)
    {
        DavaBody body = (DavaBody) b;    
        
        if(Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "] Structuring blocks...");
    }       
}

