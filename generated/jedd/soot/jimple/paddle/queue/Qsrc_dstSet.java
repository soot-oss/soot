package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrc_dstSet extends Qsrc_dst {
    public Qsrc_dstSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(VarNode _src, VarNode _dst) {
        invalidate();
        Rsrc_dst.Tuple in = new Rsrc_dst.Tuple(_src, _dst);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrc_dstSet reader = (Rsrc_dstSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rsrc_dst reader(String rname) {
        Rsrc_dst ret = new Rsrc_dstSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rsrc_dst revreader(String rname) {
        Rsrc_dst ret = new Rsrc_dstRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
