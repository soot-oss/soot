package soot.jimple.toolkit.invoke;

import soot.*;
import soot.jimple.*;

public class ThrowManager
{
    /** Iterate through the statements in b (starting at the end), returning
     * the last instance of the following pattern:
     *
     *                r928 = new java.lang.NullPointerException;
     *                specialinvoke r928."<init>"();
     *                throw r28;
     *
     * Creates if necessary.
     */
        
    public static Stmt getNullPointerExceptionThrower(JimpleBody b)
    {
        Chain units = b.getUnits();
        
        for (Stmt s = (Stmt)units.getLast(); s != units.getFirst();
             s = (Stmt)units.getPredOf(s))
        {
            if (s instanceof ThrowStmt)
            {
                Value throwee = ((ThrowStmt)s).getOp();
                if (throwee instanceof Constant)
                    continue;

                if (s == units.getFirst())
                    break;
                Stmt prosInvoke = (Stmt)units.getPredOf(s);

                if (!(prosInvoke instanceof InvokeStmt))
                    continue;

                if (prosInvoke == units.getFirst())
                    break;
                Stmt prosNew = (Stmt)units.getPredOf(prosInvoke);

                if (!(prosNew instanceof AssignStmt))
                    continue;

                InvokeExpr ie = (InvokeExpr)((InvokeStmt)prosInvoke).getInvokeExpr();
                if (!(ie instanceof SpecialInvokeExpr))
                    continue;

                if (((SpecialInvokeExpr)ie).getBase() != throwee || 
                    !ie.getMethod().getName().equals("<init>"))
                    continue;

                Value lo = ((AssignStmt)prosNew).getLeftOp();
                Value ro = ((AssignStmt)prosNew).getRightOp();
                if (lo != throwee || !(ro instanceof NewExpr))
                    continue;

                Type newType = ((NewExpr)ro).getBaseType();
                if (!newType.equals(RefType.v("java.lang.NullPointerException")))
                    continue;

                // Whew!
                return prosNew;
            }
        }

        // Create.
        Stmt last = (Stmt)units.getLast();

        Local l = Jimple.v().newLocal("__throwee", RefType.v("java.lang.NullPointerException"));
        b.getLocals().add(l);

        Stmt newStmt = Jimple.v().newAssignStmt
            (l, Jimple.v().newNewExpr(RefType.v("java.lang.NullPointerException")));

        Stmt invStmt = Jimple.v().newInvokeStmt
            (Jimple.v().newSpecialInvokeExpr(l, Scene.v().getMethod("<java.lang.NullPointerException: void <init>()>")));
        
        Stmt throwStmt = Jimple.v().newThrowStmt(l);

        units.insertAfter(newStmt, last);
        units.insertAfter(invStmt, newStmt);
        units.insertAfter(throwStmt, invStmt);
        return newStmt;
    }
}
