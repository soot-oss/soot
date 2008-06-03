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






package soot.toolkits.scalar;

import soot.*;
import java.util.*;




/**
 *   Provides an interface for querying for the definitions of a Local
 *   at a given Unit in a method.
 */
public interface LocalDefs
{
    /**
     *   Returns the definition sites for a Local at a certain
     *   point (Unit) in a method. 
     *
     *   @param l the Local in question.
     *   @param s  a unit that specifies the method context (location) 
     *             to query for the definitions of the Local. 
     *   @return a list of Units where the local is defined in the current
     *            method context.         
     */
    public List<Unit> getDefsOfAt(Local l, Unit s);
}

