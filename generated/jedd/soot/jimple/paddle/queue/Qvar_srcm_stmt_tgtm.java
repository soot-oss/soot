package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qvar_srcm_stmt_tgtm {
    public Qvar_srcm_stmt_tgtm(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _var, SootMethod _srcm, Unit _stmt, SootMethod _tgtm);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rvar_srcm_stmt_tgtm reader(String rname);
    
    public Rvar_srcm_stmt_tgtm revreader(String rname) { return reader(rname); }
    
    public void add(Rvar_srcm_stmt_tgtm.Tuple in) { add(in.var(), in.srcm(), in.stmt(), in.tgtm()); }
}
