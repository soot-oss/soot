/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
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


package soot.baf.toolkits.base;

import soot.*;




/**
 *   Interface to be implemented by peepholes acting on the Baf IR. 
 *
 *   @see PeepholeOptimizer
 *   @see ExamplePeephole
 */

public interface Peephole
{
    /**
     *   Entry point for a peephole. This method is 
     *   repeatly called by the peephole driver, until
     *   a fixed-point is reached over all peepholes.
     *
     *   @param b  Body to apply peephole to.
     *   @return   true if the peephole changed in any way the Body it
     *             acted on. false otherwise.
     */
    boolean apply(Body b);
}



