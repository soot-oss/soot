package soot.dava.toolkits.base.misc;

import soot.jimple.*;
import soot.grimp.internal.*;

public class ConditionFlipper
{
    public static ConditionExpr flip( ConditionExpr ce)
    {
	if (ce instanceof EqExpr) 
	    return new GNeExpr( ce.getOp1(), ce.getOp2());

	if (ce instanceof NeExpr)
	    return new GEqExpr( ce.getOp1(), ce.getOp2());

	if (ce instanceof GtExpr)
	    return new GLeExpr( ce.getOp1(), ce.getOp2());

	if (ce instanceof LtExpr)
	    return new GGeExpr( ce.getOp1(), ce.getOp2());

	if (ce instanceof GeExpr)
	    return new GLtExpr( ce.getOp1(), ce.getOp2());

	if (ce instanceof LeExpr)
	    return new GGtExpr( ce.getOp1(), ce.getOp2());

	return null;
    }
}
