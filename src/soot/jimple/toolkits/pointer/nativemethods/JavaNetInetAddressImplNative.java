/**
 * Simulates the native method side effects in class java.net.InetAddressImpl
 *
 * @author Feng Qian
 * @author <XXX>
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.jimple.toolkits.pointer.util.*;

public class JavaNetInetAddressImplNative extends NativeMethodClass {
    public JavaNetInetAddressImplNative( Singletons.Global g ) {}
    public static JavaNetInetAddressImplNative v() { return G.v().JavaNetInetAddressImplNative(); }

  /**
   * Implements the abstract method simulateMethod.
   * It distributes the request to the corresponding methods 
   * by signatures.
   */
  public void simulateMethod(SootMethod method,
			     ReferenceVariable thisVar,
			     ReferenceVariable returnVar,
			     ReferenceVariable params[]){

    String subSignature = method.getSubSignature();

    if (subSignature.equals("java.lang.String getLocalHostName()")){
      java_net_InetAddressImpl_getLocalHostName(method, thisVar, 
						returnVar, params);
      return;

    } else if (subSignature.equals("java.lang.String getHostByAddress(int)")){
      java_net_InetAddressImpl_getHostByAddr(method, thisVar, 
					     returnVar, params);
      return;

    } else {
      defaultMethod(method, thisVar, returnVar, params);
      return;

    }
  }

  /************************ java.net.InetAddressImpl *****************/
  /**
   * Returns a variable pointing to a string constant
   *
   * I am not sure if repeated calls of methods in this class will
   * return the same object or not. A conservative approach would
   * say YES, for definitely points-to, but NO for may points-to.
   *
   * We should avoid analyzing these unsafe native methods.
   *
   *     native java.lang.String getLocalHostName() 
   *                      throws java.net.UnknownHostException;
   */
  public static 
    void java_net_InetAddressImpl_getLocalHostName(
					  SootMethod method,
                                          ReferenceVariable thisVar,
					  ReferenceVariable returnVar,
					  ReferenceVariable params[]){
    NativeHelper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }
  
  /**
   * Create a string object
   *
   *     native java.lang.String getHostByAddr(int) 
   *                     throws java.net.UnknownHostException;
   */
  public static 
    void java_net_InetAddressImpl_getHostByAddr(SootMethod method,
						ReferenceVariable thisVar,
						ReferenceVariable returnVar,
						ReferenceVariable params[]) {
    NativeHelper.assignObjectTo(returnVar, Environment.v().getStringObject());
  }

  /**
   * NO side effects.
   *    native void makeAnyLocalAddress(java.net.InetAddress);
   *    native byte lookupAllHostAddr(java.lang.String)[][] 
   *                     throws java.net.UnknownHostException;
   *    native int getInetFamily();
   *
   * @see default(...)
   */

}
