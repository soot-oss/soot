package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface FieldArgInst extends Inst
{
    public SootField getField();
    public void setField(SootField f);
}
