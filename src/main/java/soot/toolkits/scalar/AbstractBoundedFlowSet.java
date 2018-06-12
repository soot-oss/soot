package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

/**
 * provides functional code for most of the methods. Subclasses are invited to provide a more efficient version. Most often
 * this will be done in the following way:<br>
 *
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */

public abstract class AbstractBoundedFlowSet<T> extends AbstractFlowSet<T> implements BoundedFlowSet<T> {

  public void complement() {
    complement(this);
  }

  public void complement(FlowSet<T> dest) {
    if (this == dest) {
      complement();
    } else {
      BoundedFlowSet<T> tmp = (BoundedFlowSet<T>) topSet();
      tmp.difference(this, dest);
    }
  }

  public FlowSet<T> topSet() {
    BoundedFlowSet<T> tmp = (BoundedFlowSet<T>) emptySet();
    tmp.complement();
    return tmp;
  }
}
