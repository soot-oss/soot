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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Partial implementation of trap (exception catcher), used within Body classes.
 */
@SuppressWarnings("serial")
public class AbstractTrap implements Trap, Serializable {

  /**
   * The exception being caught.
   */
  protected transient SootClass exception;

  /**
   * The first unit being trapped.
   */
  protected UnitBox beginUnitBox;

  /**
   * The unit just before the last unit being trapped.
   */
  protected UnitBox endUnitBox;

  /**
   * The unit to which execution flows after the caught exception is triggered.
   */
  protected UnitBox handlerUnitBox;

  /**
   * The list of UnitBoxes referred to in this Trap (begin, end, and handler).
   */
  protected List<UnitBox> unitBoxes;

  /**
   * Creates an AbstractTrap with the given exception, handler, begin, and end units.
   */
  protected AbstractTrap(SootClass exception, UnitBox beginUnitBox, UnitBox endUnitBox, UnitBox handlerUnitBox) {
    this.exception = exception;
    this.beginUnitBox = beginUnitBox;
    this.endUnitBox = endUnitBox;
    this.handlerUnitBox = handlerUnitBox;
    this.unitBoxes = Collections.unmodifiableList(Arrays.asList(beginUnitBox, endUnitBox, handlerUnitBox));
  }

  @Override
  public Unit getBeginUnit() {
    return beginUnitBox.getUnit();
  }

  @Override
  public Unit getEndUnit() {
    return endUnitBox.getUnit();
  }

  @Override
  public Unit getHandlerUnit() {
    return handlerUnitBox.getUnit();
  }

  @Override
  public UnitBox getHandlerUnitBox() {
    return handlerUnitBox;
  }

  @Override
  public UnitBox getBeginUnitBox() {
    return beginUnitBox;
  }

  @Override
  public UnitBox getEndUnitBox() {
    return endUnitBox;
  }

  @Override
  public List<UnitBox> getUnitBoxes() {
    return unitBoxes;
  }

  @Override
  public void clearUnitBoxes() {
    for (UnitBox box : getUnitBoxes()) {
      box.setUnit(null);
    }
  }

  @Override
  public SootClass getException() {
    return exception;
  }

  @Override
  public void setBeginUnit(Unit beginUnit) {
    beginUnitBox.setUnit(beginUnit);
  }

  @Override
  public void setEndUnit(Unit endUnit) {
    endUnitBox.setUnit(endUnit);
  }

  @Override
  public void setHandlerUnit(Unit handlerUnit) {
    handlerUnitBox.setUnit(handlerUnit);
  }

  @Override
  public void setException(SootClass exception) {
    this.exception = exception;
  }

  @Override
  public Object clone() {
    throw new RuntimeException();
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    exception = Scene.v().getSootClass((String) in.readObject());
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(exception.getName());
  }
}
