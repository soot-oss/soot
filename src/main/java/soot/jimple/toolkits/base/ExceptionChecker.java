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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.FastHierarchy;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soot.tagkit.SourceLnPosTag;
import soot.util.NumberedString;

public class ExceptionChecker extends BodyTransformer {

  FastHierarchy hierarchy;
  ExceptionCheckerErrorReporter reporter;

  public ExceptionChecker(ExceptionCheckerErrorReporter r) {
    this.reporter = r;
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map options) {

    Iterator it = b.getUnits().iterator();
    while (it.hasNext()) {
      Stmt s = (Stmt) it.next();
      if (s instanceof ThrowStmt) {
        ThrowStmt ts = (ThrowStmt) s;
        checkThrow(b, ts);
      } else if (s instanceof InvokeStmt) {
        InvokeStmt is = (InvokeStmt) s;
        checkInvoke(b, is);
      } else if ((s instanceof AssignStmt) && (((AssignStmt) s).getRightOp() instanceof InvokeExpr)) {
        InvokeExpr ie = (InvokeExpr) ((AssignStmt) s).getRightOp();
        checkInvokeExpr(b, ie, s);
      }
    }
  }

  protected void checkThrow(Body b, ThrowStmt ts) {
    if (isThrowDeclared(b, ((RefType) ts.getOp().getType()).getSootClass()) || isThrowFromCompiler(ts)
        || isExceptionCaught(b, ts, (RefType) ts.getOp().getType())) {
      return;
    }
    if (reporter != null) {
      reporter.reportError(new ExceptionCheckerError(b.getMethod(), ((RefType) ts.getOp().getType()).getSootClass(), ts,
          (SourceLnPosTag) ts.getOpBox().getTag("SourceLnPosTag")));
    }
  }

  // does the method declare the throw if its a throw that needs declaring
  // RuntimeException and subclasses do not need to be declared
  // Error and subclasses do not need to be declared
  protected boolean isThrowDeclared(Body b, SootClass throwClass) {
    if (hierarchy == null) {
      hierarchy = new FastHierarchy();
    }

    // handles case when exception is RuntimeException or Error
    if (throwClass.equals(Scene.v().getSootClass("java.lang.RuntimeException"))
        || throwClass.equals(Scene.v().getSootClass("java.lang.Error"))) {
      return true;
    }
    // handles case when exception is a subclass of RuntimeException or Error
    if (hierarchy.isSubclass(throwClass, Scene.v().getSootClass("java.lang.RuntimeException"))
        || hierarchy.isSubclass(throwClass, Scene.v().getSootClass("java.lang.Error"))) {
      return true;
    }

    // handles case when exact exception is thrown
    if (b.getMethod().throwsException(throwClass)) {
      return true;
    }

    // handles case when a super type of the exception is thrown
    List<SootClass> exceptions = b.getMethod().getExceptionsUnsafe();
    if (exceptions != null) {
      Iterator<SootClass> it = exceptions.iterator();
      while (it.hasNext()) {
        SootClass nextEx = it.next();
        if (hierarchy.isSubclass(throwClass, nextEx)) {
          return true;
        }
      }
    }
    return false;
  }

  // is the throw created by the compiler
  protected boolean isThrowFromCompiler(ThrowStmt ts) {
    if (ts.hasTag("ThrowCreatedByCompilerTag")) {
      return true;
    }
    return false;
  }

  // is the throw caught inside the method
  protected boolean isExceptionCaught(Body b, Stmt s, RefType throwType) {
    if (hierarchy == null) {
      hierarchy = new FastHierarchy();
    }
    Iterator it = b.getTraps().iterator();
    while (it.hasNext()) {
      Trap trap = (Trap) it.next();
      if (trap.getException().getType().equals(throwType)
          || hierarchy.isSubclass(throwType.getSootClass(), (trap.getException().getType()).getSootClass())) {
        if (isThrowInStmtRange(b, (Stmt) trap.getBeginUnit(), (Stmt) trap.getEndUnit(), s)) {
          return true;
        }
      }
    }
    return false;
  }

  protected boolean isThrowInStmtRange(Body b, Stmt begin, Stmt end, Stmt s) {
    Iterator it = b.getUnits().iterator(begin, end);
    while (it.hasNext()) {
      if (it.next().equals(s)) {
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
    List<SootClass> result = null;
    SootClass obj = Scene.v().getSootClass("java.lang.Object");
    sm = obj.getMethodUnsafe(sig);
    if (sm.getExceptionsUnsafe() == null) {
      return Collections.emptyList();
    }
    if (sm != null) {
      result = new Vector<SootClass>(sm.getExceptions());
    }
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
    if (ie instanceof InstanceInvokeExpr && ((InstanceInvokeExpr) ie).getBase().getType() instanceof ArrayType
        && ie.getMethodRef().name().equals("clone") && ie.getMethodRef().parameterTypes().size() == 0) {
      return; // the call is to the clone() method of an array type, which
              // is defined not to throw any exceptions; if we left this to
              // normal resolution we'd get the method in Object which does
              // throw CloneNotSupportedException
    }

    List exceptions = ie instanceof InterfaceInvokeExpr
        // For an invokeinterface, there is no unique resolution for the
        // method reference that will get the "correct" exception spec. We
        // actually need to look at the intersection of all declarations of
        // the method in supertypes.
        ? getExceptionSpec(ie.getMethodRef().declaringClass(), ie.getMethodRef().getSubSignature())
        // Otherwise, we just do normal resolution.
        : ie.getMethod().getExceptionsUnsafe();
    if (exceptions == null) {
      return;
    }
    Iterator it = exceptions.iterator();
    while (it.hasNext()) {
      SootClass sc = (SootClass) it.next();
      if (isThrowDeclared(b, sc) || isExceptionCaught(b, s, sc.getType())) {
        continue;
      }
      if (reporter != null) {
        if (s instanceof InvokeStmt) {
          reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, s, (SourceLnPosTag) s.getTag("SourceLnPosTag")));
        } else if (s instanceof AssignStmt) {
          reporter.reportError(new ExceptionCheckerError(b.getMethod(), sc, s,
              (SourceLnPosTag) ((AssignStmt) s).getRightOpBox().getTag("SourceLnPosTag")));
        }
      }
    }
  }
}
