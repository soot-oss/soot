package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrcc_src_fld_dstc_dst {
    public Qsrcc_src_fld_dstc_dst(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _srcc, VarNode _src, PaddleField _fld, Context _dstc, VarNode _dst);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrcc_src_fld_dstc_dst reader(String rname);
    
    public Rsrcc_src_fld_dstc_dst revreader(String rname) { return this.reader(rname); }
    
    public void add(Rsrcc_src_fld_dstc_dst.Tuple in) { this.add(in.srcc(), in.src(), in.fld(), in.dstc(), in.dst()); }
}
