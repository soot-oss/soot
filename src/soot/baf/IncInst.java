package soot.baf;

import  soot.jimple.*;
import soot.*;


public interface IncInst extends Inst
{
    
    Constant getConstant() ;
    void setConstant(Constant aConstant); 
    void setLocal(Local l);
    Local getLocal();
    
}

