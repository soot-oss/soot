package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcc_src_fld_dstc_dstTrad extends Qsrcc_src_fld_dstc_dst {
    public Qsrcc_src_fld_dstc_dstTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _srcc, VarNode _src, PaddleField _fld, Context _dstc, VarNode _dst) {
        q.add(_srcc);
        q.add(_src);
        q.add(_fld);
        q.add(_dstc);
        q.add(_dst);
        invalidate();
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { fld.v(), srcc.v(), dst.v(), dstc.v(), src.v() },
                                              new PhysicalDomain[] { FD.v(), C1.v(), V2.v(), C2.v(), V1.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk-save" +
                                               "d/src/soot/jimple/paddle/queue/Qsrcc_src_fld_dstc_dstTrad.je" +
                                               "dd:42,22-24"),
                                              in).iterator(new Attribute[] { srcc.v(), src.v(), fld.v(), dstc.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 5; i++) {
                add((Context) tuple[0],
                    (VarNode) tuple[1],
                    (PaddleField) tuple[2],
                    (Context) tuple[3],
                    (VarNode) tuple[4]);
            }
        }
    }
    
    public Rsrcc_src_fld_dstc_dst reader(String rname) {
        return new Rsrcc_src_fld_dstc_dstTrad(q.reader(), name + ":" + rname);
    }
}
