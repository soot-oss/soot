/**
 * An exception throwed by the simulator when a native method's side-effect
 * can not be simulated safely.
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.pointer.nativemethods;

import soot.*;

public class NativeMethodNotSupportedException
  extends RuntimeException {

  private String msg;

  public NativeMethodNotSupportedException(SootMethod method){
    String message = "The following native method is not supported: \n  "
      +method.getSignature();
    this.msg = message;
  }

  public NativeMethodNotSupportedException(String message){
    this.msg = message;
  }

  public NativeMethodNotSupportedException(){
  }

  public String toString(){
    return msg;
  }
}
