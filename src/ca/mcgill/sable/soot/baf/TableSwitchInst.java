package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public interface TableSwitchInst extends Inst
{
    public Unit getDefaultTarget();
    public void setDefaultTarget(Unit defTarget);
    public UnitBox getDefaultTargetBox();
    
    public int getLowIndex();
    public void setLowIndex(int index);
    
    public int getHighIndex();
    public void setHighIndex(int index);
    
    public List getTargets();
    public Unit getTarget(int index);
    public void setTarget(int index, Unit target);
    public void setTargets(List targets);
    public UnitBox getTargetBox(int index);
}
