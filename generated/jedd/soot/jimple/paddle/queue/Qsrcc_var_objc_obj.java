package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrcc_var_objc_obj {
    public Qsrcc_var_objc_obj(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _srcc, VarNode _var, Context _objc, AllocNode _obj);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrcc_var_objc_obj reader(String rname);
    
    public Rsrcc_var_objc_obj revreader(String rname) { return this.reader(rname); }
    
    public void add(Rsrcc_var_objc_obj.Tuple in) { this.add(in.srcc(), in.var(), in.objc(), in.obj()); }
}
