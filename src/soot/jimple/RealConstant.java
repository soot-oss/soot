package soot.jimple;

import soot.*;
import soot.util.*;
import java.util.*;

public abstract class RealConstant extends NumericConstant
{
    // PTC 1999/06/28
    public abstract IntConstant cmpl(RealConstant c);

    public abstract IntConstant cmpg(RealConstant c);
}
