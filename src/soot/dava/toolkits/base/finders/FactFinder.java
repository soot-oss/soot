package soot.dava.toolkits.base.finders;

import soot.dava.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.SET.*;

public interface FactFinder
{
    public abstract void find( DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException;
}
