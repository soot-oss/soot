
package soot.baf;

import soot.*;
import soot.util.*;
import java.util.*;


public interface Dup2Inst extends DupInst
{ 
    public Type getOp1Type();
    public Type getOp2Type();
}

