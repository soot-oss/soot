package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface OpTypeArgInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type t);
}
