package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public final class Qsrcc_src_dstc_dst_fldSet extends Qsrcc_src_dstc_dst_fld {
    public Qsrcc_src_dstc_dst_fldSet(String name) { super(name); }
    
    private LinkedList readers = new LinkedList();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst, PaddleField _fld) {
        invalidate();
        Rsrcc_src_dstc_dst_fld.Tuple in = new Rsrcc_src_dstc_dst_fld.Tuple(_srcc, _src, _dstc, _dst, _fld);
        for (Iterator it = readers.iterator(); it.hasNext(); ) {
            Rsrcc_src_dstc_dst_fldSet reader = (Rsrcc_src_dstc_dst_fldSet) it.next();
            reader.add(in);
        }
    }
    
    public void add(final jedd.internal.RelationContainer in) { throw new RuntimeException(); }
    
    public Rsrcc_src_dstc_dst_fld reader(String rname) {
        Rsrcc_src_dstc_dst_fld ret = new Rsrcc_src_dstc_dst_fldSet(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
    
    public Rsrcc_src_dstc_dst_fld revreader(String rname) {
        Rsrcc_src_dstc_dst_fld ret = new Rsrcc_src_dstc_dst_fldRev(name + ":" + rname);
        readers.add(ret);
        return ret;
    }
}
