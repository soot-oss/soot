package soot.baf;

import soot.*;

public interface NewInst extends Inst
{
    public RefType getBaseType();
    public void setBaseType(RefType type);
}
