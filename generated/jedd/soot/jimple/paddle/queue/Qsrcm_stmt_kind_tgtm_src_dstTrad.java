package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qsrcm_stmt_kind_tgtm_src_dstTrad extends Qsrcm_stmt_kind_tgtm_src_dst {
    public Qsrcm_stmt_kind_tgtm_src_dstTrad(String name) { super(name); }
    
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(SootMethod _srcm, Unit _stmt, Kind _kind, SootMethod _tgtm, VarNode _src, VarNode _dst) {
        q.add(_srcm);
        q.add(_stmt);
        q.add(_kind);
        q.add(_tgtm);
        q.add(_src);
        q.add(_dst);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { srcm.v(), tgtm.v(), dst.v(), src.v(), kind.v(), stmt.v() },
                                              new PhysicalDomain[] { T1.v(), T2.v(), V2.v(), V1.v(), FD.v(), ST.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /home/olhotak/soot-t" +
                                               "runk2/src/soot/jimple/paddle/queue/Qsrcm_stmt_kind_tgtm_src_" +
                                               "dstTrad.jedd:42,22-24"),
                                              in).iterator(new Attribute[] { srcm.v(), stmt.v(), kind.v(), tgtm.v(), src.v(), dst.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 6; i++) {
                this.add((SootMethod) tuple[0],
                         (Unit) tuple[1],
                         (Kind) tuple[2],
                         (SootMethod) tuple[3],
                         (VarNode) tuple[4],
                         (VarNode) tuple[5]);
            }
        }
    }
    
    public Rsrcm_stmt_kind_tgtm_src_dst reader(String rname) {
        return new Rsrcm_stmt_kind_tgtm_src_dstTrad(q.reader(), name + ":" + rname);
    }
}
