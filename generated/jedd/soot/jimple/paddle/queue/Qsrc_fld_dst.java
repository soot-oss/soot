package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public abstract class Qsrc_fld_dst {
    public Qsrc_fld_dst(String name) {
        super();
        this.name = name;
    }
    
    protected String name;
    
    public final String toString() { return name; }
    
    public abstract void add(VarNode _src, PaddleField _fld, VarNode _dst);
    
    public abstract void add(final jedd.internal.RelationContainer in);
    
    public abstract Rsrc_fld_dst reader(String rname);
    
    public Rsrc_fld_dst revreader(String rname) { return reader(rname); }
    
    public void add(Rsrc_fld_dst.Tuple in) { add(in.src(), in.fld(), in.dst()); }
}
