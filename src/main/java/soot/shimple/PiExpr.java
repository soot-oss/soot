package soot.shimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.Unit;
import soot.Value;
import soot.toolkits.scalar.ValueUnitPair;

/**
 * @author Navindra Umanee
 **/
public interface PiExpr extends ShimpleExpr {
  public ValueUnitPair getArgBox();

  public Value getValue();

  public Unit getCondStmt();

  public Object getTargetKey();

  public void setValue(Value v);

  public void setCondStmt(Unit cs);

  public void setTargetKey(Object targetKey);
}
