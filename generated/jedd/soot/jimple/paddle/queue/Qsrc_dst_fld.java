package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrc_dst_fld implements DepItem {
    public Qsrc_dst_fld(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _src, VarNode _dst, PaddleField _fld);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrc_dst_fld reader(String rname);
    
    public Rsrc_dst_fld revreader(String rname) { return reader(rname); }
    
    public void add(Rsrc_dst_fld.Tuple in) { add(in.src(), in.dst(), in.fld()); }
    
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
