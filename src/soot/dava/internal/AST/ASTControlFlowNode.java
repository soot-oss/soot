package soot.dava.internal.AST;

import soot.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.toolkits.base.AST.*;

public abstract class ASTControlFlowNode extends ASTLabeledNode
{
    protected ValueBox conditionBox;

    public ASTControlFlowNode( SETNodeLabel label, ConditionExpr condition)
    {
	super( label);
        this.conditionBox = Jimple.v().newConditionExprBox(condition);
    }

    public ConditionExpr get_Condition()
    {
	return (ConditionExpr) conditionBox.getValue();
    }

    public void perform_Analysis( ASTAnalysis a)
    {
	ASTWalker.v().walk_value( a, get_Condition());

	if (a instanceof TryContentsFinder) {
	    TryContentsFinder tcf = (TryContentsFinder) a;
	    tcf.v().add_ExceptionSet( this, tcf.v().remove_CurExceptionSet());
	}

	perform_AnalysisOnSubBodies( a);
    }
}
