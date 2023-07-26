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
  /**
   * Due to explicit invokestatic instruction.
   */
  public static final Kind STATIC = new Kind("STATIC");
  /**
   * Due to explicit invokevirtual instruction.
   */
  public static final Kind VIRTUAL = new Kind("VIRTUAL");
  /**
   * Due to explicit invokeinterface instruction.
   */
  public static final Kind INTERFACE = new Kind("INTERFACE");
  /**
   * Due to explicit invokespecial instruction.
   */
  public static final Kind SPECIAL = new Kind("SPECIAL");
  /**
   * Implicit call to static initializer.
   */
  public static final Kind CLINIT = new Kind("CLINIT");
  /**
   * Fake edges from our generic callback model.
   */
  public static final Kind GENERIC_FAKE = new Kind("GENERIC_FAKE");
  /**
   * Implicit call to Thread.run() due to Thread.start() call.
   */
  public static final Kind THREAD = new Kind("THREAD");
  /**
   * Implicit call to java.lang.Runnable.run() due to Executor.execute() call.
   */
  public static final Kind EXECUTOR = new Kind("EXECUTOR");
  /**
   * Implicit call to AsyncTask.doInBackground() due to AsyncTask.execute() call.
   */
  public static final Kind ASYNCTASK = new Kind("ASYNCTASK");
  /**
   * Implicit call to java.lang.ref.Finalizer.register from new bytecode.
   */
  public static final Kind FINALIZE = new Kind("FINALIZE");
  /**
   * Implicit call to Handler.handleMessage(android.os.Message) due to Handler.sendxxxxMessagexxxx() call.
   */
  public static final Kind HANDLER = new Kind("HANDLER");
  /**
   * Implicit call to finalize() from java.lang.ref.Finalizer.invokeFinalizeMethod().
   */
  public static final Kind INVOKE_FINALIZE = new Kind("INVOKE_FINALIZE");
  /**
   * Implicit call to run() through AccessController.doPrivileged().
   */
  public static final Kind PRIVILEGED = new Kind("PRIVILEGED");
  /**
   * Implicit call to constructor from java.lang.Class.newInstance().
   */
  public static final Kind NEWINSTANCE = new Kind("NEWINSTANCE");
  /**
   * Due to call to Method.invoke(..).
   */
  public static final Kind REFL_INVOKE = new Kind("REFL_METHOD_INVOKE");
  /**
   * Due to call to Constructor.newInstance(..).
   */
  public static final Kind REFL_CONSTR_NEWINSTANCE = new Kind("REFL_CONSTRUCTOR_NEWINSTANCE");
  /**
   * Due to call to Class.newInstance(..) when reflection log is enabled.
   */
  public static final Kind REFL_CLASS_NEWINSTANCE = new Kind("REFL_CLASS_NEWINSTANCE");

  private final String name;
  private int num;

  private Kind(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  @Override
  public int getNumber() {
    return num;
  }

  @Override
  public void setNumber(int num) {
    this.num = num;
  }

  @Override
  public String toString() {
    return name();
  }

  public boolean passesParameters() {
    return passesParameters(this);
  }

  public boolean isFake() {
    return isFake(this);
  }

  /**
   * Returns true if the call is due to an explicit invoke statement.
   */
  public boolean isExplicit() {
    return isExplicit(this);
  }

  /**
   * Returns true if the call is due to an explicit instance invoke statement.
   */
  public boolean isInstance() {
    return isInstance(this);
  }

  /**
   * Returns true if the call is due to an explicit virtual invoke statement.
   */
  public boolean isVirtual() {
    return isVirtual(this);
  }

  public boolean isSpecial() {
    return isSpecial(this);
  }

  /**
   * Returns true if the call is to static initializer.
   */
  public boolean isClinit() {
    return isClinit(this);
  }

  /**
   * Returns true if the call is due to an explicit static invoke statement.
   */
  public boolean isStatic() {
    return isStatic(this);
  }

  public boolean isThread() {
    return isThread(this);
  }

  public boolean isExecutor() {
    return isExecutor(this);
  }

  public boolean isAsyncTask() {
    return isAsyncTask(this);
  }

  public boolean isPrivileged() {
    return isPrivileged(this);
  }

  public boolean isReflection() {
    return isReflection(this);
  }

  public boolean isReflInvoke() {
    return isReflInvoke(this);
  }

  public boolean isHandler() {
    return isHandler(this);
  }

  public static boolean passesParameters(Kind k) {
    return isExplicit(k) || k == THREAD || k == EXECUTOR || k == ASYNCTASK || k == FINALIZE || k == PRIVILEGED
        || k == NEWINSTANCE || k == INVOKE_FINALIZE || k == REFL_INVOKE || k == REFL_CONSTR_NEWINSTANCE
        || k == REFL_CLASS_NEWINSTANCE || k == GENERIC_FAKE;
  }

  public static boolean isFake(Kind k) {
    return k == THREAD || k == EXECUTOR || k == ASYNCTASK || k == PRIVILEGED || k == HANDLER || k == GENERIC_FAKE;
  }

  /**
   * Returns true if the call is due to an explicit invoke statement.
   */
  public static boolean isExplicit(Kind k) {
    return isInstance(k) || isStatic(k);
  }

  /**
   * Returns true if the call is due to an explicit instance invoke statement.
   */
  public static boolean isInstance(Kind k) {
    return k == VIRTUAL || k == INTERFACE || k == SPECIAL;
  }

  /**
   * Returns true if the call is due to an explicit virtual invoke statement.
   */
  public static boolean isVirtual(Kind k) {
    return k == VIRTUAL;
  }

  public static boolean isSpecial(Kind k) {
    return k == SPECIAL;
  }

  /**
   * Returns true if the call is to static initializer.
   */
  public static boolean isClinit(Kind k) {
    return k == CLINIT;
  }

  /**
   * Returns true if the call is due to an explicit static invoke statement.
   */
  public static boolean isStatic(Kind k) {
    return k == STATIC;
  }

  public static boolean isThread(Kind k) {
    return k == THREAD;
  }

  public static boolean isExecutor(Kind k) {
    return k == EXECUTOR;
  }

  public static boolean isAsyncTask(Kind k) {
    return k == ASYNCTASK;
  }

  public static boolean isPrivileged(Kind k) {
    return k == PRIVILEGED;
  }

  public static boolean isReflection(Kind k) {
    return k == REFL_CLASS_NEWINSTANCE || k == REFL_CONSTR_NEWINSTANCE || k == REFL_INVOKE;
  }

  public static boolean isReflInvoke(Kind k) {
    return k == REFL_INVOKE;
  }

  public static boolean isHandler(Kind k) {
    return k == HANDLER;
  }
}
