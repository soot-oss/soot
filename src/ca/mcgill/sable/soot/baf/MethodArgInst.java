package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface MethodArgInst extends Inst
{
    public SootMethod getMethod();
    public void setMethod(SootMethod m);
}
