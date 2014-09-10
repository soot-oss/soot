/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
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
 * provides functional code for most of the methods. Subclasses are invited to
 * provide a more efficient version. Most often this will be done in the
 * following way:<br>
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */

public abstract class AbstractBoundedFlowSet<T> extends AbstractFlowSet<T> implements
                                                               BoundedFlowSet<T> {
  
  public void complement() {
    complement(this);
  }

  public void complement(FlowSet<T> dest) {
    if (this == dest)
      complement();
    else {
      BoundedFlowSet<T> tmp = (BoundedFlowSet<T>)topSet();
      tmp.difference(this, dest);
    }
  }

  public FlowSet<T> topSet() {
    BoundedFlowSet<T> tmp = (BoundedFlowSet<T>)emptySet();
    tmp.complement();
    return tmp;
  }
}
