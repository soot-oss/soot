package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class BInterfaceInvokeInst extends AbstractInvokeInst 
                                  implements InterfaceInvokeInst
{
    int argCount;
    
    public int getInCount()
    {
        return getMethod().getParameterCount() +1;
        
    }

    public int getInMachineCount()
    {
        return getMethod().getParameterCount() +1;
        
    }
    
    public int getOutCount()
    {
        if(getMethod().getReturnType() instanceof VoidType)
            return 0;
        else
            return 1;
    }

    public int getOutMachineCount()
    {
        if(getMethod().getReturnType() instanceof VoidType)
            return 0;
        else
            return 1;
    }

    BInterfaceInvokeInst(SootMethod method, int argCount) 
        { setMethod(method); this.argCount = argCount; }


    public Object clone() 
    {
	return new  BInterfaceInvokeInst(getMethod(), getArgCount());
    }

    

    final String getName() { return "interfaceinvoke"; }
    final String getParameters(boolean isBrief, Map unitToName)
        { return super.getParameters(isBrief, unitToName) + " " + argCount; }

    public int getArgCount() { return argCount; }
    public void setArgCount(int x) { argCount = x; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseInterfaceInvokeInst(this);
    }   
}
