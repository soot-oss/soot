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

import polyglot.ast.Expr;
import polyglot.ast.TypeNode;
import polyglot.ext.jl.ast.Cast_c;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.Position;
import polyglot.visit.AscriptionVisitor;

public class JjCast_c extends Cast_c {

  public JjCast_c(Position pos, TypeNode castType, Expr expr) {
    super(pos, castType, expr);
  }

  public Type childExpectedType(Expr child, AscriptionVisitor av) {
    TypeSystem ts = av.typeSystem();

    if (child == expr) {
      if (castType.type().isReference()) {
        return ts.Object();
      } else if (castType.type().isNumeric()) {
        return castType.type();
        // return ts.Double();
      } else if (castType.type().isBoolean()) {
        return ts.Boolean();
      }
    }

    return child.type();

  }
}
