package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;

public class SETTopNode extends SETNode
{
    public SETTopNode( IterableSet body)
    {
	super( body);
	add_SubBody( body);
    }

    public IterableSet get_NaturalExits()
    {
	return new IterableSet();
    }

    public ASTNode emit_AST()
    {
	return new ASTMethodNode( emit_ASTBody( (IterableSet) body2childChain.get( subBodies.get(0))));
    }

    public AugmentedStmt get_EntryStmt()
    {
	return (AugmentedStmt) ((SETNode) body2childChain.get( subBodies.get(0))).get_EntryStmt();
    }

    protected boolean resolve( SETNode parent)
    {
	throw new RuntimeException( "Attempting auto-nest a SETTopNode.");
    }
}
