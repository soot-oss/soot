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

import java.util.Iterator;
import java.util.List;

import polyglot.ast.Expr;
import polyglot.ext.jl.ast.ArrayInit_c;
import polyglot.types.Type;
import polyglot.util.InternalCompilerError;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;

public class JjArrayInit_c extends ArrayInit_c {

  public JjArrayInit_c(Position pos, List elements) {
    super(pos, elements);
  }

  public Type childExpectedType(Expr child, AscriptionVisitor av) {
    if (elements.isEmpty()) {
      return child.type();
    }

    Type t = av.toType();

    // System.out.println("t type: "+t);
    if (t == null) {
      // System.out.println("t is null");
      return child.type();
    }
    if (!t.isArray()) {
      throw new InternalCompilerError("Type of array initializer must be " + "an array.", position());
    }

    t = t.toArray().base();

    for (Iterator i = elements.iterator(); i.hasNext();) {
      Expr e = (Expr) i.next();

      if (e == child) {
        return t;
      }
    }

    return child.type();
  }
}
