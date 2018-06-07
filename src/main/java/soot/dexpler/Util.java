package soot.dexpler;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.AssignStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.scalar.LocalCreation;

public class Util {
  /**
   * Return the dotted class name of a type descriptor, i.e. change Ljava/lang/Object; to java.lang.Object.
   *
   * @raises IllegalArgumentException if classname is not of the form Lpath; or [Lpath;
   * @return the dotted name.
   */
  public static String dottedClassName(String typeDescriptor) {
    if (!isByteCodeClassName(typeDescriptor)) {
      // typeDescriptor may not be a class but something like "[[[[[[[[J"
      String t = typeDescriptor;
      int idx = 0;
      while (idx < t.length() && t.charAt(idx) == '[') {
        idx++;
      }
      String c = t.substring(idx);
      if (c.length() == 1 && (c.startsWith("I") || c.startsWith("B") || c.startsWith("C") || c.startsWith("S")
          || c.startsWith("J") || c.startsWith("D") || c.startsWith("F") || c.startsWith("Z"))) {
        Type ty = getType(t);
        return ty == null ? "" : getType(t).toString();
      }
      throw new IllegalArgumentException("typeDescriptor is not a class typedescriptor: '" + typeDescriptor + "'");
    }
    String t = typeDescriptor;
    int idx = 0;
    while (idx < t.length() && t.charAt(idx) == '[') {
      idx++;
    }
    // Debug.printDbg("t ", t ," idx ", idx);
    String className = typeDescriptor.substring(idx);

    className = className.substring(className.indexOf('L') + 1, className.indexOf(';'));

    className = className.replace('/', '.');
    // for (int i = 0; i<idx; i++) {
    // className += "[]";
    // }
    return className;
  }

  public static Type getType(String type) {
    int idx = 0;
    int arraySize = 0;
    Type returnType = null;
    boolean notFound = true;
    while (idx < type.length() && notFound) {
      switch (type.charAt(idx)) {
        case '[':
          while (idx < type.length() && type.charAt(idx) == '[') {
            arraySize++;
            idx++;
          }
          continue;
        // break;

        case 'L':
          String objectName = type.replaceAll("^[^L]*L", "").replaceAll(";$", "");
          returnType = RefType.v(objectName.replace("/", "."));
          notFound = false;
          break;

        case 'J':
          returnType = LongType.v();
          notFound = false;
          break;

        case 'S':
          returnType = ShortType.v();
          notFound = false;
          break;

        case 'D':
          returnType = DoubleType.v();
          notFound = false;
          break;

        case 'I':
          returnType = IntType.v();
          notFound = false;
          break;

        case 'F':
          returnType = FloatType.v();
          notFound = false;
          break;

        case 'B':
          returnType = ByteType.v();
          notFound = false;
          break;

        case 'C':
          returnType = CharType.v();
          notFound = false;
          break;

        case 'V':
          returnType = VoidType.v();
          notFound = false;
          break;

        case 'Z':
          returnType = BooleanType.v();
          notFound = false;
          break;

        default:
          throw new RuntimeException("unknown type: '" + type + "'");
      }
      idx++;
    }
    if (returnType != null && arraySize > 0) {
      returnType = ArrayType.v(returnType, arraySize);
    }
    return returnType;
  }

  /**
   * Check if passed class name is a byte code classname.
   *
   * @param className
   *          the classname to check.
   */
  public static boolean isByteCodeClassName(String className) {
    return ((className.startsWith("L") || className.startsWith("[")) && className.endsWith(";")
        && ((className.indexOf('/') != -1 || className.indexOf('.') == -1)));
  }

