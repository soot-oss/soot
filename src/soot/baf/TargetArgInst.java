package soot.baf;

import soot.*;

public interface TargetArgInst extends Inst
{
    public Unit getTarget();
    public UnitBox getTargetBox();
    public void setTarget(Unit target);
}
