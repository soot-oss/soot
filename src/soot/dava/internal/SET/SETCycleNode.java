package soot.dava.internal.SET;

import soot.util.*;
import soot.dava.internal.asg.*;

public abstract class SETCycleNode extends SETControlFlowNode
{
    public SETCycleNode( AugmentedStmt characterizingStmt, IteratorableSet body)
    {
	super( characterizingStmt, body);
    }
}
