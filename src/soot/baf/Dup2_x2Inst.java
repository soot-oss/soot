
package soot.baf;

import soot.*;
import soot.util.*;
import java.util.*;


public interface Dup2_x2Inst extends DupInst
{ 
    public Type getOp1Type();
    public Type getOp2Type();
    public Type getUnder1Type();
    public Type getUnder2Type();
}

