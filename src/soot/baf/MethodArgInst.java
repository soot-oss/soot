package soot.baf;

import soot.*;

public interface MethodArgInst extends Inst
{
    public SootMethod getMethod();
    public void setMethod(SootMethod m);
}
