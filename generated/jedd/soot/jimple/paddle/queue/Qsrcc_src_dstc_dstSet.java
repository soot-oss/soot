package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_dstc_dstSet extends Qsrcc_src_dstc_dst {
    public Qsrcc_src_dstc_dstSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst) {
        invalidate();
        Rsrcc_src_dstc_dst.Tuple in = new Rsrcc_src_dstc_dst.Tuple(_srcc, _src, _dstc, _dst);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dstSet reader = (Rsrcc_src_dstc_dstSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        Rsrcc_src_dstc_dst ret = new Rsrcc_src_dstc_dstSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rsrcc_src_dstc_dst revreader(String rname) {
        Rsrcc_src_dstc_dst ret = new Rsrcc_src_dstc_dstRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
