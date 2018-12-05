package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.Value;
import soot.ValueBox;
import soot.jimple.DefinitionStmt;

@SuppressWarnings("serial")
public abstract class AbstractDefinitionStmt extends AbstractStmt implements DefinitionStmt {
  public final ValueBox leftBox;
  public final ValueBox rightBox;

  protected AbstractDefinitionStmt(ValueBox leftBox, ValueBox rightBox) {
    this.leftBox = leftBox;
    this.rightBox = rightBox;
  }

  @Override
  public final Value getLeftOp() {
    return leftBox.getValue();
  }

  @Override
  public final Value getRightOp() {
    return rightBox.getValue();
  }

  @Override
  public final ValueBox getLeftOpBox() {
    return leftBox;
  }

  @Override
  public final ValueBox getRightOpBox() {
    return rightBox;
  }

  @Override
  public final List<ValueBox> getDefBoxes() {
    return Collections.singletonList(leftBox);
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();
    list.addAll(getLeftOp().getUseBoxes());
    list.add(rightBox);
    list.addAll(getRightOp().getUseBoxes());
    return list;
  }

  @Override
  public boolean fallsThrough() {
    return true;
  }

  @Override
  public boolean branches() {
    return false;
  }
}
