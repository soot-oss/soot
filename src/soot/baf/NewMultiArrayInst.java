package soot.baf;

import soot.*;

public interface NewMultiArrayInst extends Inst
{
    public ArrayType getBaseType();
    public void setBaseType(ArrayType type);
    
    public int getDimensionCount();
    public void setDimensionCount(int count);
}
