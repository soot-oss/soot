package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.*;

public interface LookupSwitchInst extends Inst
{
    public Unit getDefaultTarget();
    public void setDefaultTarget(Unit defTarget);

    public List getLookupValues();
    public void setLookupValues(List values);
    
    public List getTargets();
    public void setTargets(List targets);
}
