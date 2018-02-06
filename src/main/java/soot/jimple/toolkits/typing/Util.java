package soot.jimple.toolkits.typing;

import soot.Body;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;

public class Util {
    
    /**
     * A new "normal" statement cannot be inserted in the middle of special
     * "identity statements" (a = @parameter or b = @this in Jimple).
     * 
     * This method returns the last "identity statement" of the method.
     * @param b
     * @param s
     * @return
     */
    public static Unit findLastIdentityUnit(Body b, Stmt s) {
        Unit u2 = s;
        Unit u1 = s;
        while (u1 instanceof IdentityStmt) {
            u2 = u1;
            u1 = b.getUnits().getSuccOf(u1);
        }
        return u2;
    }
    
    /**
     * Returns the first statement after all the "identity statements".
     * @param b
     * @param s
     * @return
     */
    public static Unit findFirstNonIdentityUnit(Body b, Stmt s) {
        Unit u1 = s;
        while (u1 instanceof IdentityStmt)
            u1 = b.getUnits().getSuccOf(u1);
        return u1;
    }

}
