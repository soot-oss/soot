package soot.baf;

import ca.mcgill.sable.util.*;
import java.util.*;
import soot.*;

public interface LookupSwitchInst extends Inst
{
    public Unit getDefaultTarget();
    public void setDefaultTarget(Unit defTarget);
    public UnitBox getDefaultTargetBox();

    public void setLookupValue(int index, int value);
    public int getLookupValue(int index);
    public List getLookupValues();
    public void setLookupValues(List values);
    
    public int getTargetCount();
    public Unit getTarget(int index);
    public UnitBox getTargetBox(int index);
    public void setTarget(int index, Unit target);
    public List getTargets();
    public void setTargets(List targets);
}
