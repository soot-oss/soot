package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;

public class SETUnconditionalWhileNode extends SETCycleNode
{
    public SETUnconditionalWhileNode( IteratorableSet body)
    {
	super( (AugmentedStmt) body.getFirst(), body);
	add_SubBody( body);
    }

    public IteratorableSet get_NaturalExits()
    {
	return new IteratorableSet();
    }

    public ASTNode emit_AST()
    {
	return new ASTUnconditionalLoopNode( get_Label(), emit_ASTBody( (IteratorableSet) body2childChain.get( subBodies.get(0))));
    }

    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
