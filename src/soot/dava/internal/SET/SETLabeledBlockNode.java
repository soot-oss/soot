package soot.dava.internal.SET;

import java.util.*;
import soot.util.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.AST.*;

public class SETLabeledBlockNode extends SETNode
{
    public SETLabeledBlockNode( IteratorableSet body)
    {
	super( body);
	add_SubBody( body);
    }

    public IteratorableSet get_NaturalExits()
    {
	return ((SETNode) ((IteratorableSet) body2childChain.get( subBodies.get(0))).getLast()).get_NaturalExits();
    }

    public ASTNode emit_AST()
    {
        return new ASTLabeledBlockNode( get_Label(), emit_ASTBody( (IteratorableSet) body2childChain.get( subBodies.get(0))));
    }

    public AugmentedStmt get_EntryStmt()
    {
	return ((SETNode) ((IteratorableSet) body2childChain.get( subBodies.get(0))).getFirst()).get_EntryStmt();
    }

    protected boolean resolve( SETNode parent)
    {
	throw new RuntimeException( "Attempting auto-nest a SETLabeledBlockNode.");
    }
}
