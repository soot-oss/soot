/**
 * Wrapper over NativeMethodDriver, this is implementation-dependent.
 *
 * @author Feng Qian
 */

package soot.jimple.toolkits.invoke;

import soot.jimple.toolkits.pointer.util.*;
import soot.jimple.toolkits.pointer.representations.*;
import soot.*;
import java.util.*;

public class VTANativeMethodWrapper2 {

  public static void initialize(VTANativeHelper2 helper){
    NativeHelper.register(helper);
  }

  public static void collect(SootMethod method) {

    ReferenceVariable thisVar = method.isStatic()? null:
      TypeGraphNode2.v(VTATypeGraph2.getVTALabel(method, "this"));

    Type rttype = method.getReturnType();
    ReferenceVariable returnVar = (rttype instanceof RefLikeType)?
      TypeGraphNode2.v(VTATypeGraph2.getVTALabel(method, "return")) : null;
    
    int paramcount = method.getParameterCount();
    ReferenceVariable params[] = new ReferenceVariable[paramcount];
    for (int i=0; i<paramcount; i++) {
      Type pmtype = method.getParameterType(i);
      if (pmtype instanceof RefLikeType) {
	params[i] = 
	  TypeGraphNode2.v(VTATypeGraph2.getVTALabel(method, "p"+i));
      }
    }

    NativeMethodDriver.process(method, thisVar, returnVar, params);
  }
}
