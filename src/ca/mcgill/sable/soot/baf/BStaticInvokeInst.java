package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import java.util.*;

public class BStaticInvokeInst extends AbstractInvokeInst implements StaticInvokeInst
{
    BStaticInvokeInst(SootMethod method) { setMethod(method); }

    public int getInCount()
    {
	return getMethod().getParameterCount();
	
    }
    
    public int getOutCount()
    {
	if(getMethod().getReturnType() instanceof VoidType)
	    return 0;
	else
	    return 1;
    }

    public String getName() { return "staticinvoke"; }
}
