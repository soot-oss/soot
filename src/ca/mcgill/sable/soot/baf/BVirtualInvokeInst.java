package ca.mcgill.sable.soot.baf;

import ca.mcgill.sable.soot.*;
import java.util.*;

public class BVirtualInvokeInst extends AbstractInvokeInst implements VirtualInvokeInst
{
    BVirtualInvokeInst(SootMethod method) { setMethod(method); }
    final String getName() { return "virtualinvoke"; }
}
