package soot.javaToJimple;

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

public class CastInsertionVisitor extends polyglot.visit.AscriptionVisitor {

  public CastInsertionVisitor(polyglot.frontend.Job job, polyglot.types.TypeSystem ts, polyglot.ast.NodeFactory nf) {
    super(job, ts, nf);
  }

  public polyglot.ast.Expr ascribe(polyglot.ast.Expr e, polyglot.types.Type toType) {

    // System.out.println("expr: "+e);
    // System.out.println("expr: "+e.getClass());
    // System.out.println("to type: "+toType);
    polyglot.types.Type fromType = e.type();
    // System.out.println("from type: "+fromType);

    if (toType == null) {
      return e;
    }
    if (toType.isVoid()) {
      return e;
    }

    polyglot.util.Position p = e.position();

    if (toType.equals(fromType)) {
      return e;
    }

    /*
     * double -> (int, long, float) float -> (int, long, double) long -> (int, double, float) int (byte, char, short,
     * boolean) -> (byte, char, short, long, double, float) ie double to short goes through int etc.
     */
    if (toType.isPrimitive() && fromType.isPrimitive()) {

      polyglot.ast.Expr newExpr;

      // System.out.println("from type: "+fromType);
      // System.out.println("to type: "+toType);
      if (fromType.isFloat() || fromType.isLong() || fromType.isDouble()) {
        if (toType.isFloat() || toType.isLong() || toType.isDouble() || toType.isInt()) {
          newExpr = nf.Cast(p, nf.CanonicalTypeNode(p, toType), e).type(toType);
        } else {
          newExpr
              = nf.Cast(p, nf.CanonicalTypeNode(p, toType), nf.Cast(p, nf.CanonicalTypeNode(p, ts.Int()), e).type(ts.Int()))
                  .type(toType);
        }
      } else {
        newExpr = nf.Cast(p, nf.CanonicalTypeNode(p, toType), e).type(toType);
      }
      return newExpr;
    }

    return e;

  }

  public polyglot.ast.Node leaveCall(polyglot.ast.Node old, polyglot.ast.Node n, polyglot.visit.NodeVisitor v)
      throws polyglot.types.SemanticException {

    n = super.leaveCall(old, n, v);

    return n;
  }
}
