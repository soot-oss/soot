
package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public interface SwapInst extends Inst
{
    Type getFromType();
    void setFromType(Type fromType);
    
    Type getToType();
    void setToType(Type toType);
}
