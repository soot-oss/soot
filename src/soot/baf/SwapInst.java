
package soot.baf;

import soot.*;
import soot.util.*;
import java.util.*;

public interface SwapInst extends Inst
{
    Type getFromType();
    void setFromType(Type fromType);
    
    Type getToType();
    void setToType(Type toType);
}
