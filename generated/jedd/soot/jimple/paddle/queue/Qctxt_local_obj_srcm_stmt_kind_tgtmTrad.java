package soot.jimple.paddle.queue;

import soot.util.*;
import soot.jimple.paddle.bdddomains.*;
import soot.jimple.paddle.*;
import soot.jimple.toolkits.callgraph.*;
import soot.*;
import soot.util.queue.*;
import jedd.*;
import java.util.*;

public class Qctxt_local_obj_srcm_stmt_kind_tgtmTrad extends Qctxt_local_obj_srcm_stmt_kind_tgtm {
    private ChunkedQueue q = new ChunkedQueue();
    
    public void add(Context _ctxt,
                    Local _local,
                    AllocNode _obj,
                    SootMethod _srcm,
                    Unit _stmt,
                    Kind _kind,
                    SootMethod _tgtm) {
        q.add(_ctxt);
        q.add(_local);
        q.add(_obj);
        q.add(_srcm);
        q.add(_stmt);
        q.add(_kind);
        q.add(_tgtm);
    }
    
    public void add(final jedd.internal.RelationContainer in) {
        Iterator it =
          new jedd.internal.RelationContainer(new Attribute[] { kind.v(), obj.v(), local.v(), ctxt.v(), tgtm.v(), srcm.v(), stmt.v() },
                                              new PhysicalDomain[] { FD.v(), H1.v(), V1.v(), V2.v(), T2.v(), T1.v(), ST.v() },
                                              ("in.iterator(new jedd.Attribute[...]) at /tmp/soot-trunk/src/" +
                                               "soot/jimple/paddle/queue/Qctxt_local_obj_srcm_stmt_kind_tgtm" +
                                               "Trad.jedd:42,22-24"),
                                              in).iterator(new Attribute[] { ctxt.v(), local.v(), obj.v(), srcm.v(), stmt.v(), kind.v(), tgtm.v() });
        while (it.hasNext()) {
            Object[] tuple = (Object[]) it.next();
            for (int i = 0; i < 7; i++) {
                add((Context) tuple[0],
                    (Local) tuple[1],
                    (AllocNode) tuple[2],
                    (SootMethod) tuple[3],
                    (Unit) tuple[4],
                    (Kind) tuple[5],
                    (SootMethod) tuple[6]);
            }
        }
    }
    
    public Rctxt_local_obj_srcm_stmt_kind_tgtm reader() {
        return new Rctxt_local_obj_srcm_stmt_kind_tgtmTrad(q.reader());
    }
    
    public Qctxt_local_obj_srcm_stmt_kind_tgtmTrad() { super(); }
}
