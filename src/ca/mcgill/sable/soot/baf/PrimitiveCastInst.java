package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface PrimitiveCastInst extends Inst
{
    public Type getFromType();
    public void setFromType(Type t);
    
    public Type getToType();
    public void setToType(Type t);
}
