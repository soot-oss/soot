package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.AbstractJasminClass;
import soot.Type;
import soot.baf.DupInst;
import soot.util.Switch;

public abstract class BDupInst extends AbstractInst implements DupInst {

  @Override
  public int getInCount() {
    return getUnderTypes().size() + getOpTypes().size();
  }

  @Override
  public int getInMachineCount() {
    int count = 0;
    for (Type t : getUnderTypes()) {
      count += AbstractJasminClass.sizeOfType(t);
    }
    for (Type t : getOpTypes()) {
      count += AbstractJasminClass.sizeOfType(t);
    }
    return count;
  }

  @Override
  public int getOutCount() {
    return getUnderTypes().size() + 2 * getOpTypes().size();
  }

  @Override
  public int getOutMachineCount() {
    int count = 0;
    for (Type t : getUnderTypes()) {
      count += AbstractJasminClass.sizeOfType(t);
    }
    for (Type t : getOpTypes()) {
      count += 2 * AbstractJasminClass.sizeOfType(t);
    }
    return count;
  }

  @Override
  public void apply(Switch sw) {
    throw new RuntimeException();
  }
}
