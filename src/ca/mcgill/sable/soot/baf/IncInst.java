package ca.mcgill.sable.soot.baf;

import  ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.*;


public interface IncInst extends Inst
{
    
    Constant getConstant() ;
    void setConstant(Constant aConstant); 
    void setLocal(Local l);
    Local getLocal();
    
}

