package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_dstc_dstNumTrace extends Qsrcc_src_dstc_dst {
    public Qsrcc_src_dstc_dstNumTrace(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst) {
        Rsrcc_src_dstc_dst.Tuple in = new Rsrcc_src_dstc_dst.Tuple(_srcc, _src, _dstc, _dst);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dstNumTrace reader = (Rsrcc_src_dstc_dstNumTrace) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        Rsrcc_src_dstc_dst ret = new Rsrcc_src_dstc_dstNumTrace(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
