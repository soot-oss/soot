package soot.dava.internal.AST;

import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

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

    public void perform_Analysis( ASTAnalysis a)
    {
	ASTWalker.v().walk_value( a, condition);

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}

	perform_AnalysisOnSubBodies( a);
    }
}
