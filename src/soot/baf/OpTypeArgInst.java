package soot.baf;

import soot.*;

public interface OpTypeArgInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type t);
}
