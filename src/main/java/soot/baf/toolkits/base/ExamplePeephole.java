package soot.baf.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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

import soot.Body;
import soot.Unit;
import soot.baf.InstanceCastInst;

/**
 * Example peephole that remove all checkcast operations. Resulting class will likely not verify.
 */

public class ExamplePeephole implements Peephole {
  public boolean apply(Body b) {
    boolean changed = false;

    Iterator<Unit> it = b.getUnits().iterator();
    while (it.hasNext()) {
      Unit u = it.next();
      if (u instanceof InstanceCastInst) {
        it.remove();
        changed = true;
      }
    }

    return changed;
  }
}
