
package soot.baf;

import soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;


public interface Dup2Inst extends DupInst
{ 
    public Type getOp1Type();
    public Type getOp2Type();
}

