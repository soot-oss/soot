package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import java.util.*;

public class BSpecialInvokeInst extends AbstractInvokeInst implements SpecialInvokeInst
{
    BSpecialInvokeInst(SootMethod method) { setMethod(method); }
    public String getName() { return "specialinvoke"; }
}
