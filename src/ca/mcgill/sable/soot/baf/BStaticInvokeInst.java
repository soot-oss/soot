package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import java.util.*;

public class BStaticInvokeInst extends AbstractInvokeInst implements StaticInvokeInst
{
    BStaticInvokeInst(SootMethod method) { setMethod(method); }
    public String getName() { return "staticinvoke"; }
}
