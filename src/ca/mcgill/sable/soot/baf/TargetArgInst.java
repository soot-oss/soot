package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;

public interface TargetArgInst extends Inst
{
    public Unit getTarget();
    public UnitBox getTargetBox();
    public void setTarget(Unit target);
}
