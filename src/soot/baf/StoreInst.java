package soot.baf;

import soot.*;

public interface StoreInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type opType);
    public Local getLocal();
    public void setLocal(Local l);
}
