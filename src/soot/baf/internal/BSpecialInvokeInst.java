package soot.baf.internal;

import soot.*;
import soot.baf.*;
import soot.util.*;
import java.util.*;

public class BSpecialInvokeInst extends AbstractInvokeInst implements SpecialInvokeInst
{
    public BSpecialInvokeInst(SootMethod method) { setMethod(method); }

    public int getInCount()
    {
        
        return getMethod().getParameterCount() +1;
    }


    public int getInMachineCount()
    {
        return super.getInMachineCount() +1;        
    }


    public Object clone() 
    {
        return new  BSpecialInvokeInst(getMethod());
    }


    
    public int getOutCount()
    {
        if(getMethod().getReturnType() instanceof VoidType)
            return 0;
        else
            return 1;
    }

    

    public String getName() { return "specialinvoke"; }

    public void apply(Switch sw)
    {
        ((InstSwitch) sw).caseSpecialInvokeInst(this);
    }
}
