package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public interface TableSwitchInst extends Inst
{
    public Unit getDefaultTarget();
    public void setDefaultTarget(Unit defTarget);
    
    public int getLowIndex();
    public void setLowIndex(int index);
    
    public int getHighIndex();
    public void setHighIndex(int index);
    
    public List getTargets();
    public void setTargets(List targets);
}
