/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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


package soot.toolkits.graph;

import java.util.*;
import java.io.*;
import soot.*;


/**
 *  A CFG where the nodes are Block instances, and
 *  where exception boundries are taken into account when
 *  finding the Blocks for the provided Body.
 *  That is any unit which is the first unit to be convered by
 *  some exception handler, will start a new Block, and any unit which
 *  is the last unit to be covered a some exception handler, will end
 *  the block it is part of.
 *
 *  @see Unit
 *  @see Block
 *  @see BlockGraph
 *  @see CompleteBlockGraph
 */

public class ZonedBlockGraph extends BlockGraph 
{
    
    /**
     *   Constructs  a graph of blocks found by partitioning the
     *   enclosing Body instance's chain of units into discrete Blocks.
     *
     *   @param body               The underlying body we want to make a
     *                             graph of Block for.
     */
    public  ZonedBlockGraph(Body body)
    {
        super(body, ZONED);
    }
}

