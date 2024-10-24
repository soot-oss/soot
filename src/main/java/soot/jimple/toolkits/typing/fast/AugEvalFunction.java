package soot.jimple.toolkits.typing.fast;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2008 Ben Bellamy
 *
 * All rights reserved.
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

import java.util.Collection;
import java.util.Collections;

import soot.ArrayType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.TrapManager;
import soot.Type;
import soot.Value;
import soot.dotnet.types.DotNetBasicTypes;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.XorExpr;

/**
 * @author Ben Bellamy
 */
public class AugEvalFunction implements IEvalFunction {

  private final JimpleBody jb;

  public AugEvalFunction(JimpleBody jb) {
    this.jb = jb;
  }

  public static Type eval_(ITyping tg, Value expr, Stmt stmt, JimpleBody jb) {
    if (expr instanceof ThisRef) {
      return ((ThisRef) expr).getType();
    } else if (expr instanceof ParameterRef) {
      return ((ParameterRef) expr).getType();
    } else if (expr instanceof Local) {
      // Changed to prevent null pointer exception in case of phantom classes where a null typing is
      // encountered. -syed
      return (tg == null) ? null : tg.get((Local) expr);
    } else if (expr instanceof BinopExpr) {
      BinopExpr be = (BinopExpr) expr;
      Type tl = eval_(tg, be.getOp1(), stmt, jb), tr = eval_(tg, be.getOp2(), stmt, jb);
      if (expr instanceof CmpExpr || expr instanceof CmpgExpr || expr instanceof CmplExpr) {
        return ByteType.v();
      } else if (expr instanceof GeExpr || expr instanceof GtExpr || expr instanceof LeExpr || expr instanceof LtExpr
          || expr instanceof EqExpr || expr instanceof NeExpr) {
        return BooleanType.v();
      } else if (expr instanceof ShlExpr || expr instanceof ShrExpr || expr instanceof UshrExpr) {
        // In the JVM, there are op codes for integer and long only. In Java, the code
        // {@code short s = 2; s = s << s;} does not compile, since s << s is an integer.
        return (tl instanceof IntegerType) ? IntType.v() : tl;
      } else if (expr instanceof AddExpr || expr instanceof SubExpr || expr instanceof MulExpr || expr instanceof DivExpr
          || expr instanceof RemExpr) {
        return (tl instanceof IntegerType) ? IntType.v() : tl;
      } else if (expr instanceof AndExpr || expr instanceof OrExpr || expr instanceof XorExpr) {
        if (tl instanceof IntegerType && tr instanceof IntegerType) {
          if (tl instanceof BooleanType) {
            return (tr instanceof BooleanType) ? BooleanType.v() : tr;
          } else if (tr instanceof BooleanType) {
            return tl;
          } else {
            Collection<Type> rs = AugHierarchy.lcas_(tl, tr, false);
            if (rs.isEmpty()) {
              throw new RuntimeException();
            } else {
              // AugHierarchy.lcas_ is single-valued
              assert (rs.size() == 1);
              return rs.iterator().next();
            }
          }
        } else {
          return (tl instanceof RefLikeType) ? tr : tl;
        }
      } else {
        throw new RuntimeException("Unhandled binary expression: " + expr);
      }
    } else if (expr instanceof NegExpr) {
      Type t = eval_(tg, ((NegExpr) expr).getOp(), stmt, jb);
      if (t instanceof IntegerType) {
        // The "ineg" bytecode causes and implicit widening to int type and produces an int type.
        return IntType.v();
      } else {
        return t;
      }
    } else if (expr instanceof CaughtExceptionRef) {
      RefType throwableType = Scene.v().getBaseExceptionType();
      RefType r = null;
      for (RefType t : TrapManager.getExceptionTypesOf(stmt, jb)) {
        if (t.getSootClass().isPhantom()) {
          r = throwableType;
        } else if (r == null) {
          r = t;
        } else {
          /*
           * In theory, we could have multiple exception types pointing here. The JLS requires the exception parameter be a
           * *subclass* of Throwable, so we do not need to worry about multiple inheritance.
           */
          r = BytecodeHierarchy.lcsc(r, t, throwableType);
        }
      }
      if (r == null) {
        throw new RuntimeException("Exception reference used other than as the first statement of an exception handler.");
      }
      return r;
    } else if (expr instanceof ArrayRef) {
      Type at = tg.get((Local) ((ArrayRef) expr).getBase());

      if (at instanceof ArrayType) {
        return ((ArrayType) at).getElementType();
      } else if (at instanceof WeakObjectType) {
        return at;
      } else if (at instanceof RefType) {
        String name = ((RefType) at).getSootClass().getName();
        if (name.equals(Scene.v().getObjectType().toString())) {
          return new WeakObjectType(name);
        }
        switch (name) {
          case "java.lang.Cloneable":
          case "java.lang.Object":
          case "java.io.Serializable":
          case DotNetBasicTypes.SYSTEM_ARRAY:
            return new WeakObjectType(name);
          default:
            return BottomType.v();
        }
      } else {
        return BottomType.v();
      }
    } else if (expr instanceof NewArrayExpr) {
      return ((NewArrayExpr) expr).getBaseType().makeArrayType();
    } else if (expr instanceof NewMultiArrayExpr) {
      return ((NewMultiArrayExpr) expr).getBaseType();
    } else if (expr instanceof CastExpr) {
      return ((CastExpr) expr).getCastType();
    } else if (expr instanceof InstanceOfExpr) {
      return BooleanType.v();
    } else if (expr instanceof LengthExpr) {
      return IntType.v();
    } else if (expr instanceof InvokeExpr) {
      return ((InvokeExpr) expr).getMethodRef().getReturnType();
    } else if (expr instanceof NewExpr) {
      return ((NewExpr) expr).getBaseType();
    } else if (expr instanceof FieldRef) {
      return ((FieldRef) expr).getType();
    } else if (expr instanceof DoubleConstant) {
      return DoubleType.v();
    } else if (expr instanceof FloatConstant) {
      return FloatType.v();
    } else if (expr instanceof IntConstant) {
      int value = ((IntConstant) expr).value;

      if (value >= 0 && value < 2) {
        return Integer1Type.v();
      } else if (value >= 2 && value < 128) {
        return Integer127Type.v();
      } else if (value >= -128 && value < 0) {
        return ByteType.v();
      } else if (value >= 128 && value < 32768) {
        return Integer32767Type.v();
      } else if (value >= -32768 && value < -128) {
        return ShortType.v();
      } else if (value >= 32768 && value < 65536) {
        return CharType.v();
      } else {
        return IntType.v();
      }
    } else if (expr instanceof LongConstant) {
      return LongType.v();
    } else if (expr instanceof NullConstant) {
      return NullType.v();
    } else if (expr instanceof StringConstant) {
      return RefType.v("java.lang.String");
    } else if (expr instanceof ClassConstant) {
      return RefType.v("java.lang.Class");
    } else if (expr instanceof MethodHandle) {
      return RefType.v("java.lang.invoke.MethodHandle");
    } else if (expr instanceof MethodType) {
      return RefType.v("java.lang.invoke.MethodType");
    } else {
      throw new RuntimeException("Unhandled expression: " + expr);
    }
  }

  @Override
  public Collection<Type> eval(ITyping tg, Value expr, Stmt stmt) {
    return Collections.<Type>singletonList(eval_(tg, expr, stmt, this.jb));
  }
}
