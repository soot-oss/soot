package soot.baf;

import soot.*;

public interface LoadInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type opType);
    public Local getLocal();
    public void setLocal(Local l);
}
