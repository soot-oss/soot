package soot;

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
