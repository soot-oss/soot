package soot.dava.internal.AST;

import soot.jimple.*;
import soot.dava.internal.SET.*;


public abstract class ASTControlFlowNode extends ASTLabeledNode
{
    private ConditionExpr condition;

    public ASTControlFlowNode( SETNodeLabel label, ConditionExpr condition)
    {
	super( label);
	this.condition = condition;
    }

    public ConditionExpr get_Condition()
    {
	return condition;
    }
}
