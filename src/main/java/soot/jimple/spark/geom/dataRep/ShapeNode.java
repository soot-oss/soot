package soot.jimple.spark.geom.dataRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2011 Richard Xiao
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

import java.io.PrintStream;

/**
 * It is the the abstract super type of geometric figures and the manager of all the generated sub-type figures. It is not
 * used currently and we leave it here as an extensible point in future.
 *
 * @author xiao
 *
 */
public abstract class ShapeNode {
  // Common Instance Fields
  // I1 : the starting x coordinate
  // I2 : the starting y coordinate
  // E1: the end coordinate of the X or Y axis depending on the value of I1 and I2 (I1 != 0, then E1 is associated with I1)
  public long I1;
  public long I2;
  public long E1;
  public boolean is_new;
  public ShapeNode next;

  public ShapeNode() {
    is_new = true;
    next = null;
  }

  /**
   * Clone itself and make a new instance.
   *
   * @return
   */
  public abstract ShapeNode makeDuplicate();

  /**
   * Test if the invoked figure contains the passed in figure
   *
   * @param other
   * @return
   */
  public abstract boolean inclusionTest(ShapeNode other);

  /**
   * Test if the input x parameter falls in the range of the X coordinates of this figure
   *
   * @param x
   * @return
   */
  public abstract boolean coverThisXValue(long x);

  public abstract void printSelf(PrintStream outPrintStream);

  public abstract void copy(ShapeNode other);
}
