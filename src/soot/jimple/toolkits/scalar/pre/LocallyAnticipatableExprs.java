package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

/** An expression's value is <i>locally anticipatable</i> in a basic block <code>b</code> 
 * if it is computed in that block and if moving that computation to the beginning of
 * the block would leave the effect of the block unchanged. 
 * That is, there can be no def's of the operands. */
class LocallyAnticipatableExprs
{
    /* universe is all expressions in the program. */
    public static BoundedFlowSet getAntLocExprsOf(Block b, FlowUniverse uni)
    {
        BoundedFlowSet retVal = new ArrayPackedSet(uni);

        Iterator it = b.iterator();
    
        HashSet defdLocals = new HashSet();

        while (it.hasNext())
        {
            Unit u = (Unit)it.next();

            if (u instanceof AssignStmt)
            {
                boolean fail = false;
                AssignStmt as = (AssignStmt)u;

                Value rhs = as.getRightOp();

                if (!(rhs instanceof Expr))
                    fail = true;

                // screen out for the case where the rhs is previously-defined
                if (!fail)
                {
                    Iterator loIt = rhs.getUseBoxes().iterator();
                    while (loIt.hasNext())
                        if (defdLocals.contains(loIt.next()))
                            fail = true;
                }

                if (!fail)
                    retVal.add(rhs, retVal);
            }

            defdLocals.addAll(u.getDefBoxes());
        }
        return retVal;
    }
}
