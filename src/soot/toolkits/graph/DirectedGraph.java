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
import soot.*;


/**
 *   Interface to be implemented by classes that support the
 *   notion of a CFG.
 */
public interface DirectedGraph
{
    /** 
     *  @return   A list of the entry points for this CFG.
     */
    public List getHeads();

    /** @return  A list of the exit points for this CFG. */
    public List getTails();

    /** 
     *  @return  A list of the predessor for a node in the graph.
     *  @see Directed
     */
    public List getPredsOf(Directed s);

    /**
     *  @return  A list of the successor for a node in the graph.
     *  @see Directed
     */
    public List getSuccsOf(Directed s);

    /**
     *  @return  The node count for this graph.
     *  @see Directed
     */
    public int size();

    /**
     *  @return An iterator for the nodes in this graph. The ordering
     *          of the nodes returned by the iterator is not determined.
     */
    public Iterator iterator();
}

 
