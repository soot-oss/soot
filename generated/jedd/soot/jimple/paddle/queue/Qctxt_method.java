package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qctxt_method implements DepItem {
    public Qctxt_method(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _ctxt, SootMethod _method);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rctxt_method reader(String rname);
    
    public Rctxt_method revreader(String rname) { return reader(rname); }
    
    public void add(Rctxt_method.Tuple in) { add(in.ctxt(), in.method()); }
    
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
