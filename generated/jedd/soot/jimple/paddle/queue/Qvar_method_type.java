package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvar_method_type {
    public Qvar_method_type(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _var, SootMethod _method, Type _type);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar_method_type reader(String rname);
    
    public Rvar_method_type revreader(String rname) { return reader(rname); }
    
    public void add(Rvar_method_type.Tuple in) { add(in.var(), in.method(), in.type()); }
}
