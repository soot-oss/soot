package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.misc.*;

public class SETIfElseNode extends SETDagNode
{
    private IterableSet ifBody, elseBody;

    public SETIfElseNode( AugmentedStmt characterizingStmt, IterableSet body, IterableSet ifBody, IterableSet elseBody) 
    {
	super( characterizingStmt, body);

	this.ifBody = ifBody;
	this.elseBody = elseBody;

	add_SubBody( ifBody);
	add_SubBody( elseBody);
    }

    public IterableSet get_NaturalExits()
    {
	IterableSet c = new IterableSet();

	IterableSet ifChain = (IterableSet) body2childChain.get( ifBody);
	if (ifChain.isEmpty() == false)
	    c.addAll( ((SETNode) ifChain.getLast()).get_NaturalExits());

	IterableSet elseChain = (IterableSet) body2childChain.get( elseBody);
	if (elseChain.isEmpty() == false)
	    c.addAll( ((SETNode) elseChain.getLast()).get_NaturalExits());

	return c;
    }

    public ASTNode emit_AST()
    {
	List
	    astBody0 = emit_ASTBody( (IterableSet) body2childChain.get( ifBody)),
	    astBody1 = emit_ASTBody( (IterableSet) body2childChain.get( elseBody));

	ConditionExpr ce = (ConditionExpr) ((IfStmt) get_CharacterizingStmt().get_Stmt()).getCondition();

	if (astBody0.isEmpty()) {
	    List tbody = astBody0;
	    astBody0 = astBody1;
	    astBody1 = tbody;

	    ce = ConditionFlipper.flip( ce);
	}

	if (astBody1.isEmpty())
	    return new ASTIfNode( get_Label(), ce, astBody0);
	else 
	    return new ASTIfElseNode( get_Label(), ce, astBody0, astBody1);
    }
}


