package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrcm_stmt_kind_tgtm_src_dst {
    public Qsrcm_stmt_kind_tgtm_src_dst(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm, VarNode _src, VarNode _dst);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrcm_stmt_kind_tgtm_src_dst reader(String rname);
    
    public Rsrcm_stmt_kind_tgtm_src_dst revreader(String rname) { return this.reader(rname); }
    
    public void add(Rsrcm_stmt_kind_tgtm_src_dst.Tuple in) {
        this.add(in.srcm(), in.stmt(), in.kind(), in.tgtm(), in.src(), in.dst());
    }
}
