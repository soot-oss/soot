package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;

public class SETDoWhileNode extends SETCycleNode
{
    private AugmentedStmt entryPoint;

    public SETDoWhileNode( AugmentedStmt characterizingStmt, AugmentedStmt entryPoint, IterableSet body)
    {
	super( characterizingStmt, body);

	this.entryPoint = entryPoint;

	IterableSet subBody = (IterableSet) body.clone();
	subBody.remove( characterizingStmt);
	add_SubBody( subBody);
    }

    public IterableSet get_NaturalExits()
    {
	IterableSet c = new IterableSet();

	c.add( get_CharacterizingStmt());

	return c;
    }

    public ASTNode emit_AST()
    {
	return new ASTDoWhileNode( get_Label(), 
				   (ConditionExpr) ((IfStmt) get_CharacterizingStmt().get_Stmt()).getCondition(), 
				   emit_ASTBody( (IterableSet) body2childChain.get( subBodies.get(0))));
    }

    public AugmentedStmt get_EntryStmt()
    {
	return entryPoint;
    }
}
