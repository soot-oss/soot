package ca.mcgill.sable.soot.baf.internal;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class BStaticInvokeInst extends AbstractInvokeInst implements StaticInvokeInst
{
    public BStaticInvokeInst(SootMethod method) { setMethod(method); }


    public int getInCount()
    {
        return getMethod().getParameterCount();
        
    }




    public Object clone() 
    {
        return new  BStaticInvokeInst(getMethod());
    }


   
    public int getOutCount()
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
