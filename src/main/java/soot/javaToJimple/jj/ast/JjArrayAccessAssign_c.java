package soot.javaToJimple.jj.ast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import polyglot.ast.ArrayAccess;
import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ArrayAccessAssign_c;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;

public class JjArrayAccessAssign_c extends ArrayAccessAssign_c {

  public JjArrayAccessAssign_c(Position pos, ArrayAccess left, Operator op, Expr right) {
    super(pos, left, op, right);
  }

  public Type childExpectedType(Expr child, AscriptionVisitor av) {
    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
      return child.type();
    }

    if (child == right) {
      return left.type();
    }

    return child.type();

  }
}
