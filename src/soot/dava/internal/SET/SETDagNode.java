package soot.dava.internal.SET;

import soot.util.*;
import soot.dava.internal.asg.*;

public abstract class SETDagNode extends SETControlFlowNode
{
    public SETDagNode( AugmentedStmt characterizingStmt, IterableSet body)
    {
	super( characterizingStmt, body);
    }

    public AugmentedStmt get_EntryStmt()
    {
	return get_CharacterizingStmt();
    }
}
