/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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


package soot.toolkits.graph;

import java.util.*;
import java.io.*;
import soot.*;


/**
 *  Represents a CFG for a Body instance where the nodes are Block 
 *  instances, and where control flow associated with exceptions is ignored.
 *  Hence the graph will in fact be a forest where each exception handler will
 *  constitute a disjoint subgraph.
 */
public class ArrayRefBlockGraph extends BlockGraph 
{
    /**
     *  Constructs a ArrayRefBlockGraph from a given Body instance.
     *  @param the Body instance from which the graph is built.
     */
    public  ArrayRefBlockGraph(Body body)
    {
        super(body, ARRAYREF);
    }
}


