package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qobj_method_type implements DepItem {
    public Qobj_method_type(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(AllocNode _obj, SootMethod _method, Type _type);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Robj_method_type reader(String rname);
    
    public Robj_method_type revreader(String rname) { return reader(rname); }
    
    public void add(Robj_method_type.Tuple in) { add(in.obj(), in.method(), in.type()); }
    
    private boolean valid = true;
    
    public boolean update() {
        boolean ret = !valid;
        valid = true;
        return true;
    }
    
    public void invalidate() {
        if (!valid) return;
        valid = false;
        PaddleScene.v().depMan.invalidate(this);
    }
}
