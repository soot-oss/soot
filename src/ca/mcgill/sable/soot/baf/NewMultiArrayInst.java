package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface NewMultiArrayInst extends Inst
{
    public ArrayType getBaseType();
    public void setBaseType(ArrayType type);
    
    public int getDimensionCount();
    public void setDimensionCount(int count);
}
