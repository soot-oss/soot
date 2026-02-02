package soot.jimple.toolkits.base;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.FastHierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.tagkit.SourceLnPosTag;
import soot.tagkit.ThrowCreatedByCompilerTag;
import soot.util.NumberedString;

public class ExceptionChecker extends BodyTransformer {

  protected final ExceptionCheckerErrorReporter reporter;
  protected FastHierarchy hierarchy;

  public ExceptionChecker(ExceptionCheckerErrorReporter r) {
    this.reporter = r;
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Unit u : b.getUnits()) {
      Stmt s = (Stmt) u;
      if (s instanceof ThrowStmt) {
        checkThrow(b, (ThrowStmt) s);
      } else if (s instanceof InvokeStmt) {
        checkInvoke(b, (InvokeStmt) s);
      } else if (s instanceof AssignStmt) {
        Value rightOp = ((AssignStmt) s).getRightOp();
        if (rightOp instanceof InvokeExpr) {
          checkInvokeExpr(b, (InvokeExpr) rightOp, s);
        }
      }
    }
  }

  protected void checkThrow(Body b, ThrowStmt ts) {
    RefType opType = (RefType) ts.getOp().getType();
    if (isThrowDeclared(b, opType.getSootClass()) || isThrowFromCompiler(ts) || isExceptionCaught(b, ts, opType)) {
      return;
    }
    if (reporter != null) {
      reporter.reportError(new ExceptionCheckerError(b.getMethod(), opType.getSootClass(), ts,
          (SourceLnPosTag) ts.getOpBox().getTag(SourceLnPosTag.NAME)));
    }
  }

  // does the method declare the throw if its a throw that needs declaring
  // RuntimeException and subclasses do not need to be declared
  // Error and subclasses do not need to be declared
  protected boolean isThrowDeclared(Body b, SootClass throwClass) {
    if (hierarchy == null) {
      hierarchy = new FastHierarchy();
    }
    final SootClass sootClassRuntimeException = Scene.v().getSootClass("java.lang.RuntimeException");
    final SootClass sootClassError = Scene.v().getSootClass("java.lang.Error");

    // handles case when exception is RuntimeException or Error
    // handles case when exception is a subclass of RuntimeException or Error
    if (throwClass.equals(sootClassRuntimeException) || throwClass.equals(sootClassError)
        || hierarchy.isSubclass(throwClass, sootClassRuntimeException) || hierarchy.isSubclass(throwClass, sootClassError)) {
      return true;
    }
    // handles case when exact exception is thrown
    if (b.getMethod().throwsException(throwClass)) {
      return true;
    }

    // handles case when a super type of the exception is thrown
    List<SootClass> exceptions = b.getMethod().getExceptionsUnsafe();
    if (exceptions != null) {
      for (SootClass nextEx : exceptions) {
        if (hierarchy.isSubclass(throwClass, nextEx)) {
          return true;
        }
      }
    }
    return false;
  }

  // is the throw created by the compiler
  protected boolean isThrowFromCompiler(ThrowStmt ts) {
    return ts.hasTag(ThrowCreatedByCompilerTag.NAME);
  }

  // is the throw caught inside the method
  protected boolean isExceptionCaught(Body b, Stmt s, RefType throwType) {
    if (hierarchy == null) {
      hierarchy = new FastHierarchy();
    }

    for (Trap trap : b.getTraps()) {
      RefType type = trap.getException().getType();
      if (type.equals(throwType) || hierarchy.isSubclass(throwType.getSootClass(), type.getSootClass())) {
        if (isThrowInStmtRange(b, (Stmt) trap.getBeginUnit(), (Stmt) trap.getEndUnit(), s)) {
          return true;
        }
      }
    }
    return false;
  }

  protected boolean isThrowInStmtRange(Body b, Stmt begin, Stmt end, Stmt s) {
    for (Iterator<Unit> it = b.getUnits().iterator(begin, end); it.hasNext();) {
      Unit u = it.next();
      if (u.equals(s)) {
        return true;
      }
    }
    return false;
  }

  protected void checkInvoke(Body b, InvokeStmt is) {
    checkInvokeExpr(b, is.getInvokeExpr(), is);
  }

  // Given a method signature, see if it is declared in the given interface.
  // If so, return the exceptions thrown by the declaration. Otherwise,
  // Do the same thing recursively on superinterfaces and Object
  // and return the intersection. This gives
  // the maximal set of exceptions that could be declared to be thrown if the
  // interface had declared the method. Returns null if no supertype declares
  // the method.
  private List<SootClass> getExceptionSpec(SootClass intrface, NumberedString sig) {
    SootMethod sm = intrface.getMethodUnsafe(sig);
    if (sm != null) {
      return sm.getExceptions();
    }
    sm = Scene.v().getSootClass(Scene.v().getObjectType().toString()).getMethodUnsafe(sig);
    if (sm != null && sm.getExceptionsUnsafe() == null) {
      return Collections.emptyList();
    }

    List<SootClass> result = sm == null ? null : new ArrayList<SootClass>(sm.getExceptions());
    for (SootClass suprintr : intrface.getInterfaces()) {
      List<SootClass> other = getExceptionSpec(suprintr, sig);
      if (other != null) {
        if (result == null) {
          result = other;
        } else {
          result.retainAll(other);
        }
      }
    }
    return result;
  }

  protected void checkInvokeExpr(Body b, InvokeExpr ie, Stmt s) {
    final SootMethodRef methodRef = ie.getMethodRef();
    if ("clone".equals(methodRef.name()) && methodRef.parameterTypes().isEmpty() && ie instanceof InstanceInvokeExpr
        && ((InstanceInvokeExpr) ie).getBase().getType() instanceof ArrayType) {
      // the call is to the clone() method of an array type, which
      // is defined not to throw any exceptions; if we left this to
      // normal resolution we'd get the method in Object which does
      // throw CloneNotSupportedException
      return;
    }

    // For an invokeinterface, there is no unique resolution for the
    // method reference that will get the "correct" exception spec. We
    // actually need to look at the intersection of all declarations of
    // the method in supertypes.
    // Otherwise, we just do normal resolution.
    List<SootClass> exceptions
        = (ie instanceof InterfaceInvokeExpr) ? getExceptionSpec(methodRef.declaringClass(), methodRef.getSubSignature())
            : ie.getMethod().getExceptionsUnsafe();

    if (exceptions != null) {
      for (SootClass sc : exceptions) {
        if (isThrowDeclared(b, sc) || isExceptionCaught(b, s, sc.getType())) {
          continue;
        }
        if (reporter != null) {
          if (s instanceof InvokeStmt) {
            reporter.reportError(
                new ExceptionCheckerError(b.getMethod(), sc, s, (SourceLnPosTag) s.getTag(SourceLnPosTag.NAME)));
          } else if (s instanceof AssignStmt) {
            reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, s,
                (SourceLnPosTag) ((AssignStmt) s).getRightOpBox().getTag(SourceLnPosTag.NAME)));
          }
        }
      }
    }
  }
}
