package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface NewInst extends Inst
{
    public RefType getBaseType();
    public void setBaseType(RefType type);
}
