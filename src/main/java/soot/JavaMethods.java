package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
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

public class JavaMethods {

  public static final String SIG_MAIN = "void main(java.lang.String[])";
  public static final String SIG_FINALIZE = "void finalize()";
  public static final String SIG_EXIT = "void exit()";
  public static final String SIG_CLINIT = "void <clinit>()";
  public static final String SIG_INIT = "void <init>()";
  public static final String SIG_START = "void start()";
  public static final String SIG_RUN = "void run()";
  public static final String SIG_OBJ_RUN = "java.lang.Object run()";
  public static final String SIG_FOR_NAME = "java.lang.Class forName(java.lang.String)";

  public static final String INITIALIZE_SYSTEM_CLASS = "<java.lang.System: void initializeSystemClass()>";
  public static final String THREAD_GROUP_INIT = "<java.lang.ThreadGroup: void <init>()>";
  public static final String THREAD_EXIT = "<java.lang.Thread: void exit()>";
  public static final String THREADGROUP_UNCAUGHT_EXCEPTION
      = "<java.lang.ThreadGroup: void uncaughtException(java.lang.Thread,java.lang.Throwable)>";
  public static final String CLASSLOADER_INIT = "<java.lang.ClassLoader: void <init>()>";
  public static final String CLASSLOADER_LOAD_CLASS_INTERNAL
      = "<java.lang.ClassLoader: java.lang.Class loadClassInternal(java.lang.String)>";
  public static final String CLASSLOADER_CHECK_PACKAGE_ACC
      = "<java.lang.ClassLoader: void checkPackageAccess(java.lang.Class,java.security.ProtectionDomain)>";
  public static final String CLASSLOADER_ADD_CLASS = "<java.lang.ClassLoader: void addClass(java.lang.Class)>";
  public static final String CLASSLOADER_FIND_NATIVE
      = "<java.lang.ClassLoader: long findNative(java.lang.ClassLoader,java.lang.String)>";
  public static final String PRIV_ACTION_EXC_INIT
      = "<java.security.PrivilegedActionException: void <init>(java.lang.Exception)>";
  public static final String RUN_FINALIZE = "<java.lang.ref.Finalizer: void runFinalizer()>";
  public static final String THREAD_INIT_RUNNABLE
      = "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.Runnable)>";
  public static final String THREAD_INIT_STRING = "<java.lang.Thread: void <init>(java.lang.ThreadGroup,java.lang.String)>";
}
