package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qobjc_obj_varc_var {
    public Qobjc_obj_varc_var(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _objc, AllocNode _obj, Context _varc, VarNode _var);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Robjc_obj_varc_var reader(String rname);
    
    public Robjc_obj_varc_var revreader(String rname) { return reader(rname); }
    
    public void add(Robjc_obj_varc_var.Tuple in) { add(in.objc(), in.obj(), in.varc(), in.var()); }
}
