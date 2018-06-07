package soot.toDex;

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

import java.util.Collections;
import java.util.List;

import soot.Local;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.JimpleValueSwitch;
import soot.util.Switch;

public class TemporaryRegisterLocal implements Local {
  private static final long serialVersionUID = 1L;
  private Type type;

  public TemporaryRegisterLocal(Type regType) {
    setType(regType);
  }

  public Local clone() {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public void toString(UnitPrinter up) {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void apply(Switch sw) {
    ((JimpleValueSwitch) sw).caseLocal(this);
  }

  @Override
  public boolean equivTo(Object o) {
    return this.equals(o);
  }

  @Override
  public int equivHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public void setNumber(int number) {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public int getNumber() {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public String getName() {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void setName(String name) {
    throw new RuntimeException("Not implemented.");
  }

  @Override
  public void setType(Type t) {
    this.type = t;
  }

}
