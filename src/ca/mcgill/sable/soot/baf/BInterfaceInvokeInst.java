package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import java.util.*;

public class BInterfaceInvokeInst extends AbstractInvokeInst 
                                  implements InterfaceInvokeInst
{
    int argCount;

    BInterfaceInvokeInst(SootMethod method, int argCount) 
        { setMethod(method); this.argCount = argCount; }
    final String getName() { return "interfaceinvoke"; }
    final String getParameters(boolean isBrief, Map unitToName)
        { return super.getParameters(isBrief, unitToName) + " " + argCount; }

    public int getArgCount() { return argCount; }
    public void setArgCount(int x) { argCount = x; }
}
