package soot.baf;

import soot.*;

public interface InstanceCastInst extends Inst
{
    public Type getCastType();
    public void setCastType(Type type);
}
