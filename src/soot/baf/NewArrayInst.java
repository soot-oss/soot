package soot.baf;

import soot.*;

public interface NewArrayInst extends Inst
{
    public Type getBaseType();
    public void setBaseType(Type type);
}
