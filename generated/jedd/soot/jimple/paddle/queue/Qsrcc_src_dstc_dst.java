package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrcc_src_dstc_dst implements DepItem {
    public Qsrcc_src_dstc_dst(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrcc_src_dstc_dst reader(String rname);
    
    public Rsrcc_src_dstc_dst revreader(String rname) { return reader(rname); }
    
    public void add(Rsrcc_src_dstc_dst.Tuple in) { add(in.srcc(), in.src(), in.dstc(), in.dst()); }
    
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
