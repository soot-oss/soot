package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface LoadInst extends Inst
{
    public Type getOpType();
    public void setOpType(Type opType);
    public int getIndex();
    public void setIndex(int index);
}
