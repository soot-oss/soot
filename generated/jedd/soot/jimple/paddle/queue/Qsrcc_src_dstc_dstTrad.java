package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_src_dstc_dstTrad extends Qsrcc_src_dstc_dst {
    public Qsrcc_src_dstc_dstTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _srcc, VarNode _src, Context _dstc, VarNode _dst) {
        q.add(_srcc);
        q.add(_src);
        q.add(_dstc);
        q.add(_dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { dstc.v(), src.v(), srcc.v(), dst.v() },
                                              new PhysicalDomain[] { C2.v(), V1.v(), C1.v(), V2.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/research/ccl/o" +
                                               "lhota/soot-trunk2/src/soot/jimple/paddle/queue/Qsrcc_src_dst" +
                                               "c_dstTrad.jedd:40,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), src.v(), dstc.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 4; i++) {
                add((Context) tuple[0], (VarNode) tuple[1], (Context) tuple[2], (VarNode) tuple[3]);
            }
        }
    }
    
    public Rsrcc_src_dstc_dst reader(String rname) {
        return new Rsrcc_src_dstc_dstTrad(q.reader(), name + ":" + rname);
    }
}
