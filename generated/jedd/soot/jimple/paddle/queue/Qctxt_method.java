package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qctxt_method {
    public Qctxt_method(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _ctxt, SootMethod _method);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rctxt_method reader(String rname);
    
    public Rctxt_method revreader(String rname) { return this.reader(rname); }
}
