package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qctxt_var_obj_srcm_stmt_kind_tgtm implements DepItem {
    public Qctxt_var_obj_srcm_stmt_kind_tgtm(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _ctxt,
                             VarNode _var,
                             AllocNode _obj,
                             SootMethod _srcm,
                             Unit _stmt,
                             Kind _kind,
                             SootMethod _tgtm);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rctxt_var_obj_srcm_stmt_kind_tgtm reader(String rname);
    
    public Rctxt_var_obj_srcm_stmt_kind_tgtm revreader(String rname) { return reader(rname); }
    
    public void add(Rctxt_var_obj_srcm_stmt_kind_tgtm.Tuple in) {
        add(in.ctxt(), in.var(), in.obj(), in.srcm(), in.stmt(), in.kind(), in.tgtm());
    }
    
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
