package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvarc_var_objc_obj {
    public Qvarc_var_objc_obj(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _varc, VarNode _var, Context _objc, AllocNode _obj);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvarc_var_objc_obj reader(String rname);
    
    public Rvarc_var_objc_obj revreader(String rname) { return reader(rname); }
    
    public void add(Rvarc_var_objc_obj.Tuple in) { add(in.varc(), in.var(), in.objc(), in.obj()); }
}
