package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvar {
    public abstract void add(VarNode _var);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar reader();
    
    public Qvar() { super(); }
}
