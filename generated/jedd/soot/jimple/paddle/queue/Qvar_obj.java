package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvar_obj {
    public Qvar_obj(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _var, AllocNode _obj);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar_obj reader(String rname);
    
    public Rvar_obj revreader(String rname) { return reader(rname); }
    
    public void add(Rvar_obj.Tuple in) { add(in.var(), in.obj()); }
}
