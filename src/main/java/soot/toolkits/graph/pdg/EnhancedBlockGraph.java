/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999-2010 Hossein Sadat-Mohtasham
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
package soot.toolkits.graph.pdg;

import soot.Body;
import soot.toolkits.graph.BlockGraph;


public class EnhancedBlockGraph extends BlockGraph 
{
   
    public  EnhancedBlockGraph(Body body) {
        this(new EnhancedUnitGraph(body));
    }


    public  EnhancedBlockGraph(EnhancedUnitGraph unitGraph) {
        super(unitGraph);

        soot.util.PhaseDumper.v().dumpGraph(this, mBody);
    }
}