  /**
   * Concatenate two arrays.
   *
   * @param first
   *          first array
   * @param second
   *          second array.
   */
  public static <T> T[] concat(T[] first, T[] second) {
    T[] result = Arrays.copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Returns if the type is a floating point type.
   *
   * @param t
   *          the type to test
   */
  public static boolean isFloatLike(Type t) {
    return t.equals(FloatType.v()) || t.equals(DoubleType.v()) || t.equals(RefType.v("java.lang.Float"))
        || t.equals(RefType.v("java.lang.Double"));
  }

  /**
   * Remove all statements except from IdentityStatements for parameters. Return default value (null or zero or nothing
   * depending on the return type).
   *
   * @param jBody
   */
  public static void emptyBody(Body jBody) {
    // identity statements
    List<Unit> idStmts = new ArrayList<Unit>();
    List<Local> idLocals = new ArrayList<Local>();
    for (Unit u : jBody.getUnits()) {
      if (u instanceof IdentityStmt) {
        IdentityStmt i = (IdentityStmt) u;
        if (i.getRightOp() instanceof ParameterRef || i.getRightOp() instanceof ThisRef) {
          idStmts.add(u);
          idLocals.add((Local) i.getLeftOp());
        }
      }
    }

    jBody.getUnits().clear();
    jBody.getLocals().clear();
    jBody.getTraps().clear();

    final LocalGenerator lg = new LocalGenerator(jBody);

    for (Unit u : idStmts) {
      jBody.getUnits().add(u);
    }
    for (Local l : idLocals) {
      jBody.getLocals().add(l);
    }

    Type rType = jBody.getMethod().getReturnType();

    jBody.getUnits().add(Jimple.v().newNopStmt());

    if (rType instanceof VoidType) {
      jBody.getUnits().add(Jimple.v().newReturnVoidStmt());
    } else {
      Type t = jBody.getMethod().getReturnType();
      Local l = lg.generateLocal(t);

      AssignStmt ass = null;
      if (t instanceof RefType || t instanceof ArrayType) {
        ass = Jimple.v().newAssignStmt(l, NullConstant.v());
      } else if (t instanceof LongType) {
        ass = Jimple.v().newAssignStmt(l, LongConstant.v(0));
      } else if (t instanceof FloatType) {
        ass = Jimple.v().newAssignStmt(l, FloatConstant.v(0.0f));
      } else if (t instanceof IntType) {
        ass = Jimple.v().newAssignStmt(l, IntConstant.v(0));
      } else if (t instanceof DoubleType) {
        ass = Jimple.v().newAssignStmt(l, DoubleConstant.v(0));
      } else if (t instanceof BooleanType || t instanceof ByteType || t instanceof CharType || t instanceof ShortType) {
        ass = Jimple.v().newAssignStmt(l, IntConstant.v(0));
      } else {
        throw new RuntimeException("error: return type unknown: " + t + " class: " + t.getClass());
      }
      jBody.getUnits().add(ass);
      jBody.getUnits().add(Jimple.v().newReturnStmt(l));
    }

  }

  /**
   * Insert a runtime exception before unit u of body b. Useful to analyze broken code (which make reference to inexisting
   * class for instance) exceptionType: e.g., "java.lang.RuntimeException"
   */
  public static void addExceptionAfterUnit(Body b, String exceptionType, Unit u, String m) {
    LocalCreation lc = new LocalCreation(b.getLocals());
    Local l = lc.newLocal(RefType.v(exceptionType));

    List<Unit> newUnits = new ArrayList<Unit>();
    Unit u1 = Jimple.v().newAssignStmt(l, Jimple.v().newNewExpr(RefType.v(exceptionType)));
    Unit u2
        = Jimple.v()
            .newInvokeStmt(Jimple.v().newSpecialInvokeExpr(l,
                Scene.v().makeMethodRef(Scene.v().getSootClass(exceptionType), "<init>",
                    Collections.singletonList((Type) RefType.v("java.lang.String")), VoidType.v(), false),
                StringConstant.v(m)));
    Unit u3 = Jimple.v().newThrowStmt(l);
    newUnits.add(u1);
    newUnits.add(u2);
    newUnits.add(u3);

    b.getUnits().insertBefore(newUnits, u);
  }

  public static List<String> splitParameters(String parameters) {
    List<String> pList = new ArrayList<String>();

    int idx = 0;
    boolean object = false;

    String curr = "";
    while (idx < parameters.length()) {
      char c = parameters.charAt(idx);
      curr += c;
      switch (c) {
        // array
        case '[':
          break;
        // end of object
        case ';':
          object = false;
          pList.add(curr);
          curr = "";
          break;
        // start of object
        case 'L':
          object = true;
          break;
        default:
          if (object) {
            // caracter part of object
          } else { // primitive
            pList.add(curr);
            curr = "";
          }
          break;

      }
      idx++;
    }

    return pList;
  }
}
