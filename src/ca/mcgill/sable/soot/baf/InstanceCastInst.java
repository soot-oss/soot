package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface InstanceCastInst extends Inst
{
    public Type getCastType();
    public void setCastType(Type type);
}
