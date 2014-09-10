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

/** 
 * Represents bounded information for flow analysis.  
 * Just like FlowSet, but also provides complementation.
 * Some implementations of BoundedFlowSet may require a FlowUniverse for construction.
 *
 * @see: FlowUniverse
 */
public interface BoundedFlowSet<T> extends FlowSet<T>
{
    /**
     * Complements <code>this</code>.
     */
    public void complement();

    /** 
     * Complements this BoundedFlowSet, putting the result into
     * <code>dest</code>. <code>dest</code> and <code>this</code> may be the
     * same object.
     */
    public void complement(FlowSet<T> dest);

    /**
     * returns the topped set.
     */
    public FlowSet<T> topSet();

    /** Returns elements [low..high] of this BoundedFlowSet. (optional
     * operation) */
  /*
    public List toList(int low, int high) throws UnsupportedOperationException;
  */
}
