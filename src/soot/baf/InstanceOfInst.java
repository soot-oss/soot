package soot.baf;

import soot.*;

public interface InstanceOfInst extends Inst
{
    public Type getCheckType();
    public void setCheckType(Type type);
}
