package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface InstanceOfInst extends Inst
{
    public Type getCheckType();
    public void setCheckType(Type type);
}
