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

import java.util.Iterator;

import soot.AbstractJasminClass;
import soot.Type;
import soot.baf.DupInst;
import soot.util.Switch;

public abstract class BDupInst extends AbstractInst implements DupInst {

  public int getInCount() {
    return getUnderTypes().size() + getOpTypes().size();
  }

  public int getInMachineCount() {
    int count = 0;

    Iterator<Type> underTypesIt = getUnderTypes().iterator();
    while (underTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(underTypesIt.next());
    }

    Iterator<Type> opTypesIt = getOpTypes().iterator();
    while (opTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(opTypesIt.next());
    }

    return count;
  }

  public int getOutCount() {
    return getUnderTypes().size() + 2 * getOpTypes().size();
  }

  public int getOutMachineCount() {
    int count = 0;

    Iterator<Type> underTypesIt = getUnderTypes().iterator();
    while (underTypesIt.hasNext()) {
      count += AbstractJasminClass.sizeOfType(underTypesIt.next());
    }

    Iterator<Type> opTypesIt = getOpTypes().iterator();
    while (opTypesIt.hasNext()) {
      count += 2 * AbstractJasminClass.sizeOfType(opTypesIt.next());
    }
    return count;
  }

  public void apply(Switch sw) {
    throw new RuntimeException();
  }

}
