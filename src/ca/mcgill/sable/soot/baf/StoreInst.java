package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface StoreInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type opType);
    public Local getLocal();
    public void setLocal(Local l);
}
