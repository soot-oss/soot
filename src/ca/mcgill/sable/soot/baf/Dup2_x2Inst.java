
package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;


public interface Dup2_x2Inst extends DupInst
{ 
    public Type getOp1Type();
    public Type getOp2Type();
    public Type getUnder1Type();
    public Type getUnder2Type();
}

