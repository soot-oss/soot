package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;


public interface DupInst extends Inst
{ 
    public List getOpTypes();    
    public List getUnderTypes();
    
}
