package soot.baf;

import soot.*;

public interface FieldArgInst extends Inst
{
    public SootField getField();
    public void setField(SootField f);
}
