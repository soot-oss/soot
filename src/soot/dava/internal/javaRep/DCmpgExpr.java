package soot.dava.internal.javaRep;

import soot.*;
import soot.util.*;
import soot.grimp.*;
import soot.jimple.*;
import soot.grimp.internal.*;

public class DCmpgExpr extends AbstractGrimpIntBinopExpr implements CmpgExpr
{
    public DCmpgExpr(Value op1, Value op2) { super(op1, op2); }
    public final String getSymbol() { return " - "; }
    public final int getPrecedence() { return 700; }
    public void apply(Switch sw) { ((ExprSwitch) sw).caseCmpgExpr(this); }

    public Object clone() 
    {
        return new DCmpgExpr(Grimp.cloneIfNecessary(getOp1()), Grimp.cloneIfNecessary(getOp2()));
    }
}
