package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.util.Numberable;

/**
 * Enumeration type representing the kind of a call graph edge.
 *
 * @author Ondrej Lhotak
 */
public final class Kind implements Numberable {
  public static final Kind INVALID = new Kind("INVALID");
  /** Due to explicit invokestatic instruction. */
  public static final Kind STATIC = new Kind("STATIC");
  /** Due to explicit invokevirtual instruction. */
  public static final Kind VIRTUAL = new Kind("VIRTUAL");
  /** Due to explicit invokeinterface instruction. */
  public static final Kind INTERFACE = new Kind("INTERFACE");
  /** Due to explicit invokespecial instruction. */
  public static final Kind SPECIAL = new Kind("SPECIAL");
  /** Implicit call to static initializer. */
  public static final Kind CLINIT = new Kind("CLINIT");
  /** Implicit call to Thread.run() due to Thread.start() call. */
  public static final Kind THREAD = new Kind("THREAD");
  /**
   * Implicit call to java.lang.Runnable.run() due to Executor.execute() call.
   */
  public static final Kind EXECUTOR = new Kind("EXECUTOR");
  /**
   * Implicit call to AsyncTask.doInBackground() due to AsyncTask.execute() call.
   */
  public static final Kind ASYNCTASK = new Kind("ASYNCTASK");
  /** Implicit call to java.lang.ref.Finalizer.register from new bytecode. */
  public static final Kind FINALIZE = new Kind("FINALIZE");
  /**
   * Implicit call to Handler.handleMessage(android.os.Message) due to Handler.sendxxxxMessagexxxx() call.
   */
  public static final Kind HANDLER = new Kind("HANDLER");
  /**
   * Implicit call to finalize() from java.lang.ref.Finalizer.invokeFinalizeMethod().
   */
  public static final Kind INVOKE_FINALIZE = new Kind("INVOKE_FINALIZE");
  /** Implicit call to run() through AccessController.doPrivileged(). */
  public static final Kind PRIVILEGED = new Kind("PRIVILEGED");
  /** Implicit call to constructor from java.lang.Class.newInstance(). */
  public static final Kind NEWINSTANCE = new Kind("NEWINSTANCE");
  /** Due to call to Method.invoke(..). */
  public static final Kind REFL_INVOKE = new Kind("REFL_METHOD_INVOKE");
  /** Due to call to Constructor.newInstance(..). */
  public static final Kind REFL_CONSTR_NEWINSTANCE = new Kind("REFL_CONSTRUCTOR_NEWINSTANCE");
  /** Due to call to Class.newInstance(..) when reflection log is enabled. */
  public static final Kind REFL_CLASS_NEWINSTANCE = new Kind("REFL_CLASS_NEWINSTANCE");

  private Kind(String name) {
    this.name = name;
  }

  private final String name;
  private int num;

  public String name() {
    return name;
  }

  public int getNumber() {
    return num;
  }

  public void setNumber(int num) {
    this.num = num;
  }

  public String toString() {
    return name();
  }

  public boolean passesParameters() {
    return isExplicit() || this == THREAD || this == EXECUTOR || this == ASYNCTASK || this == FINALIZE || this == PRIVILEGED
        || this == NEWINSTANCE || this == INVOKE_FINALIZE || this == REFL_INVOKE || this == REFL_CONSTR_NEWINSTANCE
        || this == REFL_CLASS_NEWINSTANCE;
  }

  public boolean isFake() {
    return this == THREAD || this == EXECUTOR || this == ASYNCTASK || this == PRIVILEGED || this == HANDLER;
  }

  /** Returns true if the call is due to an explicit invoke statement. */
  public boolean isExplicit() {
    return isInstance() || isStatic();
  }

  /**
   * Returns true if the call is due to an explicit instance invoke statement.
   */
  public boolean isInstance() {
    return this == VIRTUAL || this == INTERFACE || this == SPECIAL;
  }

  /**
   * Returns true if the call is due to an explicit virtual invoke statement.
   */
  public boolean isVirtual() {
    return this == VIRTUAL;
  }

  public boolean isSpecial() {
    return this == SPECIAL;
  }

  /** Returns true if the call is to static initializer. */
  public boolean isClinit() {
    return this == CLINIT;
  }

  /**
   * Returns true if the call is due to an explicit static invoke statement.
   */
  public boolean isStatic() {
    return this == STATIC;
  }

  public boolean isThread() {
    return this == THREAD;
  }

  public boolean isExecutor() {
    return this == EXECUTOR;
  }

  public boolean isAsyncTask() {
    return this == ASYNCTASK;
  }

  public boolean isPrivileged() {
    return this == PRIVILEGED;
  }

  public boolean isReflection() {
    return this == REFL_CLASS_NEWINSTANCE || this == REFL_CONSTR_NEWINSTANCE || this == REFL_INVOKE;
  }

  public boolean isReflInvoke() {
    return this == REFL_INVOKE;
  }

}
