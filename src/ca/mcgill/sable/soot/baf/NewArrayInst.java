package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface NewArrayInst extends Inst
{
    public Type getBaseType();
    public void setBaseType(Type type);
}
