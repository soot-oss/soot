package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class BStaticInvokeInst extends AbstractInvokeInst implements StaticInvokeInst
{
    BStaticInvokeInst(SootMethod method) { setMethod(method); }


    public int getInCount()
    {
        return getMethod().getParameterCount();
        
    }




    public Object clone() 
    {
        return new  BStaticInvokeInst(getMethod());
    }


    public int getInMachineCount()
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

    public int getOutMachineCount()
    {
        if(getMethod().getReturnType() instanceof VoidType)
            return 0;
        else
            return 1;
    }

    public String getName() { return "staticinvoke"; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseStaticInvokeInst(this);
    }   
}
