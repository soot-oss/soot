package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BVirtualInvokeInst extends AbstractInvokeInst implements VirtualInvokeInst
{
    public BVirtualInvokeInst(SootMethod method) { setMethod(method); }
    
    public int getInCount()
    {
        return getMethod().getParameterCount() + 1;
        
    }


    public int getInMachineCount()
    {
        return super.getInMachineCount() + 1;
        
    }


    public Object clone() 
    {
        return new  BVirtualInvokeInst(getMethod());
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
            return JasminClass.sizeOfType(getMethod().getReturnType());
    } 

    

final public String getName() { return "virtualinvoke"; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseVirtualInvokeInst(this);
    }   


}







