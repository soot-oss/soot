package soot.jimple.spark.geom.dataRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.jimple.spark.pag.Node;
import soot.util.Numberable;

/**
 * The root class for representing context sensitive pointer/object in explicit form.
 * 
 * @author xiao
 *
 */
public abstract class ContextVar implements Numberable {
  // We use spark Node since it can be easily used by clients
  public Node var = null;
  public int id = -1;

  // This class cannot be instantiated directly
  // Use its derived classes
  protected ContextVar() {

  }

  @Override
  public void setNumber(int number) {
    id = number;
  }

  @Override
  public int getNumber() {
    return id;
  }

  /**
   * Test if current context variable contains the information for passed in variable
   * 
   * @param cv
   * @return
   */
  public abstract boolean contains(ContextVar cv);

  /**
   * Merge two context variables if possible Merged information is written into current variable.
   * 
   * @param cv
   * @return true if mergable.
   */
  public abstract boolean merge(ContextVar cv);

  /**
   * Two context sensitive variables have intersected contexts.
   * 
   * @param cv
   * @return
   */
  public abstract boolean intersect(ContextVar cv);
}
