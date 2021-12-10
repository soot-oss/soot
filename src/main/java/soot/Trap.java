package soot;

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

import java.util.List;

/**
 * A trap (exception catcher), used within Body classes. Intermediate representations must use an implementation of Trap to
 * describe caught exceptions.
 */
public interface Trap extends UnitBoxOwner {

  /**
   * <p>
   * Returns the first trapped unit, unless this <code>Trap</code> does not trap any units at all.
   * </p>
   *
   * <p>
   * If this is a degenerate <code>Trap</code> which traps no units (which can occur if all the units originally trapped by
   * the exception handler have been optimized away), returns an untrapped unit. The returned unit will likely be the first
   * unit remaining after the point where the trapped units were once located, but the only guarantee provided is that for
   * such an empty trap, <code>getBeginUnit()</code> will return the same value as {@link #getEndUnit()}.
   * </p>
   */
  public Unit getBeginUnit();

  /**
   * <p>
   * Returns the unit following the last trapped unit (that is, the first succeeding untrapped unit in the underlying
   * <Code>Chain</code>), unless this <code>Trap</code> does not trap any units at all.
   * </p>
   *
   * <p>
   * In the case of a degenerate <code>Trap</code> which traps no units, returns the same untrapped unit as
   * <code>getBeginUnit()</code>
   * </p>
   *
   * <p>
   * Note that a weakness of marking the end of the trapped region with the first untrapped unit is that Soot has no good
   * mechanism for describing a <code>Trap</code> which traps the last unit in a method.
   * </p>
   */
  public Unit getEndUnit();

  /**
   * Returns the unit handling the exception being trapped.
   */
  public Unit getHandlerUnit();

  /**
   * Returns the box holding the unit returned by {@link #getBeginUnit()}.
   */
  public UnitBox getBeginUnitBox();

  /**
   * Returns the box holding the unit returned by {@link #getEndUnit()}.
   */
  public UnitBox getEndUnitBox();

  /**
   * Returns the box holding the exception handler unit.
   */
  public UnitBox getHandlerUnitBox();

  /**
   * Returns the boxes for first, last, and handler units.
   */
  @Override
  public List<UnitBox> getUnitBoxes();

  /**
   * Returns the exception being caught.
   */
  public SootClass getException();

  /**
   * Sets the value to be returned by {@link #getBeginUnit()} to <code>beginUnit</code>.
   */
  public void setBeginUnit(Unit beginUnit);

  /**
   * Sets the value to be returned by {@link #getEndUnit()} to <code>endUnit</code>.
   */
  public void setEndUnit(Unit endUnit);

  /**
   * Sets the unit handling the exception to <code>handlerUnit</code>.
   */
  public void setHandlerUnit(Unit handlerUnit);

  /**
   * Sets the exception being caught to <code>exception</code>.
   */
  public void setException(SootClass exception);

  /**
   * Performs a shallow clone of this trap.
   */
  public Object clone();
}
