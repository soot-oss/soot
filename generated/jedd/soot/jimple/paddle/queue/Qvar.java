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
    public Qvar(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _var);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar reader(String rname);
    
    public Rvar revreader(String rname) { return this.reader(rname); }
}
