package soot.dava.toolkits.base.AST.transformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.ByteType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.grimp.internal.GCastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.Stmt;

public class TypeCastingError extends DepthFirstAdapter {
  public boolean myDebug = false;

  public TypeCastingError() {

  }

  public TypeCastingError(boolean verbose) {
    super(verbose);
  }

  public void inASTStatementSequenceNode(ASTStatementSequenceNode node) {
    for (AugmentedStmt as : node.getStatements()) {
      Stmt s = as.get_Stmt();
      if (!(s instanceof DefinitionStmt)) {
        continue;
      }

      DefinitionStmt ds = (DefinitionStmt) s;
      if (myDebug) {
        System.out.println("Definition stmt" + ds);
      }

      ValueBox rightBox = ds.getRightOpBox();
      ValueBox leftBox = ds.getLeftOpBox();

      Value right = rightBox.getValue();
      Value left = leftBox.getValue();

      if (!(left.getType() instanceof PrimType && right.getType() instanceof PrimType)) {
        // only interested in prim type casting errors
        if (myDebug) {
          System.out.println("\tDefinition stmt does not contain prims no need to modify");
        }
        continue;
      }

      Type leftType = left.getType();
      Type rightType = right.getType();
      if (myDebug) {
        System.out.println("Left type is: " + leftType);
      }
      if (myDebug) {
        System.out.println("Right type is: " + rightType);
      }
      if (leftType.equals(rightType)) {
        if (myDebug) {
          System.out.println("\tTypes are the same");
        }
        if (myDebug) {
          System.out.println("Right value is of instance" + right.getClass());
        }
      }
      if (!leftType.equals(rightType)) {
        if (myDebug) {
          System.out.println("\tDefinition stmt has to be modified");
        }
        // ByteType, DoubleType, FloatType, IntType, LongType, ShortType
        /*
         * byte Byte-length integer 8-bit two's complement short Short integer 16-bit two's complement int Integer 32-bit
         * two's complement long Long integer 64-bit two's complement float Single-precision floating point 32-bit IEEE 754
         * double Double-precision floating point 64-bit IEEE 754
         */
        if (leftType instanceof ByteType && (rightType instanceof DoubleType || rightType instanceof FloatType
            || rightType instanceof IntType || rightType instanceof LongType || rightType instanceof ShortType)) {
          // loss of precision do explicit casting

          if (DEBUG) {
            System.out.println("Explicit casting to BYTE required");
          }
          rightBox.setValue(new GCastExpr(right, ByteType.v()));
          if (DEBUG) {
            System.out.println("New right expr is " + rightBox.getValue().toString());
          }
          continue;
        }

        if (leftType instanceof ShortType && (rightType instanceof DoubleType || rightType instanceof FloatType
            || rightType instanceof IntType || rightType instanceof LongType)) {
          // loss of precision do explicit casting

          if (DEBUG) {
            System.out.println("Explicit casting to SHORT required");
          }
          rightBox.setValue(new GCastExpr(right, ShortType.v()));
          if (DEBUG) {
            System.out.println("New right expr is " + rightBox.getValue().toString());
          }
          continue;
        }

        if (leftType instanceof IntType
            && (rightType instanceof DoubleType || rightType instanceof FloatType || rightType instanceof LongType)) {
          // loss of precision do explicit casting

          if (myDebug) {
            System.out.println("Explicit casting to INT required");
          }
          rightBox.setValue(new GCastExpr(right, IntType.v()));
          if (myDebug) {
            System.out.println("New right expr is " + rightBox.getValue().toString());
          }
          continue;
        }

        if (leftType instanceof LongType && (rightType instanceof DoubleType || rightType instanceof FloatType)) {
          // loss of precision do explicit casting

          if (DEBUG) {
            System.out.println("Explicit casting to LONG required");
          }
          rightBox.setValue(new GCastExpr(right, LongType.v()));
          if (DEBUG) {
            System.out.println("New right expr is " + rightBox.getValue().toString());
          }
          continue;
        }

        if (leftType instanceof FloatType && rightType instanceof DoubleType) {
          // loss of precision do explicit casting

          if (DEBUG) {
            System.out.println("Explicit casting to FLOAT required");
          }
          rightBox.setValue(new GCastExpr(right, FloatType.v()));
          if (DEBUG) {
            System.out.println("New right expr is " + rightBox.getValue().toString());
          }
          continue;
        }
      }

    }

  }

}
