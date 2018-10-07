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

import java.util.List;

import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;

public interface SwitchStmt extends Stmt {
  public Unit getDefaultTarget();

  public void setDefaultTarget(Unit defaultTarget);

  public UnitBox getDefaultTargetBox();

  public Value getKey();

  public void setKey(Value key);

  public ValueBox getKeyBox();

  public List<Unit> getTargets();

  public Unit getTarget(int index);

  public void setTarget(int index, Unit target);

  public UnitBox getTargetBox(int index);
}
