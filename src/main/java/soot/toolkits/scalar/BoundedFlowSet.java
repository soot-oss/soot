package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
 * Represents bounded information for flow analysis. Just like FlowSet, but also provides complementation. Some
 * implementations of BoundedFlowSet may require a FlowUniverse for construction.
 *
 * @see: FlowUniverse
 */
public interface BoundedFlowSet<T> extends FlowSet<T> {
  /**
   * Complements <code>this</code>.
   */
  public void complement();

  /**
   * Complements this BoundedFlowSet, putting the result into <code>dest</code>. <code>dest</code> and <code>this</code> may
   * be the same object.
   */
  public void complement(FlowSet<T> dest);

  /**
   * returns the topped set.
   */
  public FlowSet<T> topSet();
}
