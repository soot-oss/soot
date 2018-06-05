package soot.jimple;

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

import java.util.HashMap;
import java.util.Map;

import soot.Local;
import soot.Unit;
import soot.baf.BafBody;

public class JimpleToBafContext {
  private Map<Local, Local> jimpleLocalToBafLocal = new HashMap<Local, Local>();
  private BafBody bafBody;
  private Unit mCurrentUnit;

  /**
   * An approximation of the local count is required in order to allocate a reasonably sized hash map.
   */

  public JimpleToBafContext(int localCount) {
    jimpleLocalToBafLocal = new HashMap<Local, Local>(localCount * 2 + 1, 0.7f);
  }

  public void setCurrentUnit(Unit u) {
    mCurrentUnit = u;
  }

  public Unit getCurrentUnit() {
    return mCurrentUnit;
  }

  public Local getBafLocalOfJimpleLocal(Local jimpleLocal) {
    return jimpleLocalToBafLocal.get(jimpleLocal);
  }

  public void setBafLocalOfJimpleLocal(Local jimpleLocal, Local bafLocal) {
    jimpleLocalToBafLocal.put(jimpleLocal, bafLocal);
  }

  public BafBody getBafBody() {
    return bafBody;
  }

  public void setBafBody(BafBody bafBody) {
    this.bafBody = bafBody;
  }

}
