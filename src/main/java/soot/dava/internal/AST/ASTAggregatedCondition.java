package soot.dava.internal.AST;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

public abstract class ASTAggregatedCondition extends ASTCondition {
  ASTCondition left;
  ASTCondition right;
  boolean not;// used to see if the condition has a not infront of it

  public ASTAggregatedCondition(ASTCondition left, ASTCondition right) {
    not = false;// by default condition does not have a not
    this.left = left;
    this.right = right;
  }

  public ASTCondition getLeftOp() {
    return left;
  }

  public ASTCondition getRightOp() {
    return right;
  }

  public void setLeftOp(ASTCondition left) {
    this.left = left;
  }

  public void setRightOp(ASTCondition right) {
    this.right = right;
  }

  public void flip() {
    if (not) {
      not = false;
    } else {
      not = true;
    }
  }

  public boolean isNotted() {
    return not;
  }
}
