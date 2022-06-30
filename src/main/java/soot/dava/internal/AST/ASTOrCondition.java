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

import soot.UnitPrinter;
import soot.dava.DavaUnitPrinter;
import soot.dava.toolkits.base.AST.analysis.Analysis;

public class ASTOrCondition extends ASTAggregatedCondition {
  public ASTOrCondition(ASTCondition left, ASTCondition right) {
    super(left, right);
  }

  public void apply(Analysis a) {
    a.caseASTOrCondition(this);
  }

  public String toString() {
    if (left instanceof ASTUnaryBinaryCondition) {
      if (right instanceof ASTUnaryBinaryCondition) {
        if (not) {
          return "!(" + left.toString() + " || " + right.toString() + ")";
        } else {
          return left.toString() + " || " + right.toString();
        }
      } else { // right is ASTAggregatedCondition
        if (not) {
          return "!(" + left.toString() + " || (" + right.toString() + " ))";
        } else {
          return left.toString() + " || (" + right.toString() + " )";
        }
      }
    } else { // left is ASTAggregatedCondition
      if (right instanceof ASTUnaryBinaryCondition) {
        if (not) {
          return "!(( " + left.toString() + ") || " + right.toString() + ")";
        } else {
          return "( " + left.toString() + ") || " + right.toString();
        }
      } else { // right is ASTAggregatedCondition also
        if (not) {
          return "!(( " + left.toString() + ") || (" + right.toString() + " ))";
        } else {
          return "( " + left.toString() + ") || (" + right.toString() + " )";
        }
      }
    }
  }

  public void toString(UnitPrinter up) {
    if (up instanceof DavaUnitPrinter) {

      if (not) {
        // print !
        ((DavaUnitPrinter) up).addNot();
        // print LeftParen
        ((DavaUnitPrinter) up).addLeftParen();
      }
      if (left instanceof ASTUnaryBinaryCondition) {
        if (right instanceof ASTUnaryBinaryCondition) {

          left.toString(up);

          ((DavaUnitPrinter) up).addAggregatedOr();

          right.toString(up);
        } else { // right is ASTAggregatedCondition

          left.toString(up);

          ((DavaUnitPrinter) up).addAggregatedOr();

          ((DavaUnitPrinter) up).addLeftParen();
          right.toString(up);
          ((DavaUnitPrinter) up).addRightParen();
        }
      } else { // left is ASTAggregatedCondition
        if (right instanceof ASTUnaryBinaryCondition) {

          ((DavaUnitPrinter) up).addLeftParen();
          left.toString(up);
          ((DavaUnitPrinter) up).addRightParen();

          ((DavaUnitPrinter) up).addAggregatedOr();

          right.toString(up);
        } else { // right is ASTAggregatedCondition also

          ((DavaUnitPrinter) up).addLeftParen();
          left.toString(up);
          ((DavaUnitPrinter) up).addRightParen();

          ((DavaUnitPrinter) up).addAggregatedOr();

          ((DavaUnitPrinter) up).addLeftParen();
          right.toString(up);
          ((DavaUnitPrinter) up).addRightParen();
        }
      }
      if (not) {
        ((DavaUnitPrinter) up).addRightParen();
      }
    } else {
      throw new RuntimeException();
    }
  }

}
