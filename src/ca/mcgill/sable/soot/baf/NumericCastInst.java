package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface NumericCastInst extends Inst
{
    public Type getSourceType();
    public void setSourceType(Type t);
    
    public Type getDestType();
    public void setDestType(Type t);
}
