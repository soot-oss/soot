package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrc_dst implements DepItem {
    public Qsrc_dst(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _src, VarNode _dst);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrc_dst reader(String rname);
    
    public Rsrc_dst revreader(String rname) { return reader(rname); }
    
    public void add(Rsrc_dst.Tuple in) { add(in.src(), in.dst()); }
    
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
