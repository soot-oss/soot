package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.jimple.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.misc.*;

public class SETWhileNode extends SETCycleNode
{
    public SETWhileNode( AugmentedStmt characterizingStmt, IteratorableSet body)
    {
	super( characterizingStmt, body);

	IteratorableSet subBody = (IteratorableSet) body.clone();
	subBody.remove( characterizingStmt);
	add_SubBody( subBody);
    }

    public IteratorableSet get_NaturalExits()
    {
	IteratorableSet c = new IteratorableSet();

	c.add( get_CharacterizingStmt());
	
	return c;
    }
    
    public ASTNode emit_AST()
    {
	return new ASTWhileNode( get_Label(), 
				 (ConditionExpr) ((IfStmt) get_CharacterizingStmt().get_Stmt()).getCondition(), 
				 emit_ASTBody( (IteratorableSet) body2childChain.get( subBodies.get(0))));
    }
    
    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
