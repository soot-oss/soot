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
    private IteratorableSet ifBody, elseBody;

    public SETIfElseNode( AugmentedStmt characterizingStmt, IteratorableSet body, IteratorableSet ifBody, IteratorableSet elseBody) 
    {
	super( characterizingStmt, body);

	this.ifBody = ifBody;
	this.elseBody = elseBody;

	add_SubBody( ifBody);
	add_SubBody( elseBody);
    }

    public IteratorableSet get_NaturalExits()
    {
	IteratorableSet c = new IteratorableSet();

	IteratorableSet ifChain = (IteratorableSet) body2childChain.get( ifBody);
	if (ifChain.isEmpty() == false)
	    c.addAll( ((SETNode) ifChain.getLast()).get_NaturalExits());

	IteratorableSet elseChain = (IteratorableSet) body2childChain.get( elseBody);
	if (elseChain.isEmpty() == false)
	    c.addAll( ((SETNode) elseChain.getLast()).get_NaturalExits());

	return c;
    }

    public ASTNode emit_AST()
    {
	List
	    astBody0 = emit_ASTBody( (IteratorableSet) body2childChain.get( ifBody)),
	    astBody1 = emit_ASTBody( (IteratorableSet) body2childChain.get( elseBody));

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


